package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.models.MDC
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import io.github.numichi.reactive.logger.mdcReferenceContentLoad
import reactor.util.context.ContextView

suspend fun readMdc(): MDC {
    return readMdc(DefaultValues.getInstance().defaultReactorContextMdcKey)
}

suspend fun readMdc(mdcContextKey: String): MDC {
    return readMdc(rectorContext(), mdcContextKey)
}

fun readMdc(contextView: ContextView?): MDC {
    return readMdc(contextView, DefaultValues.getInstance().defaultReactorContextMdcKey)
}

fun readMdc(contextView: ContextView?, mdcContextKey: String): MDC {
    requireNotNull(contextView) { "contentView must not be null" }
    val mdc = MDC(mdcContextKey)
    mdcReferenceContentLoad(contextView, mdcContextKey, mdc)
    return mdc
}