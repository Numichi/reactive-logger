package io.github.numichi.reactive.logger.spring.handler

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.readOrDefaultMdc
import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import reactor.util.context.Context
import reactor.util.context.ContextView
import reactor.util.function.Tuple2

class ContentHandlerCoroutine(val contextKey: Any) {
    fun modify(context: Context, data: Map<String, String?>): Context {
        return context.modifyMdc(contextKey, data)
    }

    fun modify(context: Context, data: Tuple2<String, String?>): Context {
        return context.modifyMdc(contextKey, data)
    }

    fun modify(context: Context, data: Pair<String, String?>): Context {
        return context.modifyMdc(contextKey, data)
    }

    fun modify(context: Context, data: MDC): Context {
        return context.modifyMdc(contextKey, data.data)
    }

    fun modify(context: Context, func: (MDC) -> MDC): Context {
        return context.modifyMdc(contextKey, func)
    }

    suspend fun read(): MDC {
        return readMdc(contextKey)
    }

    fun read(contextView: ContextView): MDC {
        return readMdc(contextView, contextKey)
    }

    suspend fun readOrDefault(): MDC {
        return readOrDefaultMdc(contextKey)
    }

    fun readOrDefault(contextView: ContextView): MDC {
        return readOrDefaultMdc(contextView, contextKey)
    }

    suspend fun snapshot(): MDC {
        return snapshotMdc(contextKey)
    }

    fun snapshot(contextView: ContextView): MDC {
        return snapshotMdc(contextView, contextKey)
    }
}