package io.github.numichi.reactive.logger

import reactor.util.context.ContextView

class MDCHook<T>(
    val contextKey: Any,
    val order: Int = 0,
    val hook: (T?) -> Map<String, String>
) {
    internal fun hookEvent(contextView: ContextView): Map<String, String> {
        val contextValue = runCatching { contextView.get<T>(contextKey) }.getOrNull()
        return runCatching { this.hook(contextValue) }.getOrNull() ?: mapOf()
    }
}
