package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.reactor.IReactorKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineKLogger private constructor(
    override val reactorLogger: IReactorKLogger,
    override val contextKey: CCKey<out CCElement>,
    override val contextExtractive: CCResolveFn<CCElement>,
) : ICoroutineKLogger, ACoroutine<IReactorKLogger>(
    reactorLogger.isEnableError,
    reactorLogger.mdcContextKey,
    reactorLogger.scheduler
) {

    companion object {
        @JvmStatic
        fun <E : CoroutineContext.Element> builder(element: CCKey<E>, contextExtractive: CCResolveFn<E>): Builder<E> {
            return Builder(element, contextExtractive)
        }

        @JvmStatic
        fun reactorBuilder() = builder(ReactorContext) { coroutineContext[it]?.context }
    }

    class Builder<E : CCElement>(
        contextKey: CCKey<E>,
        contextExtractive: CCResolveFn<E>,
        scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler,
        mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey,
        enableError: Boolean = false,
        logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveLogger::class.java))
    ) : ACoroutine.Builder<E, KLogger, CoroutineKLogger>(
        logger,
        contextKey,
        contextExtractive,
        scheduler,
        mdcContextKey,
        enableError
    ) {
        @Suppress("UNCHECKED_CAST")
        override fun build() = CoroutineKLogger(
            ReactiveKLogger(logger, enableError, mdcContextKey, scheduler),
            contextKey,
            contextExtractive as CCResolveFn<CCElement>
        )
    }

    override suspend fun <R> wrapResult(function: (IReactorKLogger) -> Mono<R>): R {
        val context = contextExtractive(contextKey) ?: Context.empty()

        return function(reactorLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }
}

