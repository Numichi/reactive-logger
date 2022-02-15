package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.abstracts.ICore
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView

interface IReactorCore : ICore {
    val logger: Logger

    override val name: String
        get() = logger.name

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

    fun wrap(runnable: Runnable): Mono<Context> {
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