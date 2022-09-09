package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import kotlinx.coroutines.reactor.ReactorContext
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ReactiveKLogger(
    override val logger: KLogger,
    mdcContextKey: String,
    scheduler: Scheduler,
) : AReactive(logger, mdcContextKey, scheduler),
    IReactorKLogger {

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun getLogger(
            logger: KLogger,
            contextKey: String? = null,
            scheduler: Scheduler? = null,
        ): ReactiveKLogger {
            return builder()
                .setLogger(logger)
                .setMDCContextKey(contextKey ?: Configuration.defaultReactorContextMdcKey)
                .setScheduler(scheduler ?: Configuration.defaultScheduler)
                .build()
        }

        @JvmStatic
        fun getDefaultLogger(logger: KLogger): ReactiveKLogger {
            return getLogger(logger, null, null)
        }
    }

    class Builder(
        scheduler: Scheduler = Configuration.defaultScheduler,
        mdcContextKey: String = Configuration.defaultReactorContextMdcKey,
        logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveKLogger::class.java))
    ) : AReactive.Builder<KLogger, ReactiveKLogger>(logger, scheduler, mdcContextKey) {
        override fun build() = ReactiveKLogger(logger, mdcContextKey, scheduler)
    }
}
