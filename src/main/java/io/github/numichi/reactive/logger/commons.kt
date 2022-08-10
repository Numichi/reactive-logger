package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.models.MDC
import io.github.numichi.reactive.logger.models.MDCHook
import reactor.util.context.ContextView

const val DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY"

internal fun mdcReferenceContentLoad(contextView: ContextView, mdcContextKey: String, mdc: MDC) {
    DefaultValues.getInstance().customHook
        .filter { it.order < 0 }
        .sortedBy { it.order }
        .forEach { hookEvent: MDCHook<*> ->
            mdc.putAll(hookEvent.hookEvent(contextView))
        }

    val mapFromContextKey = runCatching {
        contextView.getOrDefault<MutableMap<String, String>>(mdcContextKey, null) ?: mutableMapOf()
    }.getOrNull() ?: mutableMapOf()
    mdc.putAll(mapFromContextKey)

    DefaultValues.getInstance().customHook
        .filter { 0 <= it.order }
        .sortedBy { it.order }
        .forEach { hookEvent: MDCHook<*> ->
            mdc.putAll(hookEvent.hookEvent(contextView))
        }
}