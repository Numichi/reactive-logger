package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.readOrDefaultMdc
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.github.numichi.reactive.logger.hook.mdcReferenceContentLoad
import io.github.numichi.reactive.logger.toSafeMdcMap
import io.github.numichi.reactive.logger.toSafeMdcPair
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView
import reactor.util.function.Tuple2
import java.util.function.Function

object MDCContext {
    //region put
    @JvmStatic
    fun put(context: Context, vararg mdc: MDC): Context {
        val addedKeys = mutableSetOf<Any>()
        var newContext = context

        for (m in mdc) {
            check(!addedKeys.contains(m.contextKey)) { "Duplicate context key writing." }
            addedKeys.add(m.contextKey)
            newContext = newContext.put(m.contextKey, m.data)
        }

        return newContext
    }
    //endregion

    //region merge
    @JvmStatic
    fun modify(context: Context, contextKey: Any, data: Map<String, String?>): Context {
        val mdc = try {
            read(context, contextKey)
        } catch (e: ReadException) {
            MDC(contextKey)
        }

        return put(context, mdc.plus(data.toSafeMdcMap()))
    }

    @JvmStatic
    fun modify(context: Context, contextKey: Any, data: Tuple2<String, String?>): Context {
        return modify(context, contextKey, data.toSafeMdcMap())
    }

    @JvmStatic
    fun modify(context: Context, contextKey: Any, data: Pair<String, String?>): Context {
        return modify(context, contextKey, mapOf(data.toSafeMdcPair()))
    }

    @JvmStatic
    fun modify(context: Context, data: Map<String, String?>): Context {
        return modify(context, Configuration.defaultReactorContextMdcKey, data)
    }

    @JvmStatic
    fun modify(context: Context, data: Tuple2<String, String?>): Context {
        return modify(context, Configuration.defaultReactorContextMdcKey, data)
    }

    @JvmStatic
    fun modify(context: Context, data: Pair<String, String?>): Context {
        return modify(context, Configuration.defaultReactorContextMdcKey, data)
    }

    @JvmStatic
    fun modify(context: Context, data: MDC): Context {
        return modify(context, data.contextKey, data.data)
    }
    //endregion

    //region get
    @JvmStatic
    fun read(): Mono<MDC> {
        return read(Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextKey: Any): Mono<MDC> {
        return Mono.deferContextual { contextView: ContextView ->
            try {
                Mono.just(read(contextView, contextKey))
            } catch (e: ReadException) {
                Mono.error(e)
            }
        }
    }

    @JvmStatic
    fun read(contextView: ContextView): MDC {
        return read(contextView, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun read(contextView: ContextView, contextKey: Any): MDC {
        return readMdc(contextView, contextKey)
    }

    @JvmStatic
    fun readOrDefault(): Mono<MDC> {
        return readOrDefault(Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun readOrDefault(contextKey: Any): Mono<MDC> {
        return Mono.deferContextual { contextView: ContextView ->
            try {
                Mono.just(readOrDefault(contextView, contextKey))
            } catch (e: ReadException) {
                Mono.error(e)
            }
        }
    }

    @JvmStatic
    fun readOrDefault(contextView: ContextView): MDC {
        return readOrDefault(contextView, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun readOrDefault(contextView: ContextView, contextKey: Any): MDC {
        return readOrDefaultMdc(contextView, contextKey)
    }
    //endregion

    //region modify
    @JvmStatic
    fun modify(context: Context, contextKey: Any, func: Function<MDC, MDC>): Context {
        val mdc = try {
            read(context, contextKey)
        } catch (e: ReadException) {
            MDC(contextKey)
        }

        return put(context, func.apply(mdc))
    }

    @JvmStatic
    fun modify(context: Context, func: Function<MDC, MDC>): Context {
        return modify(context, Configuration.defaultReactorContextMdcKey, func)
    }
    //endregion

    //region snapshot
    @JvmStatic
    fun snapshot(): Mono<MDC> {
        return snapshot(Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun snapshot(contextKey: Any): Mono<MDC> {
        return Mono.deferContextual { contextView: ContextView ->
            Mono.just(snapshot(contextView, contextKey))
        }
    }

    @JvmStatic
    fun snapshot(contextView: ContextView): MDC {
        return snapshot(contextView, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun snapshot(contextView: ContextView, contextKey: Any): MDC {
        return mdcReferenceContentLoad(contextView, MDC(contextKey))
    }
    //endregion
}