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
    fun merge(context: Context, contextKey: Any, map: Map<String, String?>): Context {
        val mdc = try {
            read(context, contextKey)
        } catch (e: ReadException) {
            MDC(contextKey)
        }

        return put(context, mdc.plus(map.toSafeMdcMap()))
    }

    @JvmStatic
    fun merge(context: Context, contextKey: Any, tuple2: Tuple2<String, String?>): Context {
        return merge(context, contextKey, tuple2.toSafeMdcMap())
    }

    @JvmStatic
    fun merge(context: Context, contextKey: Any, pair: Pair<String, String?>): Context {
        return merge(context, contextKey, mapOf(pair.toSafeMdcPair()))
    }

    @JvmStatic
    fun merge(context: Context, map: Map<String, String?>): Context {
        return merge(context, Configuration.defaultReactorContextMdcKey, map)
    }

    @JvmStatic
    fun merge(context: Context, tuple2: Tuple2<String, String?>): Context {
        return merge(context, Configuration.defaultReactorContextMdcKey, tuple2)
    }

    @JvmStatic
    fun merge(context: Context, pair: Pair<String, String?>): Context {
        return merge(context, Configuration.defaultReactorContextMdcKey, pair)
    }

    @JvmStatic
    fun merge(context: Context, mdc: MDC): Context {
        return merge(context, mdc.contextKey, mdc.data)
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
    fun read(context: ContextView): MDC {
        return read(context, Configuration.defaultReactorContextMdcKey)
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
    fun readOrDefault(context: ContextView): MDC {
        return readOrDefault(context, Configuration.defaultReactorContextMdcKey)
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
    fun snapshot(contextKey: String): Mono<MDC> {
        return Mono.deferContextual { contextView: ContextView ->
            snapshot(contextView, contextKey)
        }
    }

    @JvmStatic
    fun snapshot(contextView: ContextView): Mono<MDC> {
        return snapshot(contextView, Configuration.defaultReactorContextMdcKey)
    }

    @JvmStatic
    fun snapshot(contextView: ContextView, contextKey: String): Mono<MDC> {
        return Mono.just(mdcReferenceContentLoad(contextView, MDC(contextKey)))
    }
    //endregion
}