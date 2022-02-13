package io.github.numichi.reactive.logger.coroutine.strategies

import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import kotlinx.coroutines.reactor.awaitSingle
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext

class KLoggerStrategy(
    private val logger: ReactiveKLogger,
    private val contextKey: CoroutineContext.Key<out CoroutineContext.Element>,
    private val contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
) : WrapStrategy<ReactiveKLogger> {

    override fun getLogger(): ReactiveKLogger {
        return logger
    }

    override suspend fun <R> wrap(fn: (ReactiveKLogger) -> Mono<R>): R {
        val context = contextExtractive(contextKey) ?: Context.empty()

        return fn(logger)
            .contextWrite { it.putAll(context.readOnly()) }
            .awaitSingle()
    }
}