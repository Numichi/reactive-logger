package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.ICore
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

interface ICoroutineCore<T : IReactorLogger> : ICore {
    val reactorLogger: T
    val contextResolver: ContextResolver
        get() = { coroutineContext[ReactorContext]?.context }

    override val name: String
        get() = reactorLogger.name

    fun <T> logSignal(signal: Signal<T>, fn: (Logger) -> Unit) {
        takeMDCSnapshot(signal.contextView).use { fn(reactorLogger.logger) }
    }

    suspend fun snapshot(context: ContextView? = null): MDC? {
        val ctx = context ?: contextResolver()
        return ctx?.let { reactorLogger.snapshot(it).awaitSingleOrNull() }
    }

    suspend fun wrap(function: (T) -> Mono<*>) {
        val context = contextResolver() ?: (Context.empty() as ContextView)
        function(reactorLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }

    suspend fun <R> wrapResult(function: (T) -> Mono<R>): R {
        throw UnsupportedOperationException()
    }
}