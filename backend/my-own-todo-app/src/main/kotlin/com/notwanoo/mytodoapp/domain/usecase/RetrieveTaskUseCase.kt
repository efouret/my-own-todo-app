package com.notwanoo.mytodoapp.domain.usecase

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject

@ApplicationScoped
class RetrieveTaskUseCase {
    @Inject
    @field: Default
    lateinit var taskRepository: TaskRepository

    fun getById(id: String): Task? {
        return taskRepository.findById(id)
    }

    fun getAllByStatus(status: Task.Status): List<Task> {
        return taskRepository.findAllByStatus(status)
    }
}