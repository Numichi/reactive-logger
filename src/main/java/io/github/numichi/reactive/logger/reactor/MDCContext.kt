package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
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
    fun getMDCOrNull(context: ContextView, contextKey: Any): MDC? {
        return runCatching { context.get<MDC>(contextKey) }.getOrNull()
    }

    @JvmStatic
    fun getMDCOrDefault(context: ContextView, contextKey: Any): MDC {
        return getMDCOrNull(context, contextKey) ?: MDC(contextKey.toString())
    }

    @JvmStatic
    fun getMDCOrDefault(context: ContextView): MDC {
        return getMDCOrNull(context) ?: MDC()
    }

    @JvmStatic
    fun modifyContext(context: Context, contextKey: Any, func: (MDC) -> Unit): Context {
        val mdc = getMDCOrNull(context, contextKey) ?: MDC(contextKey.toString())
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
    fun modifyContext(context: Context, map: Map<String, String>): Context {
        val mdc = getMDCOrNull(context) ?: MDC()
        mdc.putAll(map)
        return put(context, mdc)
    }

    @JvmStatic
    fun modifyContext(context: Context, contextKey: Any, map: Map<String, String>): Context {
        val mdc = getMDCOrNull(context, contextKey) ?: MDC(contextKey.toString())
        mdc.putAll(map)
        return put(context, mdc)
    }

    @JvmStatic
    fun read(): Mono<MDC> {
        return read(Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextKey: String): Mono<MDC> {
        return Mono.deferContextual { ctx: ContextView ->
            read(ctx, contextKey)
        }
    }

    @JvmStatic
    fun read(context: ContextView): Mono<MDC> {
        return read(context, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextView: ContextView, contextKey: String): Mono<MDC> {
        val mdc = MDC(contextKey)
        mdcReferenceContentLoad(contextView, contextKey, mdc)
        return Mono.just(mdc)
    }
}