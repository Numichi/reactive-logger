package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import org.slf4j.Logger
import reactor.core.publisher.Signal
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

abstract class Core<L> {
    abstract val logger: L
    abstract val contextKey: Any
    abstract val scheduler: Scheduler

    fun wrapRunner(contextView: ContextView, runnable: Runnable) {
        wrapRunner(contextView, Supplier { runnable.run() })
    }

    fun <T> wrapRunner(contextView: ContextView, supplier: Supplier<T>): T {
        return MDCSnapshot.of(snapshotMdc(contextView, contextKey).data).use {
            supplier.get()
        }
    }

    fun <T> logOnEach(consumer: BiConsumer<L, Signal<T>>): Consumer<Signal<T>> {
        return Consumer { signal ->
            wrapRunner(signal.contextView) { consumer.accept(logger, signal) }
        }
    }

    fun <V> logOnSignal(signal: Signal<V>, fn: Consumer<L>) {
        wrapRunner(signal.contextView) { fn.accept(logger) }
    }
}