package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.hook.mdcReferenceContentLoad
import reactor.util.context.Context
import reactor.util.context.ContextView

suspend fun readMdc(): MDC {
    return readMdc(Configuration.defaultReactorContextMdcKey)
}

suspend fun readMdc(mdcContextKey: String): MDC {
    return readMdc(reactorContextOrEmpty(), mdcContextKey)
}

// Not throw any exception
fun readMdc(contextView: ContextView? = Context.empty()): MDC {
    requireNotNull(contextView)
    return readMdc(contextView, Configuration.defaultReactorContextMdcKey)
}

fun readMdc(contextView: ContextView? = Context.empty(), mdcContextKey: String): MDC {
    requireNotNull(contextView)
    val mdc = MDC(mdcContextKey)
    mdcReferenceContentLoad(contextView, mdcContextKey, mdc)
    return mdc
}