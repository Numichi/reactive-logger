package io.github.numichi.reactive.logger.spring.handler

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.reactor.MDCContext
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView
import reactor.util.function.Tuple2
import java.util.function.Function

class ContentHandlerReactive(val contextKey: Any) {
    fun modify(context: Context, data: Map<String, String?>): Context {
        return MDCContext.modify(context, contextKey, data)
    }

    fun modify(context: Context, data: Tuple2<String, String?>): Context {
        return MDCContext.modify(context, contextKey, data)
    }

    fun modify(context: Context, data: Pair<String, String?>): Context {
        return MDCContext.modify(context, contextKey, data)
    }

    fun modify(context: Context, data: MDC): Context {
        return MDCContext.modify(context, contextKey, data.data)
    }

    fun modify(context: Context, func: Function<MDC, MDC>): Context {
        return MDCContext.modify(context, contextKey, func)
    }

    fun read(): Mono<MDC> {
        return MDCContext.read(contextKey)
    }

    fun read(contextView: ContextView): MDC {
        return MDCContext.read(contextView, contextKey)
    }

    fun readOrDefault(): Mono<MDC> {
        return MDCContext.readOrDefault(contextKey)
    }

    fun readOrDefault(contextView: ContextView): MDC {
        return MDCContext.readOrDefault(contextView, contextKey)
    }

    fun snapshot(): Mono<MDC> {
        return MDCContext.snapshot(contextKey)
    }

    fun snapshot(contextView: ContextView): MDC {
        return MDCContext.snapshot(contextView, contextKey)
    }
}