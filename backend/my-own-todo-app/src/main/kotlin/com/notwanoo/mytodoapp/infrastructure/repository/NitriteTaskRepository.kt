package com.notwanoo.mytodoapp.infrastructure.repository

import com.notwanoo.mytodoapp.domain.model.Task
import com.notwanoo.mytodoapp.domain.repository.TaskRepository
import com.notwanoo.mytodoapp.infrastructure.model.TaskDb
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.Nitrite
import org.dizitart.no2.common.module.NitriteModule.module
import org.dizitart.no2.mvstore.MVStoreModule
import org.eclipse.microprofile.config.ConfigProvider
import org.jboss.logging.Logger

private val logger = Logger.getLogger(NitriteTaskRepository::class.java)

@ApplicationScoped
class NitriteTaskRepository : TaskRepository {
    private lateinit var db: Nitrite

    fun onStart(@Observes event: StartupEvent) {
        val config = ConfigProvider.getConfig()

        val dbFilePath = config.getValue("db.nitrite.file", String::class.java)
        logger.info("Using database file: $dbFilePath")

        db = nitrite(
            config.getValue("db.nitrite.user", String::class.java),
            config.getValue("db.nitrite.password", String::class.java)
        ) {
            loadModule(MVStoreModule.withConfig().filePath(dbFilePath).autoCommit(true).build())
            loadModule(module(KotlinXSerializationMapper()))
        }
    }

    fun onStop(@Observes event: ShutdownEvent) {
        db.close()
    }

    override fun save(task: Task) {
        db.getRepository<TaskDb> {
            insert(TaskDb.fromDomain(task))
        }
    }

    override fun findById(id: String): Task? {
        return db.getRepository<TaskDb>().getById(id)?.toDomain()
    }

    override fun update(updatedTask: Task) {
        db.getRepository<TaskDb> {
            update(TaskDb.fromDomain(updatedTask))
        }
    }

    override fun findAllByStatus(status: Task.Status): List<Task> =
        db.getRepository<TaskDb>().find(TaskDb::status eq status.name).toList().map { it.toDomain() }

    override fun delete(id: String) {
        db.getRepository<TaskDb> {
            remove(TaskDb::id eq id)
        }
    }
}