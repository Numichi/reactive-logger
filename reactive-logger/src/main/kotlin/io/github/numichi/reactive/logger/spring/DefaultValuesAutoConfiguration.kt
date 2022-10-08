package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration as ReactiveLoggerConfiguration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.spring.properties.ReactiveLoggerProperties
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import reactor.core.scheduler.Schedulers

@Configuration
@EnableConfigurationProperties(value = [ReactiveLoggerProperties::class])
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE)
open class DefaultValuesAutoConfiguration(properties: ReactiveLoggerProperties) {

    companion object {
        @JvmStatic
        fun reset(properties: ReactiveLoggerProperties, force: Boolean) {
            val contextKey = properties.contextKey
            val scheduler = properties.scheduler

            if ((ReactiveLoggerConfiguration.defaultReactorContextMdcKey == DEFAULT_REACTOR_CONTEXT_MDC_KEY && contextKey != null) || (force && contextKey != null)) {
                ReactiveLoggerConfiguration.defaultReactorContextMdcKey = contextKey
            }

            if ((ReactiveLoggerConfiguration.defaultScheduler == Schedulers.boundedElastic() && scheduler != null) || (force && scheduler != null)) {
                ReactiveLoggerConfiguration.setDefaultScheduler(scheduler)
            }
        }
    }

    init {
        reset(properties, false)
    }
}