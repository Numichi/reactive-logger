package io.github.numichi.reactive.logger.spring.properties

import io.github.numichi.reactive.logger.SchedulerOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "reactive-logger")
data class ReactiveLoggerProperties(
    var contextKey: String? = null,
    var scheduler: SchedulerOptions? = null,
    var instances: Map<String, InstanceProperties> = mapOf()
)