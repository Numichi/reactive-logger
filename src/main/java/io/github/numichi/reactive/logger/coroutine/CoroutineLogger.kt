package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.CoroutineAbstract
import io.github.numichi.reactive.logger.coroutine.strategies.LoggerStrategy
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineLogger private constructor(
    val reactiveLogger: ReactiveLogger,
    contextKey: CoroutineContext.Key<out CoroutineContext.Element>,
    contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?,
) : CoroutineAbstract<ReactiveLogger>(
    LoggerStrategy(reactiveLogger, contextKey, contextExtractive),
    reactiveLogger.isEnableError,
    reactiveLogger.mdcContextKey,
    reactiveLogger.scheduler
) {

    companion object {
        @JvmStatic
        fun <E : CoroutineContext.Element> builder(
            element: CoroutineContext.Key<E>,
            contextExtractive: suspend (CoroutineContext.Key<out E>) -> Context?
        ): Builder<E> {
            return Builder(element, contextExtractive)
        }

        @JvmStatic
        fun reactorBuilder(): Builder<ReactorContext> {
            return builder(ReactorContext) { coroutineContext[it]?.context }
        }
    }

    class Builder<E : CoroutineContext.Element>(
        override var contextKey: CoroutineContext.Key<E>,
        override var contextExtractive: suspend (CoroutineContext.Key<out E>) -> Context?,
        override var scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler,
        override var mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey,
        override var enableError: Boolean = false,
        override var logger: Logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    ) : CoroutineAbstract.Builder<E, Logger, CoroutineLogger>() {
        @Suppress("UNCHECKED_CAST")
        override fun build(): CoroutineLogger {
            return CoroutineLogger(
                ReactiveLogger(logger, enableError, mdcContextKey, scheduler),
                contextKey,
                contextExtractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
            )
        }
    }

    val logger: Logger
        get() = reactiveLogger.logger

    //region Trace
    val isTraceEnabled: Boolean
        get() = reactiveLogger.isTraceEnabled

    fun isTraceEnabled(marker: Marker?): Boolean {
        return reactiveLogger.isTraceEnabled(marker)
    }

    suspend fun trace(msg: String?) {
        wrap { it.trace(msg) }
    }

    suspend fun trace(format: String?, arg: Any?) {
        wrap { it.trace(format, arg) }
    }

    suspend fun trace(format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.trace(format, arg1, arg2) }
    }

    suspend fun trace(format: String?, vararg arguments: Any?) {
        wrap { it.trace(format, *arguments) }
    }

    suspend fun trace(msg: String?, t: Throwable?) {
        wrap { it.trace(msg, t) }
    }

    suspend fun trace(marker: Marker?, msg: String?) {
        wrap { it.trace(marker, msg) }
    }

    suspend fun trace(marker: Marker?, format: String?, arg: Any?) {
        wrap { it.trace(marker, format, arg) }
    }

    suspend fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.trace(marker, format, arg1, arg2) }
    }

    suspend fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {
        wrap { it.trace(marker, format, *arguments) }
    }

    suspend fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        wrap { it.trace(marker, msg, t) }
    }
    //endregion

    //region Debug
    val isDebugEnabled: Boolean
        get() = reactiveLogger.isDebugEnabled

    fun isDebugEnabled(marker: Marker?): Boolean {
        return reactiveLogger.isDebugEnabled(marker)
    }

    suspend fun debug(msg: String?) {
        wrap { it.debug(msg) }
    }

    suspend fun debug(format: String?, arg: Any?) {
        wrap { it.debug(format, arg) }
    }

    suspend fun debug(format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.debug(format, arg1, arg2) }
    }

    suspend fun debug(format: String?, vararg arguments: Any?) {
        wrap { it.debug(format, *arguments) }
    }

    suspend fun debug(msg: String?, t: Throwable?) {
        wrap { it.debug(msg, t) }
    }

    suspend fun debug(marker: Marker?, msg: String?) {
        wrap { it.debug(marker, msg) }
    }

    suspend fun debug(marker: Marker?, format: String?, arg: Any?) {
        wrap { it.debug(marker, format, arg) }
    }

    suspend fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.debug(marker, format, arg1, arg2) }
    }

    suspend fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        wrap { it.debug(marker, format, *arguments) }
    }

    suspend fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        wrap { it.debug(marker, msg, t) }
    }
    //endregion

    //region Info
    val isInfoEnabled: Boolean
        get() = reactiveLogger.isInfoEnabled

    fun isInfoEnabled(marker: Marker?): Boolean {
        return reactiveLogger.isInfoEnabled(marker)
    }

    suspend fun info(msg: String?) {
        wrap { it.info(msg) }
    }

    suspend fun info(format: String?, arg: Any?) {
        wrap { it.info(format, arg) }
    }

    suspend fun info(format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.info(format, arg1, arg2) }
    }

    suspend fun info(format: String?, vararg arguments: Any?) {
        wrap { it.info(format, *arguments) }
    }

    suspend fun info(msg: String?, t: Throwable?) {
        wrap { it.info(msg, t) }
    }

    suspend fun info(marker: Marker?, msg: String?) {
        wrap { it.info(marker, msg) }
    }

    suspend fun info(marker: Marker?, format: String?, arg: Any?) {
        wrap { it.info(marker, format, arg) }
    }

    suspend fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.info(marker, format, arg1, arg2) }
    }

    suspend fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        wrap { it.info(marker, format, *arguments) }
    }

    suspend fun info(marker: Marker?, msg: String?, t: Throwable?) {
        wrap { it.info(marker, msg, t) }
    }
    //endregion

    //region Warning
    val isWarnEnabled: Boolean
        get() = reactiveLogger.isWarnEnabled

    fun isWarnEnabled(marker: Marker?): Boolean {
        return reactiveLogger.isWarnEnabled(marker)
    }

    suspend fun warn(msg: String?) {
        wrap { it.warn(msg) }
    }

    suspend fun warn(format: String?, arg: Any?) {
        wrap { it.warn(format, arg) }
    }

    suspend fun warn(format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.warn(format, arg1, arg2) }
    }

    suspend fun warn(format: String?, vararg arguments: Any?) {
        wrap { it.warn(format, *arguments) }
    }

    suspend fun warn(msg: String?, t: Throwable?) {
        wrap { it.warn(msg, t) }
    }

    suspend fun warn(marker: Marker?, msg: String?) {
        wrap { it.warn(marker, msg) }
    }

    suspend fun warn(marker: Marker?, format: String?, arg: Any?) {
        wrap { it.warn(marker, format, arg) }
    }

    suspend fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.warn(marker, format, arg1, arg2) }
    }

    suspend fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        wrap { it.warn(marker, format, *arguments) }
    }

    suspend fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        wrap { it.warn(marker, msg, t) }
    }
    //endregion

    //region Error
    val isErrorEnabled: Boolean
        get() = reactiveLogger.isErrorEnabled

    fun isErrorEnabled(marker: Marker?): Boolean {
        return reactiveLogger.isErrorEnabled(marker)
    }

    suspend fun error(msg: String?) {
        wrap { it.error(msg) }
    }

    suspend fun error(format: String?, arg: Any?) {
        wrap { it.error(format, arg) }
    }

    suspend fun error(format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.error(format, arg1, arg2) }
    }

    suspend fun error(format: String?, vararg arguments: Any?) {
        wrap { it.error(format, *arguments) }
    }

    suspend fun error(msg: String?, t: Throwable?) {
        wrap { it.error(msg, t) }
    }

    suspend fun error(marker: Marker?, msg: String?) {
        wrap { it.error(marker, msg) }
    }

    suspend fun error(marker: Marker?, format: String?, arg: Any?) {
        wrap { it.error(marker, format, arg) }
    }

    suspend fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        wrap { it.error(marker, format, arg1, arg2) }
    }

    suspend fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        wrap { it.error(marker, format, *arguments) }
    }

    suspend fun error(marker: Marker?, msg: String?, t: Throwable?) {
        wrap { it.error(marker, msg, t) }
    }
    //endregion
}

