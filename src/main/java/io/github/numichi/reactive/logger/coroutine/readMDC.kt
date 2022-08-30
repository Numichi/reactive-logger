package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.hook.mdcReferenceContentLoad
import reactor.util.context.ContextView

suspend fun readMdc(): MDC {
    return readMdc(Configuration.defaultReactorContextMdcKey)
}

suspend fun readMdc(mdcContextKey: String): MDC {
    return readMdc(rectorContext(), mdcContextKey)
}

fun readMdc(contextView: ContextView?): MDC {
    return readMdc(contextView, Configuration.defaultReactorContextMdcKey)
}

fun readMdc(contextView: ContextView?, mdcContextKey: String): MDC {
    requireNotNull(contextView) { "contentView must not be null" }
    val mdc = MDC(mdcContextKey)
    mdcReferenceContentLoad(contextView, mdcContextKey, mdc)
    return mdc
}