package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import reactor.core.publisher.Mono
import reactor.util.context.ContextView
import java.util.function.Supplier

abstract class ReactiveCore<L> : Core<L>(), RSnapshot {
    override fun snapshot(contextView: ContextView): Mono<MDC> {
        return Mono.just(snapshotMdc(contextView, contextKey))
    }

    override fun snapshot(): Mono<MDC> {
        return Mono.deferContextual { snapshot(it) }
    }

    fun wrap(runnable: Runnable): Mono<Void> {
        return wrap(Supplier { runnable.run() }).then()
    }

    fun <T> wrap(supplier: Supplier<T>): Mono<T> {
        return Mono.deferContextual { contextView: ContextView ->
            val result = runCatching { wrapRunner(contextView, supplier) }
            val exception = result.exceptionOrNull()
            val value = result.getOrNull()

            if (exception == null) {
                Mono.justOrEmpty(value)
            } else {
                Mono.error(exception)
            }
        }.subscribeOn(scheduler)
    }
}
