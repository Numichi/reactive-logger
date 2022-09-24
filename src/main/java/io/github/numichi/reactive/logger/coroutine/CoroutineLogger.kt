package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import io.github.numichi.reactive.logger.core.CoroutineCore
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import mu.KLogger
import org.slf4j.Logger
import org.slf4j.Marker
import reactor.core.scheduler.Scheduler

open class CoroutineLogger(
    override val reactiveLogger: ReactiveLogger
) : CoroutineCore<ReactiveLogger, Logger>(reactiveLogger.logger, reactiveLogger.contextKey, reactiveLogger.scheduler), CLogger {

    companion object {
        @JvmStatic
        fun getLogger(string: String, contextKey: String? = null, scheduler: Scheduler? = null): CoroutineLogger {
            return getLogger(LoggerFactory.getLogger(string), contextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: String? = null, scheduler: Scheduler? = null): CoroutineLogger {
            return getLogger(LoggerFactory.getLogger(clazz), contextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(kLogger: KLogger, contextKey: String? = null, scheduler: Scheduler? = null): CoroutineLogger {
            return getLogger(kLogger.underlyingLogger, contextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: String? = null, scheduler: Scheduler? = null): CoroutineLogger {
            contextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return CoroutineLogger(
                ReactiveLogger(
                    logger,
                    contextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                )
            )
        }
    }

    override val isTraceEnabled: Boolean
        get() = reactiveLogger.isTraceEnabled

    override fun isTraceEnabled(marker: Marker?): Boolean = reactiveLogger.isTraceEnabled(marker)
    override suspend fun trace(msg: String?) = wrapUnit { it.trace(msg) }
    override suspend fun trace(format: String?, arg: Any?) = wrapUnit { it.trace(format, arg) }
    override suspend fun trace(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.trace(format, arg1, arg2) }
    override suspend fun trace(format: String?, vararg arguments: Any?) = wrapUnit { it.trace(format, *arguments) }
    override suspend fun trace(msg: String?, t: Throwable?) = wrapUnit { it.trace(msg, t) }
    override suspend fun trace(marker: Marker?, msg: String?) = wrapUnit { it.trace(marker, msg) }
    override suspend fun trace(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.trace(marker, format, arg) }
    override suspend fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.trace(marker, format, arg1, arg2) }
    override suspend fun trace(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.trace(marker, format, *argArray) }
    override suspend fun trace(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.trace(marker, msg, t) }

    override val isDebugEnabled: Boolean
        get() = reactiveLogger.isDebugEnabled

    override fun isDebugEnabled(marker: Marker?): Boolean = reactiveLogger.isDebugEnabled(marker)
    override suspend fun debug(msg: String?) = wrapUnit { it.debug(msg) }
    override suspend fun debug(format: String?, arg: Any?) = wrapUnit { it.debug(format, arg) }
    override suspend fun debug(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.debug(format, arg1, arg2) }
    override suspend fun debug(format: String?, vararg arguments: Any?) = wrapUnit { it.debug(format, *arguments) }
    override suspend fun debug(msg: String?, t: Throwable?) = wrapUnit { it.debug(msg, t) }
    override suspend fun debug(marker: Marker?, msg: String?) = wrapUnit { it.debug(marker, msg) }
    override suspend fun debug(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.debug(marker, format, arg) }
    override suspend fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.debug(marker, format, arg1, arg2) }
    override suspend fun debug(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.debug(marker, format, *argArray) }
    override suspend fun debug(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.debug(marker, msg, t) }

    override val isInfoEnabled: Boolean
        get() = reactiveLogger.isInfoEnabled

    override fun isInfoEnabled(marker: Marker?): Boolean = reactiveLogger.isInfoEnabled(marker)
    override suspend fun info(msg: String?) = wrapUnit { it.info(msg) }
    override suspend fun info(format: String?, arg: Any?) = wrapUnit { it.info(format, arg) }
    override suspend fun info(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.info(format, arg1, arg2) }
    override suspend fun info(format: String?, vararg arguments: Any?) = wrapUnit { it.info(format, *arguments) }
    override suspend fun info(msg: String?, t: Throwable?) = wrapUnit { it.info(msg, t) }
    override suspend fun info(marker: Marker?, msg: String?) = wrapUnit { it.info(marker, msg) }
    override suspend fun info(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.info(marker, format, arg) }
    override suspend fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.info(marker, format, arg1, arg2) }
    override suspend fun info(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.info(marker, format, *argArray) }
    override suspend fun info(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.info(marker, msg, t) }

    override val isWarnEnabled: Boolean
        get() = reactiveLogger.isWarnEnabled

    override fun isWarnEnabled(marker: Marker?): Boolean = reactiveLogger.isWarnEnabled(marker)
    override suspend fun warn(msg: String?) = wrapUnit { it.warn(msg) }
    override suspend fun warn(format: String?, arg: Any?) = wrapUnit { it.warn(format, arg) }
    override suspend fun warn(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.warn(format, arg1, arg2) }
    override suspend fun warn(format: String?, vararg arguments: Any?) = wrapUnit { it.warn(format, *arguments) }
    override suspend fun warn(msg: String?, t: Throwable?) = wrapUnit { it.warn(msg, t) }
    override suspend fun warn(marker: Marker?, msg: String?) = wrapUnit { it.warn(marker, msg) }
    override suspend fun warn(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.warn(marker, format, arg) }
    override suspend fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.warn(marker, format, arg1, arg2) }
    override suspend fun warn(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.warn(marker, format, *argArray) }
    override suspend fun warn(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.warn(marker, msg, t) }

    override val isErrorEnabled: Boolean
        get() = reactiveLogger.isErrorEnabled

    override fun isErrorEnabled(marker: Marker?): Boolean = reactiveLogger.isErrorEnabled(marker)
    override suspend fun error(msg: String?) = wrapUnit { it.error(msg) }
    override suspend fun error(format: String?, arg: Any?) = wrapUnit { it.error(format, arg) }
    override suspend fun error(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.error(format, arg1, arg2) }
    override suspend fun error(format: String?, vararg arguments: Any?) = wrapUnit { it.error(format, *arguments) }
    override suspend fun error(msg: String?, t: Throwable?) = wrapUnit { it.error(msg, t) }
    override suspend fun error(marker: Marker?, msg: String?) = wrapUnit { it.error(marker, msg) }
    override suspend fun error(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.error(marker, format, arg) }
    override suspend fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.error(marker, format, arg1, arg2) }
    override suspend fun error(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.error(marker, format, *argArray) }
    override suspend fun error(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.error(marker, msg, t) }
}

