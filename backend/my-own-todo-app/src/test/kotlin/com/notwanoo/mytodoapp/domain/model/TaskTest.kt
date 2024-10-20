package com.notwanoo.mytodoapp.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant

class TaskTest : FunSpec({
    context("finish") {
        val fixedClock: Clock = object : Clock {
            override fun now(): Instant = Instant.parse("2024-10-20T18:18:00Z")
        }

        test("should correctly set new dueDate when onCompletion is true") {
            val task = Task(
                id = "task-1",
                title = "Task 1",
                dueDate = fixedClock.now(),
                recurringPeriod = Task.RecurringPeriod(
                    period = DatePeriod.parse("P1Y"),
                    onCompletion = true,
                ),
            )

            val finishedTask = task.finish(fixedClock)

            finishedTask.dueDate?.epochSeconds shouldBe Instant.parse("2025-10-20T18:18:00Z").epochSeconds
        }

        test("should correctly set new dueDate when onCompletion is true") {
            val task = Task(
                id = "task-1",
                title = "Task 1",
                dueDate = fixedClock.now(),
                recurringPeriod = Task.RecurringPeriod(
                    period = DatePeriod.parse("P2M"),
                    onCompletion = true,
                ),
            )

            val finishedTask = task.finish(fixedClock)

            finishedTask.dueDate?.epochSeconds shouldBe Instant.parse("2024-12-20T18:18:00Z").epochSeconds
        }
    }
})