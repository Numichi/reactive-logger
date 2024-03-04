package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.readMdc
import reactor.util.context.ContextView

internal fun mdcReferenceContentLoad(contextView: ContextView, mdc: MDC): MDC {
    var mdcInstance = mdc

    MDCContextHookCache.getHooks()[Position.BEFORE]?.hookEvent(contextView, mdcInstance)?.run {
        mdcInstance = mdcInstance.plus(this)
    }

    runCatching { mdcInstance.plus(readMdc(contextView, mdc.contextKey).data) }.getOrNull()?.also {
        mdcInstance = it
    }

    MDCContextHookCache.getHooks()[Position.AFTER]?.hookEvent(contextView, mdcInstance)?.run {
        mdcInstance = mdcInstance.plus(this)
    }

    return mdcInstance
}