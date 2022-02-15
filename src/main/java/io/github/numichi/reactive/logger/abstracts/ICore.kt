package io.github.numichi.reactive.logger.abstracts

import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import java.util.*

interface ICore {
    val isEnableError: Boolean
    val mdcContextKey: String
    val scheduler: Scheduler
    val name: String

    fun readMDC(context: Context): Optional<Map<String, String>> {
        return context.getOrEmpty(mdcContextKey)
    }

    @Throws(ContextNotExistException::class)
    fun takeMDCSnapshot(context: Context): MDCSnapshot {
        val optionalSnapshot = readMDC(context).map { MDCSnapshot.of(it) }

        return if (isEnableError) {
            optionalSnapshot.orElseThrow { ContextNotExistException("\"$mdcContextKey\" context not found") }
        } else {
            optionalSnapshot.orElseGet { MDCSnapshot.empty() }
        }
    }
}