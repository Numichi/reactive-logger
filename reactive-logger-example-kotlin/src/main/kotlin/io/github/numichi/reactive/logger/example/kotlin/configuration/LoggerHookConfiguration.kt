package io.github.numichi.reactive.logger.example.kotlin.configuration

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.example.kotlin.model.ExampleModel
import io.github.numichi.reactive.logger.hook.MDCContextHook
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.util.context.ContextView

@Configuration
class LoggerHookConfiguration {

    @Bean
    fun mdcContextHook(): MDCContextHook {
        return MDCContextHook { ctx: ContextView, mdc: MDC ->
            val result = mutableMapOf<String, String?>()

            ctx[ExampleModel::class.java].also {
                result["example"] = it.value
            }

            if (mdc.contextKey == "context-key-1") {
                result["example"] = "n/a"
            }

            result
        }
    }
}