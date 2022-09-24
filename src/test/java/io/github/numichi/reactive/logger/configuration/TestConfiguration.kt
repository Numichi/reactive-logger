package io.github.numichi.reactive.logger.configuration

import io.github.numichi.reactive.logger.hook.MDCHook
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TestConfiguration {

    @Bean
    open fun hook1() = MDCHook<String>("hook1", "") { _, _ -> mapOf() }

    @Bean
    open fun hook2() = MDCHook<Int>("hook2", "") { _, _ -> mapOf() }
}