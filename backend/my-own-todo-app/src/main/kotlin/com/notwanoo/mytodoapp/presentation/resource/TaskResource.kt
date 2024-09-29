package com.notwanoo.mytodoapp.presentation.resource

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.usecase.CreateTaskUseCase
import com.notwanoo.mytodoapp.domain.usecase.DeleteTaskUseCase
import com.notwanoo.mytodoapp.domain.usecase.FinishTaskUseCase
import com.notwanoo.mytodoapp.domain.usecase.RetrieveTaskUseCase
import com.notwanoo.mytodoapp.presentation.api.CreateTaskRequest
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.*
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
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
}