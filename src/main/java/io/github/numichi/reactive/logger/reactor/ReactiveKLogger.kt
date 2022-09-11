package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import mu.KLogger
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

class ReactiveKLogger(
    override val logger: KLogger,
    override val mdcContextKey: String,
    override val scheduler: Scheduler,
) : IReactorKLogger {

    companion object {
        @JvmStatic
        fun getLogger(
            string: String,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(string), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            clazz: Class<*>,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            logger: Logger,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            func: () -> Unit,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            logger: KLogger,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null
        ): ReactiveKLogger {
            mdcContextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return ReactiveKLogger(
                logger,
                mdcContextKey ?: Configuration.defaultReactorContextMdcKey,
                scheduler ?: Configuration.defaultScheduler
            )
        }
    }
}
