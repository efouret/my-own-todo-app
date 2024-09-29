package com.notwanoo.mytodoapp.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: Status = Status.Todo,
    val dueDate: Instant? = null,
    val recurringPeriod: RecurringPeriod? = null,
    val comments: List<Comment> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val history: List<TaskHistory> = emptyList(),
    val tags: Set<String> = emptySet(),
) {
    fun finish(): Task {
        if (recurringPeriod == null) {
            return this.copy(
                status = Status.Done,
                updatedAt = Clock.System.now(),
                history = history + TaskHistory(this.copy(history = emptyList())),
            )
        }

        return this.copy(
            dueDate = if (recurringPeriod.onCompletion) Clock.System.now()
                .plus(recurringPeriod.period.toDuration()) else dueDate?.plus(
                recurringPeriod.period.toDuration()
            ),
            updatedAt = Clock.System.now(),
            history = history + TaskHistory(this.copy(history = emptyList())),
        )
    }

    enum class Status {
        Todo,
        Done,
        Deleted,
    }

    @Serializable
    data class RecurringPeriod(
        val period: DatePeriod,
        val onCompletion: Boolean = true,
    )

    @Serializable
    data class Comment(
        val content: String,
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now(),
    )

    @Serializable
    data class TaskHistory(
        val previousState: Task,
    )
}

//FIXME handle leap years
fun DatePeriod.toDuration() = ((years * 365) + days).days