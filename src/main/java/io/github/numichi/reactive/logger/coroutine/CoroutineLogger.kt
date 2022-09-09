package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import mu.KLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineLogger private constructor(
    override val reactorLogger: IReactorLogger,
    override val contextKey: CCKey<out CCElement>,
    override val contextExtractive: CCResolveFn<CCElement>,
) : ICoroutineLogger<IReactorLogger>, ACoroutine<IReactorLogger>(
    reactorLogger.mdcContextKey,
    reactorLogger.scheduler
) {

    companion object {
        @JvmStatic
        fun <E : CoroutineContext.Element> builder(element: CCKey<E>, contextExtractive: CCResolveFn<E>): Builder<E> {
            return Builder(element, contextExtractive)
        }

        @JvmStatic
        fun reactorBuilder(): Builder<ReactorContext> {
            return builder(ReactorContext) { coroutineContext[it]?.context }
        }

        @JvmStatic
        fun <E : CoroutineContext.Element> getLogger(
            logger: Logger,
            contextKey: String? = null,
            scheduler: Scheduler? = null,
            coroutineContextKey: CCKey<E>,
            contextExtractive: CCResolveFn<E>
        ): CoroutineLogger {
            return builder(coroutineContextKey, contextExtractive)
                .setLogger(logger)
                .setMDCContextKey(contextKey ?: Configuration.defaultReactorContextMdcKey)
                .setScheduler(scheduler ?: Configuration.defaultScheduler)
                .build()
        }

        @JvmStatic
        fun getDefaultLogger(logger: Logger): CoroutineLogger {
            return getLogger(logger, null, null, ReactorContext) { coroutineContext[it]?.context }
        }
    }

    class Builder<E : CCElement>(
        contextKey: CCKey<E>,
        contextExtractive: CCResolveFn<E>,
        scheduler: Scheduler = Configuration.defaultScheduler,
        mdcContextKey: String = Configuration.defaultReactorContextMdcKey,
        logger: Logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    ) : ACoroutine.Builder<E, Logger, CoroutineLogger>(logger, contextKey, contextExtractive, scheduler, mdcContextKey) {
        @Suppress("UNCHECKED_CAST")
        override fun build() = CoroutineLogger(
            ReactiveLogger(logger, mdcContextKey, scheduler),
            contextKey,
            contextExtractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
        )
    }
}

