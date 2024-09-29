package com.notwanoo.mytodoapp.domain.usecase

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject

@ApplicationScoped
class CreateTaskUseCase {

    @Inject
    @field: Default
    lateinit var taskRepository: TaskRepository

    fun createTask(task: Task) {
        taskRepository.save(task)
    }
}