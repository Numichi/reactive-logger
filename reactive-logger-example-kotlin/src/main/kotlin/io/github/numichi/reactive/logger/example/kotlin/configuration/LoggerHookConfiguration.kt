package io.github.numichi.reactive.logger.example.kotlin.configuration

import io.github.numichi.reactive.logger.hook.MDCHook
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.cloud.sleuth.TraceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerHookConfiguration {

    /**
     * Runs for all context keys.
     */
    @Bean
    @ConditionalOnClass(TraceContext::class)
    fun traceContextHook(): MDCHook<TraceContext> {
        return MDCHook("traceContextHook", TraceContext::class.java) { traceContext, _ ->
            requireNotNull(traceContext) { "Trace data cannot be added to MDC if the information is missing!" }

            val map = mutableMapOf<String, String?>()
            map["traceId"] = traceContext.traceId()
            map["spanId"] = traceContext.spanId()
            map["parentId"] = traceContext.parentId()

            map
        }
    }

    /**
     * This hook only has an effect on MDC information when context key is "another-context-key-from-yml".
     */
    @Bean
    @ConditionalOnClass(TraceContext::class)
    fun anotherTraceContextHook(): MDCHook<TraceContext> {
        return MDCHook("anotherTraceContextHook", TraceContext::class.java) { traceContext, mdc ->
            requireNotNull(traceContext) { "Trace data cannot be added to MDC if the information is missing!" }
            check(mdc.contextKey == "another-context-key-from-yml") { "Context key must be \"another-context-key-from-yml\"!" }

            mapOf("TraceContext.hashCode" to traceContext.hashCode().toString())
        }
    }
}