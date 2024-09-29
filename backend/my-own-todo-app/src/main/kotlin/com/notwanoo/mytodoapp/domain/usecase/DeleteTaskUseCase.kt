package com.notwanoo.mytodoapp.domain.usecase

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject

@ApplicationScoped
class DeleteTaskUseCase {

    @Inject
    @field: Default
    lateinit var taskRepository: TaskRepository

    fun deleteTask(id: String) {
        taskRepository.update(
            taskRepository.findById(id)?.copy(status = Task.Status.Deleted)
                ?: throw NoSuchElementException("Task with id $id not found")
        )
    }

}
