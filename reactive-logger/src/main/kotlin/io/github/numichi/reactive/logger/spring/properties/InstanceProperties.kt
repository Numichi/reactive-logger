package io.github.numichi.reactive.logger.spring.properties

import io.github.numichi.reactive.logger.SchedulerOptions

data class InstanceProperties(
    var logger: String? = null,
    var contextKey: String? = null,
    var scheduler: SchedulerOptions? = null
)