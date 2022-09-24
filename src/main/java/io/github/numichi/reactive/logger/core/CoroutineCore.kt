package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.ContextResolver
import io.github.numichi.reactive.logger.exceptions.NotEmittedValueException
import io.github.numichi.reactive.logger.reactor.RLogger
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

abstract class CoroutineCore<R : RLogger, L : Logger>(
    override val logger: L,
    override val contextKey: String,
    override val scheduler: Scheduler,
) : Core<L>() {

    abstract val reactiveLogger: R

    suspend fun getContextView(): ContextView {
        return coroutineContext[ReactorContext]?.context ?: Context.empty()
    }

    suspend fun snapshot(contextView: ContextView? = null): MDC {
        val ctx = contextView ?: getContextView()
        return reactiveLogger.snapshot(ctx).awaitSingle()
    }

    fun <V> logSignal(signal: Signal<V>, fn: (L) -> Unit) {
        wrapRunner(signal.contextView) { fn(logger) }
    }

    suspend fun wrapUnit(function: (R) -> Mono<*>) {
        val context = getContextView()
        function(reactiveLogger).contextWrite { it.putAll(context) }.awaitSingleOrNull()
    }

    suspend fun <V> wrap(function: (R) -> Mono<V>): V {
        val context = getContextView()
        try {
            return function(reactiveLogger).contextWrite { it.putAll(context) }.awaitSingle()
        } catch (e: NoSuchElementException) {
            throw NotEmittedValueException("Did not emit any value", e)
        }
    }

    // ------------------------------ DEPRECATED ------------------------------

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", replaceWith = ReplaceWith("wrap(function)"))
    open suspend fun <V> wrapResult(function: (R) -> Mono<V>): V {
        throw UnsupportedOperationException()
    }

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", replaceWith = ReplaceWith("reactiveLogger"))
    val reactorLogger: R
        get() = reactiveLogger

    @Deprecated("Customizable the context resolver was removed, so it was deprecated. Use: getContextView(). It will be deleted in v3.3.0.", ReplaceWith("getContextView()"))
    val contextResolver: ContextResolver
        get() = { coroutineContext[ReactorContext]?.context }
}