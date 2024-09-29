package com.notwanoo.mytodoapp.domain.repository

import com.notwanoo.mytodoapp.domain.model.Task

interface TaskRepository {
    fun save(task: Task)
    fun findById(id: String): Task?
    fun update(updatedTask: Task)
    fun findAllByStatus(status: Task.Status): List<Task>
    fun delete(id: String)
}