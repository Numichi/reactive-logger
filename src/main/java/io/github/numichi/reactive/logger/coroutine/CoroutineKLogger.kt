package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.CoroutineAbstract
import io.github.numichi.reactive.logger.coroutine.strategies.KLoggerStrategy
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import mu.KLogger
import mu.KotlinLogging
import mu.Marker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineKLogger private constructor(
    val reactiveKLogger: ReactiveKLogger,
    contextKey: CoroutineContext.Key<out CoroutineContext.Element>,
    contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?,
) : CoroutineAbstract<ReactiveKLogger>(
    KLoggerStrategy(reactiveKLogger, contextKey, contextExtractive),
    reactiveKLogger.isEnableError,
    reactiveKLogger.mdcContextKey,
    reactiveKLogger.scheduler
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
        override var logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveLogger::class.java))
    ) : CoroutineAbstract.Builder<E, KLogger, CoroutineKLogger>() {
        @Suppress("UNCHECKED_CAST")
        override fun build(): CoroutineKLogger {
            return CoroutineKLogger(
                ReactiveKLogger(logger, enableError, mdcContextKey, scheduler),
                contextKey,
                contextExtractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
            )
        }
    }

    val logger: Logger
        get() = reactiveKLogger.logger

    //region Trace
    val isTraceEnabled: Boolean
        get() = reactiveKLogger.isTraceEnabled

    fun isTraceEnabled(marker: Marker?): Boolean {
        return reactiveKLogger.isTraceEnabled(marker)
    }

    suspend fun trace(msg: () -> Any?) {
        wrap { it.trace(msg) }
    }

    suspend fun trace(t: Throwable?, msg: () -> Any?) {
        wrap { it.trace(t, msg) }
    }

    suspend fun trace(marker: Marker?, msg: () -> Any?) {
        wrap { it.trace(marker, msg) }
    }

    suspend fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        wrap { it.trace(marker, t, msg) }
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
        get() = reactiveKLogger.isDebugEnabled

    fun isDebugEnabled(marker: Marker?): Boolean {
        return reactiveKLogger.isDebugEnabled(marker)
    }

    suspend fun debug(msg: () -> Any?) {
        wrap { it.debug(msg) }
    }

    suspend fun debug(t: Throwable?, msg: () -> Any?) {
        wrap { it.debug(t, msg) }
    }

    suspend fun debug(marker: Marker?, msg: () -> Any?) {
        wrap { it.debug(marker, msg) }
    }

    suspend fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        wrap { it.debug(marker, t, msg) }
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
        get() = reactiveKLogger.isInfoEnabled

    fun isInfoEnabled(marker: Marker?): Boolean {
        return reactiveKLogger.isInfoEnabled(marker)
    }

    suspend fun info(msg: () -> Any?) {
        wrap { it.info(msg) }
    }

    suspend fun info(t: Throwable?, msg: () -> Any?) {
        wrap { it.info(t, msg) }
    }

    suspend fun info(marker: Marker?, msg: () -> Any?) {
        wrap { it.info(marker, msg) }
    }

    suspend fun info(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        wrap { it.info(marker, t, msg) }
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
        get() = reactiveKLogger.isWarnEnabled

    fun isWarnEnabled(marker: Marker?): Boolean {
        return reactiveKLogger.isWarnEnabled(marker)
    }

    suspend fun warn(msg: () -> Any?) {
        wrap { it.warn(msg) }
    }

    suspend fun warn(t: Throwable?, msg: () -> Any?) {
        wrap { it.warn(t, msg) }
    }

    suspend fun warn(marker: Marker?, msg: () -> Any?) {
        wrap { it.warn(marker, msg) }
    }

    suspend fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        wrap { it.warn(marker, t, msg) }
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
        get() = reactiveKLogger.isErrorEnabled

    fun isErrorEnabled(marker: Marker?): Boolean {
        return reactiveKLogger.isErrorEnabled(marker)
    }

    suspend fun error(msg: () -> Any?) {
        wrap { it.error(msg) }
    }

    suspend fun error(t: Throwable?, msg: () -> Any?) {
        wrap { it.error(t, msg) }
    }

    suspend fun error(marker: Marker?, msg: () -> Any?) {
        wrap { it.error(marker, msg) }
    }

    suspend fun error(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        wrap { it.error(marker, t, msg) }
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

    //region Other
    suspend fun entry(vararg argArray: Any?) {
        wrap { it.entry(*argArray) }
    }

    suspend fun exit() {
        wrap { it.exit() }
    }

    suspend fun <T> exit(result: T): T {
        return wrapResult { it.exit(result) }.second
    }

    suspend fun <T : Throwable> throwing(throwable: T): T {
        return wrapResult { it.throwing(throwable) }
    }

    suspend fun <T : Throwable> catching(throwable: T): T {
        return wrapResult { it.catching(throwable) }
    }
    //endregion
}

