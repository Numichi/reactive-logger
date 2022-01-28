package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.ReactiveLogger as JReactiveLogger
import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
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
        fun builder(): Builder<ReactorContext> {
            return builder(ReactorContext).withContext { reactorContextOrEmpty() }
        }

        @JvmStatic
        fun <T : CoroutineContext.Element> builder(coroutineKey: CoroutineContext.Key<T>): Builder<T> {
            return Builder(coroutineKey)
        }
    }

    fun readMDC(context: Context): Optional<Map<String, String>> = reactiveLogger.readMDC(context)

    suspend fun snapshot(context: Context? = null): MDC? {
        val ctx = context ?: coroutineContext[ReactorContext]?.context
        return reactiveLogger.snapshot(ctx).awaitSingleOrNull()
    }

    val imperative: Logger
        get() = reactiveLogger.imperative()

    val scheduler: Scheduler
        get() = reactiveLogger.scheduler()

    val name: String
        get() = reactiveLogger.name

    val mdcContextKey: String
        get() = reactiveLogger.mdcContextKey()

    //region Trace
    val isTraceEnabled: Boolean
        get() = reactiveLogger.isTraceEnabled

    
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
    val isDebugEnabled: Boolean
        get() = reactiveLogger.isDebugEnabled

    
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
    val isInfoEnabled: Boolean
        get() = reactiveLogger.isInfoEnabled

    
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
    val isWarnEnabled: Boolean
        get() = reactiveLogger.isWarnEnabled


    suspend fun warn(msg: String) {
        return wrap { logger -> logger.warn(msg) }
    }

    
    suspend fun warn(format: String, vararg arguments: Any) {
        return wrap { logger -> logger.warn(format, *arguments) }
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
    val isErrorEnabled: Boolean
        get() = reactiveLogger.isErrorEnabled

    
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
        val context = contextExtractive(contextKey)

        requireNotNull(context) { "Context configuration is null! The resulting context is null. Use withContext via Builder." }

        fn(reactiveLogger)
            .contextWrite { it.putAll(context.readOnly()) }
            .awaitSingleOrNull()
    }

    class Builder<T : CoroutineContext.Element>(private val contextKey: CoroutineContext.Key<T>) {
        private var scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler
        private var logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
        private var mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey!!
        private var contextExtractive: suspend (CoroutineContext.Key<out T>) -> Context?
        private var enableError: Boolean = false

        init {
            contextExtractive = { null }
        }

        fun enableError(): Builder<T> {
            enableError = true
            return this
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
            require(mdcContextKey.trim().isNotEmpty()) { "MDC context key must not be blank" }

            this.mdcContextKey = mdcContextKey
            return this
        }

        fun withContext(extractive: suspend (CoroutineContext.Key<out T>) -> Context?): Builder<T> {
            this.contextExtractive = extractive
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun build(): ReactiveLogger {
            val jLogger = JReactiveLogger.builder()
                .withLogger(logger)
                .withScheduler(scheduler)
                .withMDCContextKey(mdcContextKey)

            if (enableError) {
                jLogger.enableError()
            }

            return ReactiveLogger(
                jLogger.build(),
                contextKey,
                contextExtractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?,
            )
        }
    }
}

