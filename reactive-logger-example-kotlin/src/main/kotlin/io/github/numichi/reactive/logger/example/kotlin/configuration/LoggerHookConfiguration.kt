package io.github.numichi.reactive.logger.example.kotlin.configuration

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.example.kotlin.model.ExampleModel
import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.MDCHook
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.util.context.ContextView

@Configuration
class LoggerHookConfiguration {

    @Bean
    fun mdcContextHook(): MDCContextHook {
        return MDCContextHook label@{ ctx: ContextView, mdc: MDC ->
            val result = mutableMapOf<String, String?>()

            ctx[ExampleModel::class.java].also {
                result["example"] = it.value
            }

            if (mdc.contextKey == "another-context-key-from-yml") {
                result["example"] = "n/a"
            }

            result
        }
    }

    // @Bean
    @Deprecated("See: mdcContextHook()")
    fun traceContextHook(): MDCHook<ExampleModel> {
        return MDCHook("ExampleModel_1", ExampleModel::class.java) { model: ExampleModel?, _: MDC? ->
            requireNotNull(model)

            mapOf("example" to model.value)
        }
    }

    // @Bean
    @Deprecated("See: mdcContextHook()")
    fun anotherTraceContextHook(): MDCHook<ExampleModel> {
        return MDCHook("ExampleModel_2", ExampleModel::class.java) { model: ExampleModel?, mdc: MDC ->
            requireNotNull(model)
            check(mdc.contextKey == "another-context-key-from-yml")

            mapOf("example" to "n/a")
        }
    }
}