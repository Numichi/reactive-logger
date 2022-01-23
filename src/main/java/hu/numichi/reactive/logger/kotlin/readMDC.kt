package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.Values
import hu.numichi.reactive.logger.exception.InvalidContextDataException
import hu.numichi.reactive.logger.MDC
import reactor.util.context.ContextView

suspend fun readMDC(): MDC {
    return readMDC(Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY)
}

suspend fun readMDC(mdcContextKey: String): MDC {
    return readMDC(rectorContext(), mdcContextKey)
}

fun readMDC(contextView: ContextView?): MDC {
    return readMDC(contextView, Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY)
}

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