package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.java.ReactiveLogger as JReactiveLogger
import hu.numichi.reactive.logger.Consts
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import reactor.util.context.ContextView
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ReactiveLogger private constructor(
    private val reactiveLogger: JReactiveLogger,
    private val contextKey: CoroutineContext.Key<out CoroutineContext.Element>,
    private val contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?,
) {

    companion object {
        @JvmStatic
        fun defaultBuilder() = Builder<ReactorContext>()
            .withContext(ReactorContext) { coroutineContext[it]?.context }

        @JvmStatic
        fun <E : CoroutineContext.Element> builder() = Builder<E>()
    }

    fun imperative(): Logger = reactiveLogger.imperative()
    fun scheduler(): Scheduler = reactiveLogger.scheduler()
    fun readMDC(context: Context): Optional<Map<String, String>> = reactiveLogger.readMDC(context)
    fun getName(): String? = reactiveLogger.name

    //region Trace
    fun isTraceEnabled(): Boolean {
        return reactiveLogger.isTraceEnabled
    }

    suspend fun trace(msg: String) {
        return wrap { logger -> logger.trace(msg) }
    }

    suspend fun trace(format: String, vararg arguments: Any) {
        return wrap { logger -> logger.trace(format, *arguments) }
    }

    suspend fun trace(msg: String, t: Throwable) {
        return wrap { logger -> logger.trace(msg, t) }
    }

    fun isTraceEnabled(marker: Marker): Boolean {
        return reactiveLogger.isTraceEnabled(marker)
    }

    suspend fun trace(marker: Marker, msg: String) {
        return wrap { logger -> logger.trace(marker, msg) }
    }

    suspend fun trace(marker: Marker, format: String, vararg argArray: Any) {
        return wrap { logger -> logger.trace(marker, format, *argArray) }
    }

    suspend fun trace(marker: Marker, msg: String, t: Throwable) {
        return wrap { logger -> logger.trace(marker, msg, t) }
    }
    //endregion

    //region Debug
    fun isDebugEnabled(): Boolean {
        return reactiveLogger.isDebugEnabled
    }

    suspend fun debug(msg: String) {
        return wrap { logger -> logger.debug(msg) }
    }

    suspend fun debug(format: String, vararg arguments: Any) {
        return wrap { logger -> logger.debug(format, *arguments) }
    }

    suspend fun debug(msg: String, t: Throwable) {
        return wrap { logger -> logger.debug(msg, t) }
    }

    fun isDebugEnabled(marker: Marker): Boolean {
        return reactiveLogger.isDebugEnabled(marker)
    }

    suspend fun debug(marker: Marker, msg: String) {
        return wrap { logger -> logger.debug(marker, msg) }
    }

    suspend fun debug(marker: Marker, format: String, vararg arguments: Any) {
        return wrap { logger -> logger.debug(marker, format, *arguments) }
    }

    suspend fun debug(marker: Marker, msg: String, t: Throwable) {
        return wrap { logger -> logger.debug(marker, msg, t) }
    }
    //endregion

    //region Info
    fun isInfoEnabled(): Boolean {
        return reactiveLogger.isInfoEnabled
    }

    suspend fun info(msg: String) {
        return wrap { logger -> logger.info(msg) }
    }

    suspend fun info(format: String, vararg arguments: Any) {
        return wrap { logger -> logger.info(format, *arguments) }
    }

    suspend fun info(msg: String, t: Throwable) {
        return wrap { logger -> logger.info(msg, t) }
    }

    fun isInfoEnabled(marker: Marker): Boolean {
        return reactiveLogger.isInfoEnabled(marker)
    }

    suspend fun info(marker: Marker, msg: String) {
        return wrap { logger -> logger.info(marker, msg) }
    }

    suspend fun info(marker: Marker, format: String, vararg arguments: Any) {
        return wrap { logger -> logger.info(marker, format, *arguments) }
    }

    suspend fun info(marker: Marker, msg: String, t: Throwable) {
        return wrap { logger -> logger.info(marker, msg, t) }
    }
    //endregion

    //region Warn
    fun isWarnEnabled(): Boolean {
        return reactiveLogger.isWarnEnabled
    }

    suspend fun warn(msg: String) {
        return wrap { logger -> logger.warn(msg) }
    }

    suspend fun warn(format: String, arg1: Any, arg2: Any) {
        return wrap { logger -> logger.warn(format, arg1, arg2) }
    }

    suspend fun warn(msg: String, t: Throwable) {
        return wrap { logger -> logger.warn(msg, t) }
    }

    fun isWarnEnabled(marker: Marker): Boolean {
        return reactiveLogger.isWarnEnabled(marker)
    }

    suspend fun warn(marker: Marker, msg: String) {
        return wrap { logger -> logger.warn(marker, msg) }
    }

    suspend fun warn(marker: Marker, format: String, vararg arguments: Any) {
        return wrap { logger -> logger.warn(marker, format, *arguments) }
    }

    suspend fun warn(marker: Marker, msg: String, t: Throwable) {
        return wrap { logger -> logger.warn(marker, msg, t) }
    }
    //endregion

    //region Error
    fun isErrorEnabled(): Boolean {
        return reactiveLogger.isErrorEnabled
    }

    suspend fun error(msg: String) {
        return wrap { logger -> logger.error(msg) }
    }

    suspend fun error(format: String, vararg arguments: Any) {
        return wrap { logger -> logger.error(format, *arguments) }
    }

    suspend fun error(msg: String, t: Throwable) {
        return wrap { logger -> logger.error(msg, t) }
    }

    fun isErrorEnabled(marker: Marker): Boolean {
        return reactiveLogger.isErrorEnabled(marker)
    }

    suspend fun error(marker: Marker, msg: String) {
        return wrap { logger -> logger.error(marker, msg) }
    }

    suspend fun error(marker: Marker, format: String, vararg arguments: Any) {
        return wrap { logger -> logger.error(marker, format, *arguments) }
    }

    suspend fun error(marker: Marker, msg: String, t: Throwable) {
        return wrap { logger -> logger.error(marker, msg, t) }
    }
    //endregion

    private suspend fun wrap(fn: (JReactiveLogger) -> Mono<Context>) {
        val key = coroutineContext[contextKey] as CoroutineContext.Key<out CoroutineContext.Element>
        val context: ContextView = contextExtractive(key)?.readOnly() ?: Context.empty().readOnly()

        mono { fn(reactiveLogger) }
            .contextWrite { it.putAll(context) }
            .awaitSingleOrNull()
    }

    class Builder<T : CoroutineContext.Element> {
        private var scheduler = Consts.DEFAULT_SCHEDULER
        private var logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
        private var mdcContextKey = Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY
        private var contextKey: CoroutineContext.Key<T>? = null
        private var contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context? = {
            coroutineContext[ReactorContext]?.context
        }

        fun withLogger(logger: Class<*>): Builder<T> {
            this.logger = LoggerFactory.getLogger(logger)
            return this
        }

        fun withLogger(logger: String): Builder<T> {
            this.logger = LoggerFactory.getLogger(logger)
            return this
        }

        fun withLogger(logger: Logger): Builder<T> {
            this.logger = logger
            return this
        }

        fun withScheduler(scheduler: Scheduler): Builder<T> {
            this.scheduler = scheduler
            return this
        }

        fun withMDCContextKey(mdcContextKey: String): Builder<T> {
            require(mdcContextKey.trim { it <= ' ' }.isNotEmpty()) { "MDC context key must not be blank" }

            this.mdcContextKey = mdcContextKey
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun withContext(key: CoroutineContext.Key<T>, extractive: suspend (CoroutineContext.Key<T>) -> Context?): Builder<T> {
            this.contextExtractive = extractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
            this.contextKey = key
            return this
        }

        fun build(): ReactiveLogger {
            return ReactiveLogger(
                JReactiveLogger.builder()
                    .withLogger(logger)
                    .withScheduler(scheduler)
                    .withMDCContextKey(mdcContextKey)
                    .build(),
                contextKey ?: ReactorContext,
                contextExtractive
            )
        }
    }
}

