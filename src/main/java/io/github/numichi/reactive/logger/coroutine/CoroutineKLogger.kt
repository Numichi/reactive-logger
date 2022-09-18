package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import io.github.numichi.reactive.logger.reactor.IReactorKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogger
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context

class CoroutineKLogger private constructor(
    override val reactorLogger: IReactorKLogger,
    override val mdcContextKey: String = reactorLogger.mdcContextKey,
    override val scheduler: Scheduler = reactorLogger.scheduler
) : ICoroutineKLogger {
    companion object {
        @JvmStatic
        fun getLogger(string: String, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(string), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            mdcContextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return CoroutineKLogger(
                ReactiveKLogger(
                    logger,
                    mdcContextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                ),
            )
        }
    }

    override suspend fun <R> wrapResult(function: (IReactorKLogger) -> Mono<R>): R {
        val context = contextResolver() ?: Context.empty()

        return function(reactorLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }
}

