package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import reactor.util.context.ContextView

suspend fun readMDC(): MDC {
    return readMDC(DefaultValues.getInstance().defaultReactorContextMdcKey)
}

suspend fun readMDC(mdcContextKey: String): MDC {
    return readMDC(rectorContext(), mdcContextKey)
}

fun readMDC(contextView: ContextView?): MDC {
    return readMDC(contextView, DefaultValues.getInstance().defaultReactorContextMdcKey)
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