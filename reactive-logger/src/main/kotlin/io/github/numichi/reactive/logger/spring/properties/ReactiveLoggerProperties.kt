package io.github.numichi.reactive.logger.spring.properties

import io.github.numichi.reactive.logger.SchedulerOptions
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "reactive-logger")
data class ReactiveLoggerProperties(
    var forceUse: Boolean = false,
    var contextKey: String? = null,
    var scheduler: SchedulerOptions? = null,
)