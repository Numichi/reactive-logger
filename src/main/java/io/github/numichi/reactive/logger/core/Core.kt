package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import mu.KLogger
import org.slf4j.Logger
import reactor.core.publisher.Signal
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import java.util.function.BiConsumer
import java.util.function.Consumer

abstract class Core<L : Logger> {

    abstract val logger: L
    abstract val contextKey: String
    abstract val scheduler: Scheduler

    fun wrapRunner(contextView: ContextView, runnable: Runnable) {
        MDCSnapshot.of(readMdc(contextView, contextKey)).use { runnable.run() }
    }

    fun <T> logConsumer(consumer: BiConsumer<Logger, Signal<T>>): Consumer<Signal<T>> {
        return Consumer { signal ->
            wrapRunner(signal.contextView) { consumer.accept(logger, signal) }
        }
    }

    // ------------------------------ DEPRECATED ------------------------------

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", replaceWith = ReplaceWith(""))
    val slf4jLogger: Logger
        get() {
            return if (logger is KLogger) {
                (logger as KLogger).underlyingLogger
            } else {
                logger
            }
        }

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", ReplaceWith("logger.name"))
    val name: String
        get() = logger.name

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", ReplaceWith("logger.name"))
    val mdcContextKey: String
        get() = contextKey
}