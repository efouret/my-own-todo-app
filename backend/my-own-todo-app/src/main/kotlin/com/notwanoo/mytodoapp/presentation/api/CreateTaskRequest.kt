package com.notwanoo.mytodoapp.presentation.api

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.model.Task.Comment
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val dueDate: Instant? = null,
    val recurringPeriod: RecurringPeriod? = null,
    val comments: List<Comment> = emptyList(),
    val tags: Set<String> = emptySet(),
) {
    fun toDomain(id: String): Task = Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        recurringPeriod = recurringPeriod?.toDomain(),
        comments = comments,
        tags = tags,
    )

    @Serializable
    data class RecurringPeriod(
        val period: String,
        val onCompletion: Boolean = true,
    ) {
        fun toDomain(): Task.RecurringPeriod = Task.RecurringPeriod(DatePeriod.parse(period), onCompletion)
    }
}