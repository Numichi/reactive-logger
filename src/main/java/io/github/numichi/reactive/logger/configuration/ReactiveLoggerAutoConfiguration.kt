package io.github.numichi.reactive.logger.configuration

import org.springframework.context.annotation.Configuration
import io.github.numichi.reactive.logger.Configuration as RLConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import javax.annotation.PostConstruct

@Configuration
@EnableConfigurationProperties(value = [ReactiveLoggerProperties::class])
open class ReactiveLoggerAutoConfiguration(private val properties: ReactiveLoggerProperties) {

    @PostConstruct
    fun postConstructor() {
        RLConfig.defaultReactorContextMdcKey = properties.default.key
        RLConfig.setDefaultScheduler(properties.default.scheduler)
    }
}