package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import reactor.util.context.ContextView

internal fun mdcReferenceContentLoad(contextView: ContextView, mdcContextKey: String, mdc: MDC) {
    MDCHookCache.initCache()

    MDCHookCache.listBefore.forEach { hookEvent: MDCHook<*> ->
        mdc.putAll(hookEvent.hookEvent(contextView, MDC(mdc)))
    }

    contextView.getOrDefault<Map<String, String>>(mdcContextKey, null)?.also {
        val map = it.mapValues { v -> (v.value as Any).toString() }
        mdc.putAll(map)
    }

    MDCHookCache.listAfter.forEach { hookEvent: MDCHook<*> ->
        mdc.putAll(hookEvent.hookEvent(contextView, MDC(mdc)))
    }
}