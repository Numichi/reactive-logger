package io.github.numichi.reactive.logger.spring.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "reactive-logger")
data class ReactiveLoggerProperties(
    var instances: Map<String, InstanceProperties> = mapOf()
)