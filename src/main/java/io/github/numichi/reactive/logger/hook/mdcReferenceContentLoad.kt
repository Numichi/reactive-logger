package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.MDCHook
import reactor.util.context.ContextView

internal fun mdcReferenceContentLoad(contextView: ContextView, mdcContextKey: String, mdc: MDC) {
    MDCHookCache.initCache(Configuration.customHook)

    MDCHookCache.listBefore.forEach { hookEvent: MDCHook<*> ->
        mdc.putAll(hookEvent.hookEvent(contextView))
    }

    val mapFromContextKey = runCatching {
        contextView.getOrDefault<MutableMap<String, String>>(mdcContextKey, null) ?: mutableMapOf()
    }.getOrNull() ?: mutableMapOf()

    mdc.putAll(mapFromContextKey)

    MDCHookCache.listAfter.forEach { hookEvent: MDCHook<*> ->
        mdc.putAll(hookEvent.hookEvent(contextView))
    }
}