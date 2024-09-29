package com.notwanoo.mytodoapp.presentation.resource

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.usecase.*
import com.notwanoo.mytodoapp.presentation.api.CreateTaskRequest
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.net.URI
import java.util.*

private val logger = Logger.getLogger(TaskResource::class.java)

@Path("/api/tasks")
class TaskResource {
    @Inject
    @field: Default
    lateinit var createTaskUseCase: CreateTaskUseCase

    @Inject
    @field: Default
    lateinit var retrieveTaskUseCase: RetrieveTaskUseCase

    @Inject
    @field: Default
    lateinit var finishTaskUseCase: FinishTaskUseCase

    @Inject
    @field: Default
    lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @Inject
    @field: Default
    lateinit var importTasksUseCase: ImportTasksUseCase

    @POST
    @Consumes("application/json")
    fun createTask(createTaskRequest: CreateTaskRequest): RestResponse<Unit> {
        logger.info("Creating task with title: ${createTaskRequest.title}")
        val id = "task-${UUID.randomUUID()}"
        createTaskUseCase.createTask(createTaskRequest.toDomain(id))
        return RestResponse.created(URI.create("/tasks/$id"))
    }

    @GET
    fun getTasks(
        @QueryParam("status") @DefaultValue("Todo") status: String = Task.Status.Todo.toString(),
    ): List<Task> {
        logger.info("Getting tasks for status $status")
        return retrieveTaskUseCase.getAllByStatus(Task.Status.valueOf(status))
    }

    @GET
    @Path("/{id}")
    fun getTask(id: String): Task? {
        logger.info("Getting task with id: $id")
        return retrieveTaskUseCase.getById(id)
    }

    @PUT
    @Path("/{id}/done")
    fun markTaskAsDone(id: String): RestResponse<Unit> {
        logger.info("Marking task with id: $id as done")
        finishTaskUseCase.finishTask(id)
        return RestResponse.ok()
    }

    @DELETE
    @Path("/{id}")
    fun deleteTask(id: String): RestResponse<Unit> {
        logger.info("Deleting task with id: $id")
        deleteTaskUseCase.deleteTask(id)
        return RestResponse.noContent()
    }

    @POST
    @Path("/import-csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun import(@RestForm("csv") csv: FileUpload): RestResponse<Unit> {
        logger.info("Importing tasks from CSV file")

        val tasksToImport = mutableListOf<Task>()

        csvReader().readAll(csv.uploadedFile().toFile()).drop(1).forEach { line ->
            // TYPE, CONTENT, DESCRIPTION, PRIORITY, INDENT, AUTHOR, RESPONSIBLE, DATE, DATE_LANG, TIMEZONE, DURATION, DURATION_UNIT
            println(line)

            if (line[0] == "note") {
                val lastTask = tasksToImport.removeLast()
                val updatedTask = lastTask.copy(
                    comments = lastTask.comments + Task.Comment(
                        content = line[1],
                        createdAt = Instant.parse(line[7]),
                    )
                )
                tasksToImport.add(updatedTask)

                println("updated task: $updatedTask")

            } else {

                val task = Task(
                    id = "task-${UUID.randomUUID()}",
                    title = line[1],
                    description = line[2],
                    dueDate = line[7].takeIf { it.isNotBlank() }?.let { Instant.parse(it) },
                    recurringPeriod = line[12].takeIf { it.isNotBlank() }?.let { it.todoistToRecurringPeriod() },
                    comments = emptyList(),
                    tags = emptySet(),
                )

                println("new task: $task")

                tasksToImport.add(task)
            }
        }

        importTasksUseCase.importTasks(tasksToImport)

        return RestResponse.ok()
    }
}

fun String.todoistToRecurringPeriod(): Task.RecurringPeriod {
    // Example: every! 2 weeks -> P2W on completion
    // Example: every 1 year -> P1Y NOT on completion
    val regex = Regex("every(!?)\\s+(\\d*\\s*)(\\w+)")
    val matchResult = regex.find(this) ?: throw IllegalArgumentException("Invalid recurring period: $this")
    val recurOnCompletion = matchResult.groupValues[1].isNotEmpty()
    val count = matchResult.groupValues[2].takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 1
    val unit = when (matchResult.groupValues[3]) {
        "day" -> "D"
        "days" -> "D"
        "week" -> "W"
        "weeks" -> "W"
        "month" -> "M"
        "months" -> "M"
        "year" -> "Y"
        "years" -> "Y"
        else -> throw IllegalArgumentException("Invalid recurring period: $this")
    }
    return Task.RecurringPeriod(DatePeriod.parse("P$count$unit"), recurOnCompletion)
}