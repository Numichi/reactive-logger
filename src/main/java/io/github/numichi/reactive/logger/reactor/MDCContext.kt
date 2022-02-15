package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView

object MDCContext {
    @JvmStatic
    fun put(context: Context, vararg mdc: MDC?): Context {
        var newContext = context

        for (m in mdc) {
            if (m == null) {
                continue
            }

            newContext = newContext.put(m.contextKey, m)
        }

        return newContext
    }

    @JvmStatic
    fun read(): Mono<MDC> {
        return read(DefaultValues.getInstance().defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(mdcContextKey: String): Mono<MDC> {
        return Mono.deferContextual { ctx: ContextView ->
            read(ctx, mdcContextKey)
        }
    }

    @JvmStatic
    fun read(context: ContextView): Mono<MDC> {
        return read(context, DefaultValues.getInstance().defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextView: ContextView, mdcContextKey: String): Mono<MDC> {
        val mdc = MDC(mdcContextKey)

        try {
            val map = contextView.get<Map<String, String>>(mdcContextKey)
            mdc.putAll(map)
        } catch (exception: Exception) {
            return Mono.error(InvalidContextDataException(exception))
        }

        return Mono.just(mdc)
    }
}