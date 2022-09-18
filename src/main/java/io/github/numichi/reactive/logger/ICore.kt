package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.coroutine.readMdc as readMdcFn
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import mu.KLogger
import org.slf4j.Logger
import reactor.core.publisher.Signal
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import java.util.function.BiConsumer
import java.util.function.Consumer

interface ICore {
    val mdcContextKey: String
    val scheduler: Scheduler
    val name: String
    /** NOT USE FOR LOGGING */
    val logger: Logger
    /** NOT USE FOR LOGGING */
    val slf4jLogger: Logger
        get() {
            return if (logger is KLogger) {
                (logger as KLogger).underlyingLogger
            } else {
                logger
            }
        }

    fun readMDC(context: ContextView): Map<String, String> {
        return readMdcFn(context, mdcContextKey)
    }

    fun takeMDCSnapshot(context: ContextView): MDCSnapshot {
        val result = readMdcFn(context, mdcContextKey)
        return MDCSnapshot.of(result)
    }

    fun <T> logConsumer(fn: BiConsumer<Logger, Signal<T>>): Consumer<Signal<T>> {
        return Consumer { signal ->
            takeMDCSnapshot(signal.contextView).use { fn.accept(slf4jLogger, signal) }
        }
    }
}