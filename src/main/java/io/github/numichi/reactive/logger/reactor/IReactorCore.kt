package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.models.MDC
import io.github.numichi.reactive.logger.ICore
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.util.context.Context
import reactor.util.context.ContextView

interface IReactorCore : ICore {
    val logger: Logger

    override val name: String
        get() = logger.name

    fun snapshot(context: ContextView): Mono<MDC> {
        var mdc: MDC
        takeMDCSnapshot(context).use {
            mdc = MDC(mdcContextKey, it.copyOfContextMap)
        }
        return Mono.just(mdc)
    }

    fun wrap(runnable: Runnable): Mono<ContextView> {
        return Mono.deferContextual { contextView: ContextView ->
            try {
                takeMDCSnapshot(contextView).use { runnable.run() }
            } catch (exception: ContextNotExistException) {
                return@deferContextual Mono.error<Context>(exception)
            }

            Mono.just(contextView)
        }.subscribeOn(scheduler)
    }
}