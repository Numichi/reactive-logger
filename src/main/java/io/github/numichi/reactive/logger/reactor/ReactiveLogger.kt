package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

class ReactiveLogger(
    override val logger: Logger,
    override val mdcContextKey: String,
    override val scheduler: Scheduler,
) : IReactorLogger {

    companion object {
        @JvmStatic
        fun getLogger(
            string: String,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(string), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            clazz: Class<*>,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(clazz), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(
            logger: Logger,
            mdcContextKey: String? = null,
            scheduler: Scheduler? = null
        ): ReactiveLogger {
            mdcContextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return ReactiveLogger(
                logger,
                mdcContextKey ?: Configuration.defaultReactorContextMdcKey,
                scheduler ?: Configuration.defaultScheduler
            )
        }
    }
}
