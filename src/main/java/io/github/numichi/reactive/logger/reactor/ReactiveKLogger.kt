package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.ReactiveAbstract
import mu.KLogger
import mu.KotlinLogging
import mu.Marker
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context

class ReactiveKLogger(
    val logger: KLogger,
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
        override var logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveKLogger::class.java))
    ) : ReactiveAbstract.Builder<KLogger, ReactiveKLogger>() {
        override fun build(): ReactiveKLogger {
            return ReactiveKLogger(logger, enableError, mdcContextKey, scheduler)
        }
    }

    //region Debug
    val isDebugEnabled: Boolean
        get() = logger.isDebugEnabled

    fun isDebugEnabled(marker: Marker?): Boolean {
        return logger.isDebugEnabled(marker)
    }

    fun debug(msg: () -> Any?): Mono<Context> {
        return wrap { logger.debug(msg) }
    }

    fun debug(t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.debug(t, msg) }
    }

    fun debug(marker: Marker?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.debug(marker, msg) }
    }

    fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.debug(marker, t, msg) }
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

    fun debug(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.debug(marker, format, *arguments) }
    }

    fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.debug(marker, msg, t) }
    }
    //endregion

    //region Error
    val isErrorEnabled: Boolean
        get() = logger.isErrorEnabled

    fun isErrorEnabled(marker: Marker?): Boolean {
        return logger.isErrorEnabled(marker)
    }

    fun error(msg: () -> Any?): Mono<Context> {
        return wrap { logger.error(msg) }
    }

    fun error(t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.error(t, msg) }
    }

    fun error(marker: Marker?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.error(marker, msg) }
    }

    fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.error(marker, t, msg) }
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

    fun error(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.error(marker, format, *arguments) }
    }

    fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.error(marker, msg, t) }
    }
    //endregion

    //region Info
    val isInfoEnabled: Boolean
        get() = logger.isInfoEnabled

    fun isInfoEnabled(marker: Marker?): Boolean {
        return logger.isInfoEnabled(marker)
    }

    fun info(msg: () -> Any?): Mono<Context> {
        return wrap { logger.info(msg) }
    }

    fun info(t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.info(t, msg) }
    }

    fun info(marker: Marker?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.info(marker, msg) }
    }

    fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.info(marker, t, msg) }
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

    fun info(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.info(marker, format, *arguments) }
    }

    fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.info(marker, msg, t) }
    }
    //endregion

    //region Trace
    val isTraceEnabled: Boolean
        get() = logger.isTraceEnabled

    fun isTraceEnabled(marker: Marker?): Boolean {
        return logger.isTraceEnabled(marker)
    }

    fun trace(msg: () -> Any?): Mono<Context> {
        return wrap { logger.trace(msg) }
    }

    fun trace(t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.trace(t, msg) }
    }

    fun trace(marker: Marker?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.trace(marker, msg) }
    }

    fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.trace(marker, t, msg) }
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

    fun trace(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.trace(marker, format, *arguments) }
    }

    fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.trace(marker, msg, t) }
    }
    //endregion

    //region Warning
    val isWarnEnabled: Boolean
        get() = logger.isWarnEnabled

    fun isWarnEnabled(marker: Marker?): Boolean {
        return logger.isWarnEnabled(marker)
    }

    fun warn(msg: () -> Any?): Mono<Context> {
        return wrap { logger.warn(msg) }
    }

    fun warn(t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.warn(t, msg) }
    }

    fun warn(marker: Marker?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.warn(marker, msg) }
    }

    fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Context> {
        return wrap { logger.warn(marker, t, msg) }
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

    fun warn(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Context> {
        return wrap { logger.warn(marker, format, *arguments) }
    }

    fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<Context> {
        return wrap { logger.warn(marker, msg, t) }
    }
    //endregion

    //region Other
    fun entry(vararg argArray: Any?): Mono<Context> {
        return wrap { logger.entry(*argArray) }
    }

    fun exit(): Mono<Context> {
        return wrap { logger.exit() }
    }

    fun <T> exit(result: T): Mono<Pair<Context, T>> {
        return wrap { logger.exit(result) }.map { Pair(it, result) }
    }

    fun <T : Throwable> throwing(throwable: T): Mono<T> {
        return wrap { logger.throwing(throwable) }.map { throwable }
    }

    fun <T : Throwable> catching(throwable: T): Mono<T> {
        return wrap { logger.catching(throwable) }.map { throwable }
    }
    //endregion
}
