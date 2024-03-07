package io.github.numichi.reactive.logger

import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

enum class SchedulerOptions {
    BOUNDED_ELASTIC,
    PARALLEL,
    IMMEDIATE,
    SINGLE,
    ;

    fun toScheduler(): Scheduler {
        return when (this) {
            BOUNDED_ELASTIC -> Schedulers.boundedElastic()
            PARALLEL -> Schedulers.parallel()
            IMMEDIATE -> Schedulers.immediate()
            SINGLE -> Schedulers.single()
        }
    }
}
