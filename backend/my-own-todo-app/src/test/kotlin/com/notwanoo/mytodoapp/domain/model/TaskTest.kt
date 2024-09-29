package com.notwanoo.mytodoapp.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.*

class TaskTest : FunSpec({
    context("finish") {
        test("should correctly set new dueDate when onCompletion is true") {
            val task = Task(
                id = "task-1",
                title = "Task 1",
                dueDate = Clock.System.now(),
                recurringPeriod = Task.RecurringPeriod(
                    period = DatePeriod.parse("P1Y"),
                    onCompletion = true,
                ),
            )

            val finishedTask = task.finish()

            finishedTask.dueDate?.epochSeconds shouldBe Clock.System.now()
                .plus(1, DateTimeUnit.YEAR, TimeZone.UTC).epochSeconds
        }
    }
})