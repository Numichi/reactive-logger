package io.github.numichi.reactive.logger.example.kotlin.configuration

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.example.kotlin.model.ExampleModel
import io.github.numichi.reactive.logger.hook.MDCHook
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerHookConfiguration {

    @Bean
    fun traceContextHook(): MDCHook<ExampleModel> {
        return MDCHook("ExampleModel_1", ExampleModel::class.java) { model: ExampleModel?, _: MDC? ->
            requireNotNull(model)

            mapOf("example" to model.value)
        }
    }

    @Bean
    fun anotherTraceContextHook(): MDCHook<ExampleModel> {
        return MDCHook("ExampleModel_2", ExampleModel::class.java) { model: ExampleModel?, mdc: MDC ->
            requireNotNull(model)
            check(mdc.contextKey == "another-context-key-from-yml")

            mapOf("example" to "n/a")
        }
    }
}