package io.github.numichi.reactive.logger.kotlin

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.Values
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import reactor.util.context.ContextView

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith("readMDC()", "io.github.numichi.reactive.logger.coroutine")
)
suspend fun readMDC(): MDC {
    return readMDC(Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY)
}

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith("readMDC(mdcContextKey)", "io.github.numichi.reactive.logger.coroutine")
)
suspend fun readMDC(mdcContextKey: String): MDC {
    return readMDC(rectorContext(), mdcContextKey)
}

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith("readMDC(contextView)", "io.github.numichi.reactive.logger.coroutine")
)
fun readMDC(contextView: ContextView?): MDC {
    return readMDC(contextView, Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY)
}

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith("readMDC(contextView, mdcContextKey)", "io.github.numichi.reactive.logger.coroutine")
)
fun readMDC(contextView: ContextView?, mdcContextKey: String): MDC {
    requireNotNull(contextView) { "contentView must not be null" }

    val mdc = MDC(mdcContextKey)

    try {
        val map: Map<String, String> = contextView.get(mdcContextKey)
        mdc.putAll(map)
    } catch (exception: Exception) {
        throw InvalidContextDataException(exception)
    }

    return mdc
}