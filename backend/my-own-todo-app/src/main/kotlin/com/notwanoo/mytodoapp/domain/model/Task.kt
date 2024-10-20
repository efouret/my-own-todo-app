package com.notwanoo.mytodoapp.domain.model

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

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
    fun finish(clock: Clock = Clock.System): Task {
        if (recurringPeriod == null) {
            return this.copy(
                status = Status.Done,
                updatedAt = clock.now(),
                history = history + TaskHistory(this.copy(history = emptyList())),
            )
        }

        val now = clock.now()
        val timeZone = TimeZone.UTC

        val newDueDate = if (recurringPeriod.onCompletion) {
            now.plus(recurringPeriod.period, timeZone)
        } else {
            dueDate?.plus(recurringPeriod.period, timeZone)
        }

        return this.copy(
            dueDate = newDueDate,
            updatedAt = clock.now(),
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
