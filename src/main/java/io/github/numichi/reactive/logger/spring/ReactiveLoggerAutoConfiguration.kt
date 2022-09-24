package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(value = [ReactiveLoggerProperties::class])
open class ReactiveLoggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun loggerRegistry(properties: ReactiveLoggerProperties): LoggerRegistry {
        return LoggerRegistryImpl(properties.instances)
    }
}