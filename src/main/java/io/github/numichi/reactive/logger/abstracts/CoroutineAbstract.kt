package io.github.numichi.reactive.logger.abstracts

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.strategies.WrapStrategy
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates

abstract class CoroutineAbstract<T>(
    private val strategy: WrapStrategy<T>,
    isEnableError: Boolean,
    mdcContextKey: String,
    scheduler: Scheduler
) : MainAbstract(
    isEnableError,
    mdcContextKey,
    scheduler
) {

    val name: String
        get() = (strategy.getLogger() as ReactiveAbstract).name

    suspend fun snapshot(context: Context? = null): MDC? {
        val ctx = context ?: coroutineContext[ReactorContext]?.context
        return ctx?.let { (strategy.getLogger() as ReactiveAbstract).snapshot(it).awaitSingleOrNull() }
    }

    protected suspend fun wrap(fn: (T) -> Mono<*>) {
        strategy.wrap { fn(it) }
    }

    protected suspend fun <R> wrapResult(fn: (T) -> Mono<R>): R {
        return strategy.wrap { fn(it) }
    }

    abstract class Builder<T : CoroutineContext.Element, L : Logger, R> {
        open lateinit var logger: L
        open lateinit var contextKey: CoroutineContext.Key<T>
        open lateinit var contextExtractive: suspend (CoroutineContext.Key<out T>) -> Context?
        open lateinit var scheduler: Scheduler
        open lateinit var mdcContextKey: String
        open var enableError by Delegates.notNull<Boolean>()

        fun withScheduler(scheduler: Scheduler): Builder<T, L, R> {
            this.scheduler = scheduler
            return this
        }

        fun withMDCContextKey(mdcContextKey: String): Builder<T, L, R> {
            this.mdcContextKey = mdcContextKey
            return this
        }

        fun withContextKey(contextKey: CoroutineContext.Key<T>): Builder<T, L, R> {
            this.contextKey = contextKey
            return this
        }

        fun withContextExtractive(contextExtractive: suspend (CoroutineContext.Key<out T>) -> Context?): Builder<T, L, R> {
            this.contextExtractive = contextExtractive
            return this
        }

        fun withEnableError(enableError: Boolean): Builder<T, L, R> {
            this.enableError = enableError
            return this
        }

        fun withLogger(logger: L): Builder<T, L, R> {
            this.logger = logger
            return this
        }

        abstract fun build(): R
    }
}