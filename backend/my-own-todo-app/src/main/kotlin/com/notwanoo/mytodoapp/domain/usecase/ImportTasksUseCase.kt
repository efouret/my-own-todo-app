package com.notwanoo.mytodoapp.domain.usecase

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject

@ApplicationScoped
class ImportTasksUseCase {
    @Inject
    @field: Default
    lateinit var taskRepository: TaskRepository

    fun importTasks(tasksToImport: List<Task>) {
        tasksToImport.forEach(taskRepository::save)
    }
}