package com.notwanoo.mytodoapp.infrastructure.model

import com.notwanoo.mytodoapp.common.serialization.InstantSerializer
import com.notwanoo.mytodoapp.common.serialization.NullableInstantSerializer
import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.model.Task.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.dizitart.no2.repository.annotations.Entity
import org.dizitart.no2.repository.annotations.Id

@Entity
@Serializable
data class TaskDb(
    @Id val id: String,
    val title: String,
    val description: String? = null,
    val status: Status = Status.Todo,
    @Serializable(with = NullableInstantSerializer::class)
    val dueDate: Instant? = null,
    val recurringPeriod: RecurringPeriodDb? = null,
    val comments: List<CommentDb> = emptyList(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Clock.System.now(),
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant = Clock.System.now(),
    val history: List<TaskHistoryDb> = emptyList(),
    val tags: Set<String> = emptySet(),
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        status = status,
        dueDate = dueDate,
        recurringPeriod = recurringPeriod?.toDomain(),
        comments = comments.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        history = history.map { it.toDomain() },
        tags = tags,
    )

    companion object {
        fun fromDomain(task: Task): TaskDb = TaskDb(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status,
            dueDate = task.dueDate,
            recurringPeriod = task.recurringPeriod?.let { RecurringPeriodDb.fromDomain(it) },
            comments = task.comments.map { CommentDb.fromDomain(it) },
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            history = task.history.map { TaskHistoryDb.fromDomain(it) },
            tags = task.tags,
        )
    }

    @Serializable
    data class RecurringPeriodDb(
        val period: String,
        val onCompletion: Boolean = true,
    ) {
        fun toDomain(): RecurringPeriod = RecurringPeriod(DatePeriod.parse(period), onCompletion)

        companion object {
            fun fromDomain(recurringPeriod: RecurringPeriod): RecurringPeriodDb =
                RecurringPeriodDb(recurringPeriod.period.toString(), recurringPeriod.onCompletion)
        }
    }

    @Serializable
    data class CommentDb(
        val content: String,
        @Serializable(with = InstantSerializer::class)
        val createdAt: Instant = Clock.System.now(),
        @Serializable(with = InstantSerializer::class)
        val updatedAt: Instant = Clock.System.now(),
    ) {
        fun toDomain(): Comment = Comment(content)

        companion object {
            fun fromDomain(comment: Comment): CommentDb = CommentDb(comment.content)
        }
    }

    @Serializable
    data class TaskHistoryDb(
        val previousState: TaskDb,
    ) {
        fun toDomain(): TaskHistory = TaskHistory(previousState.toDomain())

        companion object {
            fun fromDomain(taskHistory: TaskHistory): TaskHistoryDb =
                TaskHistoryDb(fromDomain(taskHistory.previousState))
        }
    }
}