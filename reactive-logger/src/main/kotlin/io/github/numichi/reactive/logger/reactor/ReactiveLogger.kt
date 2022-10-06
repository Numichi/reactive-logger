package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import io.github.numichi.reactive.logger.core.ReactiveCore
import org.slf4j.Logger
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

open class ReactiveLogger(
    override val logger: Logger,
    override val contextKey: String,
    override val scheduler: Scheduler
) : ReactiveCore<Logger>(), RLogger {

    companion object {
        //region String base
        @JvmStatic
        fun getLogger(string: String): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(string), null, null)
        }

        @JvmStatic
        fun getLogger(string: String, contextKey: String?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(string), contextKey, null)
        }

        @JvmStatic
        fun getLogger(string: String, scheduler: Scheduler?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(string), null, scheduler)
        }

        @JvmStatic
        fun getLogger(string: String, contextKey: String?, scheduler: Scheduler?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(string), contextKey, scheduler)
        }
        //endregion

        //region Class base
        @JvmStatic
        fun getLogger(clazz: Class<*>): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(clazz), null, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: String?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(clazz), contextKey, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, scheduler: Scheduler?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(clazz), null, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: String?, scheduler: Scheduler?): ReactiveLogger {
            return getLogger(LoggerFactory.getLogger(clazz), contextKey, scheduler)
        }
        //endregion

        //region Logger base
        @JvmStatic
        fun getLogger(logger: Logger): ReactiveLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: String?): ReactiveLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, scheduler: Scheduler?): ReactiveLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: String?, scheduler: Scheduler?): ReactiveLogger {
            contextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return ReactiveLogger(
                logger,
                contextKey ?: Configuration.defaultReactorContextMdcKey,
                scheduler ?: Configuration.defaultScheduler
            )
        }
        //endregion
    }

    override val isTraceEnabled: Boolean
        get() = logger.isTraceEnabled

    override fun isTraceEnabled(marker: Marker?): Boolean = logger.isTraceEnabled(marker)
    override fun trace(msg: String?): Mono<Void> = wrap { logger.trace(msg) }
    override fun trace(format: String?, arg: Any?): Mono<Void> = wrap { logger.trace(format, arg) }
    override fun trace(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.trace(format, arg1, arg2) }
    override fun trace(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.trace(format, *arguments) }
    override fun trace(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.trace(msg, t) }
    override fun trace(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.trace(marker, msg) }
    override fun trace(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.trace(marker, format, arg) }
    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.trace(marker, format, arg1, arg2) }
    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.trace(marker, format, *argArray) }
    override fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.trace(marker, msg, t) }

    override val isDebugEnabled: Boolean
        get() = logger.isDebugEnabled

    override fun isDebugEnabled(marker: Marker?): Boolean = logger.isDebugEnabled(marker)
    override fun debug(msg: String?): Mono<Void> = wrap { logger.debug(msg) }
    override fun debug(format: String?, arg: Any?): Mono<Void> = wrap { logger.debug(format, arg) }
    override fun debug(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.debug(format, arg1, arg2) }
    override fun debug(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.debug(format, *arguments) }
    override fun debug(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.debug(msg, t) }
    override fun debug(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.debug(marker, msg) }
    override fun debug(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.debug(marker, format, arg) }
    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.debug(marker, format, arg1, arg2) }
    override fun debug(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.debug(marker, format, *argArray) }
    override fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.debug(marker, msg, t) }

    override val isInfoEnabled: Boolean
        get() = logger.isInfoEnabled

    override fun isInfoEnabled(marker: Marker?): Boolean = logger.isInfoEnabled(marker)
    override fun info(msg: String?): Mono<Void> = wrap { logger.info(msg) }
    override fun info(format: String?, arg: Any?): Mono<Void> = wrap { logger.info(format, arg) }
    override fun info(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.info(format, arg1, arg2) }
    override fun info(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.info(format, *arguments) }
    override fun info(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.info(msg, t) }
    override fun info(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.info(marker, msg) }
    override fun info(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.info(marker, format, arg) }
    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.info(marker, format, arg1, arg2) }
    override fun info(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.info(marker, format, *argArray) }
    override fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.info(marker, msg, t) }

    override val isWarnEnabled: Boolean
        get() = logger.isWarnEnabled

    override fun isWarnEnabled(marker: Marker?): Boolean = logger.isWarnEnabled(marker)
    override fun warn(msg: String?): Mono<Void> = wrap { logger.warn(msg) }
    override fun warn(format: String?, arg: Any?): Mono<Void> = wrap { logger.warn(format, arg) }
    override fun warn(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.warn(format, arg1, arg2) }
    override fun warn(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.warn(format, *arguments) }
    override fun warn(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.warn(msg, t) }
    override fun warn(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.warn(marker, msg) }
    override fun warn(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.warn(marker, format, arg) }
    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.warn(marker, format, arg1, arg2) }
    override fun warn(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.warn(marker, format, *argArray) }
    override fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.warn(marker, msg, t) }

    override val isErrorEnabled: Boolean
        get() = logger.isErrorEnabled

    override fun isErrorEnabled(marker: Marker?): Boolean = logger.isErrorEnabled(marker)
    override fun error(msg: String?): Mono<Void> = wrap { logger.error(msg) }
    override fun error(format: String?, arg: Any?): Mono<Void> = wrap { logger.error(format, arg) }
    override fun error(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.error(format, arg1, arg2) }
    override fun error(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.error(format, *arguments) }
    override fun error(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.error(msg, t) }
    override fun error(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.error(marker, msg) }
    override fun error(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.error(marker, format, arg) }
    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.error(marker, format, arg1, arg2) }
    override fun error(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.error(marker, format, *argArray) }
    override fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.error(marker, msg, t) }
}
