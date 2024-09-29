package com.notwanoo.mytodoapp.domain.usecase

import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject

@ApplicationScoped
class FinishTaskUseCase {

    @Inject
    @field: Default
    lateinit var taskRepository: TaskRepository

    fun finishTask(id: String) {
        val task = taskRepository.findById(id) ?: throw NoSuchElementException("Task with id $id not found")
        val updatedTask = task.finish()
        taskRepository.update(updatedTask)
    }
}