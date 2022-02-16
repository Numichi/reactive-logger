package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.coroutine.readMDCResult
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import java.util.*

interface ICore {
    val isEnableError: Boolean
    val mdcContextKey: String
    val scheduler: Scheduler
    val name: String

    fun readMDC(context: ContextView): Optional<Map<String, String>> {
        return Optional.ofNullable(readMDCResult(context, mdcContextKey).getOrNull())
    }

    @Throws(ContextNotExistException::class, InvalidContextDataException::class)
    fun takeMDCSnapshot(context: ContextView): MDCSnapshot {
        val result = readMDCResult(context, mdcContextKey)

        return when {
            result.isSuccess -> MDCSnapshot.of(result.getOrNull())
            result.isFailure && !isEnableError -> MDCSnapshot.empty()
            else -> throw result.exceptionOrNull()!!
        }
    }
}