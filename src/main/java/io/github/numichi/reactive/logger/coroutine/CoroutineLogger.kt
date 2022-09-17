package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import mu.KLogger
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

class CoroutineLogger private constructor(
    override val reactorLogger: IReactorLogger,
    override val contextExtractive: suspend () -> ContextView?,
    override val mdcContextKey: String = reactorLogger.mdcContextKey,
    override val scheduler: Scheduler = reactorLogger.scheduler
) : ICoroutineLogger<IReactorLogger> {

    companion object {
        @JvmStatic
        fun getLogger(
            string: String,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context }
        ): CoroutineLogger {
            return getLogger(LoggerFactory.getLogger(string), mdcContextKey, scheduler, contextExtractive)
        }

        @JvmStatic
        fun getLogger(
            clazz: Class<*>,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context }
        ): CoroutineLogger {
            return getLogger(LoggerFactory.getLogger(clazz), mdcContextKey, scheduler, contextExtractive)
        }

        @JvmStatic
        fun getLogger(
            kLogger: KLogger,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context }
        ): CoroutineLogger {
            return getLogger(kLogger.underlyingLogger, mdcContextKey, scheduler, contextExtractive)
        }

        @JvmStatic
        fun getLogger(
            logger: Logger,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context }
        ): CoroutineLogger {
            mdcContextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return CoroutineLogger(
                ReactiveLogger(
                    logger,
                    mdcContextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                ),
                contextExtractive
            )
        }
    }
}

