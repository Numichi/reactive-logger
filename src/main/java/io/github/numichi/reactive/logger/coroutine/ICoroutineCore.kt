package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.ICore
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

interface ICoroutineCore<T : IReactorLogger> : ICore {
    val reactorLogger: T
    val contextExtractive: suspend () -> ContextView?

    override val name: String
        get() = reactorLogger.name

    suspend fun snapshot(context: ContextView? = null): MDC? {
        val ctx = context ?: coroutineContext[ReactorContext]?.context
        return ctx?.let { reactorLogger.snapshot(it).awaitSingleOrNull() }
    }

    suspend fun wrap(function: (T) -> Mono<*>) {
        val context = contextExtractive() ?: Context.empty()

        function(reactorLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }

    suspend fun <R> wrapResult(function: (T) -> Mono<R>): R {
        throw UnsupportedOperationException()
    }
}