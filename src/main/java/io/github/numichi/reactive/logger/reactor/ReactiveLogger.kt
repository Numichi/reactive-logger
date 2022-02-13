package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.ReactiveAbstract
import mu.KLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context

class ReactiveLogger(
    val logger: Logger,
    isEnableError: Boolean,
    mdcContextKey: String,
    scheduler: Scheduler,
) : ReactiveAbstract(logger, isEnableError, mdcContextKey, scheduler) {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(
        override var scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler,
        override var mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey,
        override var enableError: Boolean = false,
        override var logger: Logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    ) : ReactiveAbstract.Builder<Logger, ReactiveLogger>() {
        override fun build(): ReactiveLogger {
            return ReactiveLogger(logger, enableError, mdcContextKey, scheduler)
        }
    }

    //region Trace
    val isTraceEnabled: Boolean
        get() = logger.isTraceEnabled

    fun isTraceEnabled(marker: Marker?): Boolean {
        return logger.isTraceEnabled(marker)
    }

    fun trace(msg: String?): Mono<Context> {
        return wrap { logger.trace(msg) }
    }

    fun trace(format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.trace(format, arg) }
    }

    fun trace(format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.trace(format, arg1, arg2) }
    }

    fun trace(format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.trace(format, *arguments) }
    }

    fun trace(msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.trace(msg, t) }
    }

    fun trace(marker: Marker?, msg: String?): Mono<Context> {
        return wrap { logger.trace(marker, msg) }
    }

    fun trace(marker: Marker?, format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.trace(marker, format, arg) }
    }

    fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.trace(marker, format, arg1, arg2) }
    }

    fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Context> {
        return wrap { logger.trace(marker, format, *argArray) }
    }

    fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.trace(marker, msg, t) }
    }
    //endregion

    //region Debug
    val isDebugEnabled: Boolean
        get() = logger.isDebugEnabled

    fun isDebugEnabled(marker: Marker?): Boolean {
        return logger.isDebugEnabled(marker)
    }

    fun debug(msg: String?): Mono<Context> {
        return wrap { logger.debug(msg) }
    }

    fun debug(format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.debug(format, arg) }
    }

    fun debug(format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.debug(format, arg1, arg2) }
    }

    fun debug(format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.debug(format, *arguments) }
    }

    fun debug(msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.debug(msg, t) }
    }

    fun debug(marker: Marker?, msg: String?): Mono<Context> {
        return wrap { logger.debug(marker, msg) }
    }

    fun debug(marker: Marker?, format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.debug(marker, format, arg) }
    }

    fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.debug(marker, format, arg1, arg2) }
    }

    fun debug(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Context> {
        return wrap { logger.debug(marker, format, *argArray) }
    }

    fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.debug(marker, msg, t) }
    }
    //endregion

    //region Info
    val isInfoEnabled: Boolean
        get() = logger.isInfoEnabled

    fun isInfoEnabled(marker: Marker?): Boolean {
        return logger.isInfoEnabled(marker)
    }

    fun info(msg: String?): Mono<Context> {
        return wrap { logger.info(msg) }
    }

    fun info(format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.info(format, arg) }
    }

    fun info(format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.info(format, arg1, arg2) }
    }

    fun info(format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.info(format, *arguments) }
    }

    fun info(msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.info(msg, t) }
    }

    fun info(marker: Marker?, msg: String?): Mono<Context> {
        return wrap { logger.info(marker, msg) }
    }

    fun info(marker: Marker?, format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.info(marker, format, arg) }
    }

    fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.info(marker, format, arg1, arg2) }
    }

    fun info(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Context> {
        return wrap { logger.info(marker, format, *argArray) }
    }

    fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.info(marker, msg, t) }
    }
    //endregion

    //region Warn
    val isWarnEnabled: Boolean
        get() = logger.isWarnEnabled

    fun isWarnEnabled(marker: Marker?): Boolean {
        return logger.isWarnEnabled(marker)
    }

    fun warn(msg: String?): Mono<Context> {
        return wrap { logger.warn(msg) }
    }

    fun warn(format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.warn(format, arg) }
    }

    fun warn(format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.warn(format, arg1, arg2) }
    }

    fun warn(format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.warn(format, *arguments) }
    }

    fun warn(msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.warn(msg, t) }
    }

    fun warn(marker: Marker?, msg: String?): Mono<Context> {
        return wrap { logger.warn(marker, msg) }
    }

    fun warn(marker: Marker?, format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.warn(marker, format, arg) }
    }

    fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.warn(marker, format, arg1, arg2) }
    }

    fun warn(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Context> {
        return wrap { logger.warn(marker, format, *argArray) }
    }

    fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.warn(marker, msg, t) }
    }
    //endregion

    //region Error
    val isErrorEnabled: Boolean
        get() = logger.isErrorEnabled

    fun isErrorEnabled(marker: Marker?): Boolean {
        return logger.isErrorEnabled(marker)
    }

    fun error(msg: String?): Mono<Context> {
        return wrap { logger.error(msg) }
    }

    fun error(format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.error(format, arg) }
    }

    fun error(format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.error(format, arg1, arg2) }
    }

    fun error(format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.error(format, *arguments) }
    }

    fun error(msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.error(msg, t) }
    }

    fun error(marker: Marker?, msg: String?): Mono<Context> {
        return wrap { logger.error(marker, msg) }
    }

    fun error(marker: Marker?, format: String?, arg: Any?): Mono<Context> {
        return wrap { logger.error(marker, format, arg) }
    }

    fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Context> {
        return wrap { logger.error(marker, format, arg1, arg2) }
    }

    fun error(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Context> {
        return wrap { logger.error(marker, format, *argArray) }
    }

    fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.error(marker, msg, t) }
    }
    //endregion
}
