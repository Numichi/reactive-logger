package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.readMdc
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

abstract class ReactiveCore<L : Logger> : Core<L>(), RSnapshot {

    override fun snapshot(contextView: ContextView): Mono<MDC> {
        return Mono.just(readMdc(contextView, contextKey))
    }

    override fun snapshot(): Mono<MDC> {
        return Mono.deferContextual { snapshot(it) }
    }

    fun wrap(runnable: Runnable): Mono<ContextView> {
        return Mono.deferContextual { contextView: ContextView ->
            wrapRunner(contextView, runnable)
            Mono.just(contextView)
        }.subscribeOn(scheduler)
    }
}