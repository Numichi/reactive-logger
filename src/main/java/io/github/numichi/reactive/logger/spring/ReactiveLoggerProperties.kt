package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.spring.properties.Default
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "reactive-logger")
data class ReactiveLoggerProperties(
    val default: Default = Default()
)