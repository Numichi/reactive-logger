package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.abstracts.ICore
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.coroutineContext

interface ICoroutineCore<T : IReactorLogger> : ICore {
    val reactorLogger: T
    val contextExtractive: CCResolveFn<CCElement>
    val contextKey: CCKey<out CCElement>

    override val name: String
        get() = reactorLogger.name

    suspend fun snapshot(context: Context? = null): MDC? {
        val ctx = context ?: coroutineContext[ReactorContext]?.context
        return ctx?.let { reactorLogger.snapshot(it).awaitSingleOrNull() }
    }

    suspend fun wrap(function: (T) -> Mono<*>) {
        val context = contextExtractive(contextKey) ?: Context.empty()

        function(reactorLogger)
            .contextWrite { it.putAll(context.readOnly()) }
            .awaitSingle()
    }

    suspend fun <R> wrapResult(function: (T) -> Mono<R>): R {
        throw UnsupportedOperationException()
    }
}