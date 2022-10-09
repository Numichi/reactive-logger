package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.readMdc
import reactor.util.context.ContextView

internal fun mdcReferenceContentLoad(contextView: ContextView, mdc: MDC): MDC {
    var mdcInstance = mdc

    MDCHookCache.listBefore.forEach { hookEvent: MDCHook<*> ->
        mdcInstance = mdcInstance.plus(hookEvent.hookEvent(contextView, mdcInstance))
    }

    runCatching { mdcInstance.plus(readMdc(contextView, mdc.contextKey).data) }.getOrNull()?.also {
        mdcInstance = it
    }

    MDCHookCache.listAfter.forEach { hookEvent: MDCHook<*> ->
        mdcInstance = mdcInstance.plus(hookEvent.hookEvent(contextView, mdcInstance))
    }

    return mdcInstance
}