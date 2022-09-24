package io.github.numichi.reactive.logger.configuration.properties

import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.SchedulerOptions

data class Default(
    val key: String = DEFAULT_REACTOR_CONTEXT_MDC_KEY,
    val scheduler: SchedulerOptions = SchedulerOptions.BOUNDED_ELASTIC
)
