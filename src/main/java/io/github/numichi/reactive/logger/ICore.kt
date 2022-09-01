package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.coroutine.readMdc as helperReadMDC
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView

interface ICore {
    val mdcContextKey: String
    val scheduler: Scheduler
    val name: String

    fun readMDC(context: ContextView): Map<String, String> {
        return helperReadMDC(context, mdcContextKey)
    }

    fun takeMDCSnapshot(context: ContextView): MDCSnapshot {
        val result = helperReadMDC(context, mdcContextKey)
        return MDCSnapshot.of(result)
    }
}