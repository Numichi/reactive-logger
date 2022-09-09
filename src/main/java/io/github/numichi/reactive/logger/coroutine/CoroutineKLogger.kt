package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
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
import reactor.util.context.ContextView
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineKLogger private constructor(
    override val reactorLogger: IReactorKLogger,
    override val contextExtractive: suspend () -> ContextView?,
) : ICoroutineKLogger, ACoroutine<IReactorKLogger>(
    reactorLogger.mdcContextKey,
    reactorLogger.scheduler
) {

    companion object {
        @JvmStatic
        @Deprecated(
            "CoroutineContext.Key is not need!",
            replaceWith = ReplaceWith("builder(contextExtractive)"),
            level = DeprecationLevel.WARNING
        )
        fun <E : CoroutineContext.Element> builder(key: CoroutineContext.Key<E>, contextExtractive: suspend () -> ContextView?): CoroutineLogger.Builder {
            return CoroutineLogger.Builder(contextExtractive)
        }

        @JvmStatic
        fun builder(contextExtractive: suspend () -> ContextView?): Builder {
            return Builder(contextExtractive)
        }

        @JvmStatic
        fun reactorBuilder() = builder { coroutineContext[ReactorContext]?.context }

        @JvmStatic
        fun getLogger(
            logger: KLogger,
            contextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context } 
        ): CoroutineKLogger {
            return builder(contextExtractive)
                .setLogger(logger)
                .setMDCContextKey(contextKey ?: Configuration.defaultReactorContextMdcKey)
                .setScheduler(scheduler ?: Configuration.defaultScheduler)
                .build()
        }
    }

    class Builder(
        contextExtractive: suspend () -> ContextView?,
        scheduler: Scheduler = Configuration.defaultScheduler,
        mdcContextKey: String = Configuration.defaultReactorContextMdcKey,
        logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveLogger::class.java))
    ) : ACoroutine.Builder<KLogger, CoroutineKLogger>(
        logger,
        contextExtractive,
        scheduler,
        mdcContextKey,
    ) {
        override fun build() = CoroutineKLogger(
            ReactiveKLogger(logger, mdcContextKey, scheduler),
            contextExtractive
        )
    }

    override suspend fun <R> wrapResult(function: (IReactorKLogger) -> Mono<R>): R {
        val context = contextExtractive() ?: Context.empty()

        return function(reactorLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }
}

