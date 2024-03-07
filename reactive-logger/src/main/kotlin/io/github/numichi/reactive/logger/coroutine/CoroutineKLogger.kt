package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.core.CoroutineCore
import io.github.numichi.reactive.logger.coroutine.extend.ExtendedCKLogger
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.reactor.RKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

class CoroutineKLogger(
    override val reactiveLogger: ReactiveKLogger,
    override val name: String = reactiveLogger.name
) : CoroutineCore<RKLogger, KLogger>(reactiveLogger.logger, reactiveLogger.contextKey, reactiveLogger.scheduler), ExtendedCKLogger {

    companion object {
        //region String base
        @JvmStatic
        fun getLogger(name: String): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, null)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, null)
        }

        @JvmStatic
        fun getLogger(name: String, scheduler: Scheduler): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, scheduler)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any?, scheduler: Scheduler?): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, scheduler)
        }
        //endregion

        //region Class base
        @JvmStatic
        fun getLogger(clazz: Class<*>): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, scheduler: Scheduler): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any?, scheduler: Scheduler?): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, scheduler)
        }
        //endregion

        //region Logger base
        @JvmStatic
        fun getLogger(logger: Logger): CoroutineKLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any): CoroutineKLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, scheduler: Scheduler): CoroutineKLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any?, scheduler: Scheduler?): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), contextKey, scheduler)
        }
        //endregion

        //region Lambda base
        @JvmStatic
        fun getLogger(func: () -> Unit): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, scheduler: Scheduler): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, scheduler)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any?, scheduler: Scheduler?): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, scheduler)
        }
        //endregion

        //region KLogger base
        @JvmStatic
        fun getLogger(logger: KLogger): CoroutineKLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any): CoroutineKLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, scheduler: Scheduler): CoroutineKLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any?, scheduler: Scheduler?): CoroutineKLogger {
            return CoroutineKLogger(
                ReactiveKLogger(
                    logger,
                    contextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                )
            )
        }
        //endregion
    }

    override fun isLoggingOff(marker: Marker?): Boolean = reactiveLogger.isLoggingOff(marker)
    override fun isLoggingEnabledFor(level: Level, marker: Marker?): Boolean = reactiveLogger.isLoggingEnabledFor(level, marker)

    override fun isTraceEnabled(marker: Marker?): Boolean = reactiveLogger.isTraceEnabled(marker)
    override suspend fun trace(message: () -> Any?) = wrapUnit { it.trace(message) }
    override suspend fun trace(throwable: Throwable?, message: () -> Any?) = wrapUnit { it.trace(throwable, message) }
    override suspend fun trace(throwable: Throwable?, marker: Marker?, message: () -> Any?) = wrapUnit { it.trace(throwable, marker, message) }

    override fun isDebugEnabled(marker: Marker?): Boolean = reactiveLogger.isDebugEnabled(marker)
    override suspend fun debug(message: () -> Any?) = wrapUnit { it.debug(message) }
    override suspend fun debug(throwable: Throwable?, message: () -> Any?) = wrapUnit { it.debug(throwable, message) }
    override suspend fun debug(throwable: Throwable?, marker: Marker?, message: () -> Any?) = wrapUnit { it.debug(throwable, marker, message) }

    override fun isInfoEnabled(marker: Marker?): Boolean = reactiveLogger.isInfoEnabled(marker)
    override suspend fun info(message: () -> Any?) = wrapUnit { it.info(message) }
    override suspend fun info(throwable: Throwable?, message: () -> Any?) = wrapUnit { it.info(throwable, message) }
    override suspend fun info(throwable: Throwable?, marker: Marker?, message: () -> Any?) = wrapUnit { it.info(throwable, marker, message) }

    override fun isWarnEnabled(marker: Marker?): Boolean = reactiveLogger.isWarnEnabled(marker)
    override suspend fun warn(message: () -> Any?) = wrapUnit { it.warn(message) }
    override suspend fun warn(throwable: Throwable?, message: () -> Any?) = wrapUnit { it.warn(throwable, message) }
    override suspend fun warn(throwable: Throwable?, marker: Marker?, message: () -> Any?) = wrapUnit { it.warn(throwable, marker, message) }


    override fun isErrorEnabled(marker: Marker?): Boolean = reactiveLogger.isErrorEnabled(marker)
    override suspend fun error(message: () -> Any?) = wrapUnit { it.error(message) }
    override suspend fun error(throwable: Throwable?, message: () -> Any?) = wrapUnit { it.error(throwable, message) }
    override suspend fun error(throwable: Throwable?, marker: Marker?, message: () -> Any?) = wrapUnit { it.error(throwable, marker, message) }

    override suspend fun atTrace(marker: Marker?, block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atTrace(marker, block) }
    override suspend fun atTrace(block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atTrace(block) }
    override suspend fun atDebug(marker: Marker?, block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atDebug(marker, block) }
    override suspend fun atDebug(block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atDebug(block) }
    override suspend fun atInfo(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)  = wrapUnit { it.atInfo(marker, block) }
    override suspend fun atInfo(block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atInfo(block) }
    override suspend fun atWarn(marker: Marker?, block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atWarn(marker, block) }
    override suspend fun atWarn(block: KLoggingEventBuilder.() -> Unit)  = wrapUnit { it.atWarn(block) }
    override suspend fun atError(marker: Marker?, block: KLoggingEventBuilder.() -> Unit) = wrapUnit { it.atError(marker, block) }
    override suspend fun atError(block: KLoggingEventBuilder.() -> Unit)  = wrapUnit { it.atError(block) }
    override suspend fun at(level: Level, marker: Marker?, block: KLoggingEventBuilder.() -> Unit)  = wrapUnit { it.at(level, marker, block) }

    override suspend fun entry(vararg arguments: Any?) = wrapUnit { it.entry(*arguments) }
    override suspend fun exit() = wrapUnit { it.exit() }
    override suspend fun <T> exit(result: T) = wrap { it.exit(result) }
    override suspend fun <T : Throwable> throwing(throwable: T) = wrap { it.throwing(throwable) }
    override suspend fun <T : Throwable> catching(throwable: T) = wrapUnit { it.catching(throwable) }
}
