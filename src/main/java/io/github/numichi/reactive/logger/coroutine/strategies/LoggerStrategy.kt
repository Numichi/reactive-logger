package io.github.numichi.reactive.logger.coroutine.strategies

import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.awaitSingle
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext

class LoggerStrategy(
    private val logger: ReactiveLogger,
    private val contextKey: CoroutineContext.Key<out CoroutineContext.Element>,
    private val contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
) : WrapStrategy<ReactiveLogger> {

    override fun getLogger(): ReactiveLogger {
        return logger
    }

    override suspend fun <R> wrap(fn: (ReactiveLogger) -> Mono<R>): R {
        val context = contextExtractive(contextKey) ?: Context.empty()

        return fn(logger)
            .contextWrite { it.putAll(context.readOnly()) }
            .awaitSingle()
    }
}