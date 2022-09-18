package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.ICore
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

interface IReactorCore : ICore {
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
            takeMDCSnapshot(contextView).use { runnable.run() }
            Mono.just(contextView)
        }.subscribeOn(scheduler)
    }
}