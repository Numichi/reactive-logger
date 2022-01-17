package hu.numichi.kotlin.reactive.logger

import kotlinx.coroutines.reactor.ReactorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ReactiveLoggerBuilder<T : CoroutineContext.Element> {
    private var scheduler = DEFAULT_SCHEDULER
    private var logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    private var mdcContextKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY

    private var contextKey: CoroutineContext.Key<T>? = null
    private var contextExtractive: suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context? = {
        coroutineContext[ReactorContext]?.context
    }

    fun withLogger(logger: Class<*>): ReactiveLoggerBuilder<T> {
        this.logger = LoggerFactory.getLogger(logger)
        return this
    }

    fun withLogger(logger: String): ReactiveLoggerBuilder<T> {
        this.logger = LoggerFactory.getLogger(logger)
        return this
    }

    fun withLogger(logger: Logger): ReactiveLoggerBuilder<T> {
        this.logger = logger
        return this
    }

    fun withScheduler(scheduler: Scheduler): ReactiveLoggerBuilder<T> {
        this.scheduler = scheduler
        return this
    }

    fun withMDCContextKey(mdcContextKey: String): ReactiveLoggerBuilder<T> {
        require(mdcContextKey.trim { it <= ' ' }.isNotEmpty()) { "MDC context key must not be blank" }

        this.mdcContextKey = mdcContextKey
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun withContext(key: CoroutineContext.Key<T>, extractive: suspend (CoroutineContext.Key<T>) -> Context?): ReactiveLoggerBuilder<T> {
        this.contextExtractive = extractive as suspend (CoroutineContext.Key<out CoroutineContext.Element>) -> Context?
        this.contextKey = key
        return this
    }

    fun build(): ReactiveLogger {
        return ReactiveLogger(
            hu.numichi.reactive.logger.ReactiveLogger.builder()
                .withLogger(logger)
                .withScheduler(scheduler)
                .withMDCContextKey(mdcContextKey)
                .build(),
            contextKey ?: ReactorContext.Key,
            contextExtractive
        )
    }
}