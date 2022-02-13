package io.github.numichi.reactive.logger.abstracts

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.properties.Delegates

abstract class ReactiveAbstract(
    private var logger: Logger,
    isEnableError: Boolean,
    mdcContextKey: String,
    scheduler: Scheduler
) : MainAbstract(
    isEnableError,
    mdcContextKey,
    scheduler
) {

    val name: String
        get() = logger.name

    abstract class Builder<L : Logger, R> {
        open lateinit var scheduler: Scheduler
        open lateinit var mdcContextKey: String
        open var enableError by Delegates.notNull<Boolean>()
        open lateinit var logger: L

        fun withScheduler(scheduler: Scheduler): Builder<L, R> {
            this.scheduler = scheduler
            return this;
        }

        fun withMDCContextKey(mdcContextKey: String): Builder<L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this;
        }

        fun withEnableError(enableError: Boolean): Builder<L, R> {
            this.enableError = enableError
            return this;
        }

        fun withLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this;
        }

        abstract fun build(): R
    }

    fun snapshot(context: Context): Mono<MDC> {
        return try {
            var mdc: MDC
            takeMDCSnapshot(context).use {
                mdc = MDC(mdcContextKey, it.copyOfContextMap)
            }
            Mono.just(mdc)
        } catch (exception: Exception) {
            Mono.error(exception)
        }
    }

    protected fun wrap(runnable: Runnable): Mono<Context> {
        return Mono.deferContextual { contextView: ContextView ->
            val context = Context.of(contextView)

            try {
                takeMDCSnapshot(context).use { runnable.run() }
            } catch (exception: ContextNotExistException) {
                return@deferContextual Mono.error<Context>(exception)
            }

            Mono.just(context)
        }.subscribeOn(scheduler)
    }
}