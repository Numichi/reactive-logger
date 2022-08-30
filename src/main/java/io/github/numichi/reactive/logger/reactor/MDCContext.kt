package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import io.github.numichi.reactive.logger.hook.mdcReferenceContentLoad
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
    fun getMDCOrNull(context: ContextView): MDC? {
        return getMDCOrNull(context, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun getMDCOrNull(context: ContextView, mdcContextKey: Any): MDC? {
        return runCatching { context.get<MDC>(mdcContextKey) }.getOrNull()
    }

    @JvmStatic
    fun getMDCOrDefault(context: ContextView, mdcContextKey: Any): MDC {
        return getMDCOrNull(context, mdcContextKey) ?: MDC(mdcContextKey.toString())
    }

    @JvmStatic
    fun getMDCOrDefault(context: ContextView): MDC {
        return getMDCOrNull(context) ?: MDC()
    }

    @JvmStatic
    fun modifyContext(context: Context, mdcContextKey: Any, func: (MDC) -> Unit): Context {
        val mdc = getMDCOrNull(context, mdcContextKey) ?: MDC(mdcContextKey.toString())
        func(mdc)
        return put(context, mdc)
    }

    @JvmStatic
    fun modifyContext(context: Context, func: (MDC) -> Unit): Context {
        val mdc = getMDCOrNull(context) ?: MDC()
        func(mdc)
        return put(context, mdc)
    }

    @JvmStatic
    fun read(): Mono<MDC> {
        return read(Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(mdcContextKey: String): Mono<MDC> {
        return Mono.deferContextual { ctx: ContextView ->
            read(ctx, mdcContextKey)
        }
    }

    @JvmStatic
    fun read(context: ContextView): Mono<MDC> {
        return read(context, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextView: ContextView, mdcContextKey: String): Mono<MDC> {
        val mdc = MDC(mdcContextKey)

        try {
            mdcReferenceContentLoad(contextView, mdcContextKey, mdc)
        } catch (exception: Exception) {
            return Mono.error(InvalidContextDataException(exception))
        }

        return Mono.just(mdc)
    }
}