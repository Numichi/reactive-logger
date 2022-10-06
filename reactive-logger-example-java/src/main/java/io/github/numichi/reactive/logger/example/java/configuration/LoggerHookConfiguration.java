package io.github.numichi.reactive.logger.example.java.configuration;

import io.github.numichi.reactive.logger.hook.MDCHook;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class LoggerHookConfiguration {
    
    /**
     * Runs for all context keys.
     */
    @Bean
    @ConditionalOnClass(TraceContext.class)
    public MDCHook<TraceContext> traceContextHook() {
        return new MDCHook<>("traceContextHook", TraceContext.class, (traceContext, mdc) -> {
            Objects.requireNonNull(traceContext, "Trace data cannot be added to MDC if the information is missing!");
    
            Map<String, String> result = new HashMap<>();
            result.put("traceId", traceContext.traceId());
            result.put("spanId", traceContext.spanId());
            result.put("parentId", traceContext.parentId());
            result.put(null, null); // it will be removed because key is null
            
            return result;
        });
    }
    
    /**
     * This hook only has an effect on MDC information when context key is "another-context-key-from-yml".
     */
    @Bean
    @ConditionalOnClass(TraceContext.class)
    public MDCHook<TraceContext> anotherTraceContextHook() {
        return new MDCHook<>("anotherTraceContextHook", TraceContext.class, (traceContext, mdc) -> {
            Objects.requireNonNull(traceContext, "Trace data cannot be added to MDC if the information is missing!");
            
            if (!mdc.getContextKey().equals("another-context-key-from-yml")) {
                throw new IllegalStateException("Context key must be \"another-context-key-from-yml\"!");
            }
            
            return Map.of("TraceContext.hashCode", String.valueOf(traceContext.hashCode()));
        });
    }
}
