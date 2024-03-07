package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.core.ReactiveCore
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.reactor.extend.ExtendedRKLogger
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

class ReactiveKLogger(
    override val logger: KLogger,
    override val contextKey: Any,
    override val scheduler: Scheduler,
    override val name: String = logger.name
) : ReactiveCore<KLogger>(), ExtendedRKLogger {

    companion object {
        //region String base
        @JvmStatic
        fun getLogger(name: String): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, null)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, null)
        }

        @JvmStatic
        fun getLogger(name: String, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, scheduler)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, scheduler)
        }
        //endregion

        //region Class base
        @JvmStatic
        fun getLogger(clazz: Class<*>): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, scheduler)
        }
        //endregion

        //region Logger base
        @JvmStatic
        fun getLogger(logger: Logger): ReactiveKLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any): ReactiveKLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), contextKey, scheduler)
        }
        //endregion

        //region Lambda base
        @JvmStatic
        fun getLogger(func: () -> Unit): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, scheduler)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, scheduler)
        }
        //endregion

        //region KLogger base
        @JvmStatic
        fun getLogger(logger: KLogger): ReactiveKLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any): ReactiveKLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return ReactiveKLogger(
                logger,
                contextKey ?: Configuration.defaultReactorContextMdcKey,
                scheduler ?: Configuration.defaultScheduler
            )
        }
        //endregion
    }

    override fun isTraceEnabled(marker: Marker?): Boolean = logger.isTraceEnabled(marker)
    override fun trace(message: () -> Any?): Mono<Void> = wrap { logger.trace(message) }
    override fun trace(throwable: Throwable?, message: () -> Any?): Mono<Void> = wrap { logger.trace(throwable, message) }
    override fun trace(throwable: Throwable?, marker: Marker?, message: () -> Any?): Mono<Void> = wrap { logger.trace(throwable, marker, message) }

    override fun isDebugEnabled(marker: Marker?): Boolean = logger.isDebugEnabled(marker)
    override fun debug(message: () -> Any?): Mono<Void> = wrap { logger.debug(message) }
    override fun debug(throwable: Throwable?, message: () -> Any?): Mono<Void> = wrap { logger.debug(throwable, message) }
    override fun debug(throwable: Throwable?, marker: Marker?, message: () -> Any?): Mono<Void> = wrap { logger.debug(throwable, marker, message) }

    override fun isInfoEnabled(marker: Marker?): Boolean = logger.isInfoEnabled(marker)
    override fun info(message: () -> Any?): Mono<Void> = wrap { logger.info(message) }
    override fun info(throwable: Throwable?, message: () -> Any?): Mono<Void> = wrap { logger.info(throwable, message) }
    override fun info(throwable: Throwable?, marker: Marker?, message: () -> Any?): Mono<Void> = wrap { logger.info(throwable, marker, message) }

    override fun isWarnEnabled(marker: Marker?): Boolean = logger.isWarnEnabled(marker)
    override fun warn(message: () -> Any?): Mono<Void> = wrap { logger.warn(message) }
    override fun warn(throwable: Throwable?, message: () -> Any?): Mono<Void> = wrap { logger.warn(throwable, message) }
    override fun warn(throwable: Throwable?, marker: Marker?, message: () -> Any?): Mono<Void> = wrap { logger.warn(throwable, marker, message) }

    override fun isErrorEnabled(marker: Marker?): Boolean = logger.isErrorEnabled(marker)
    override fun isLoggingOff(marker: Marker?): Boolean = logger.isLoggingOff(marker)
    override fun isLoggingEnabledFor(level: Level, marker: Marker?): Boolean = logger.isLoggingEnabledFor(level, marker)

    override fun error(message: () -> Any?): Mono<Void> = wrap { logger.error(message) }
    override fun error(throwable: Throwable?, message: () -> Any?): Mono<Void> = wrap { logger.error(throwable, message) }
    override fun error(throwable: Throwable?, marker: Marker?, message: () -> Any?): Mono<Void> = wrap { logger.error(throwable, marker, message) }

    override fun atTrace(marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atTrace(marker, block) }
    override fun atTrace(block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atTrace(block) }
    override fun atDebug(marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atDebug(marker, block) }
    override fun atDebug(block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atDebug(block) }
    override fun atInfo(marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atInfo(marker, block) }
    override fun atInfo(block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atInfo(block) }
    override fun atWarn(marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atWarn(marker, block) }
    override fun atWarn(block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atWarn(block) }
    override fun atError(marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atError(marker, block) }
    override fun atError(block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.atError(block) }
    override fun at(level: Level, marker: Marker?, block: KLoggingEventBuilder.() -> Unit): Mono<Void> = wrap { logger.at(level, marker, block) }

    override fun entry(vararg arguments: Any?): Mono<Void> = wrap { logger.entry(*arguments) }
    override fun exit(): Mono<Void> = wrap { logger.exit() }
    override fun <T : Any?> exit(result: T): Mono<T> = wrap { logger.exit(result) }.then(Mono.justOrEmpty(result))
    override fun <T : Throwable> throwing(throwable: T): Mono<T> = wrap { logger.throwing(throwable) }.thenReturn(throwable)
    override fun <T : Throwable> catching(throwable: T): Mono<Void> = wrap { logger.catching(throwable) }
}
