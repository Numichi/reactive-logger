package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import reactor.util.context.ContextView
import java.util.NoSuchElementException

class MDCHook<T>(
    val contextKey: Any,
    val order: Int,
    val hook: (T?, MDC) -> Map<String, String>
) {
    internal fun hookEvent(contextView: ContextView, mdc: MDC): Map<String, String> {
        val contextValue = try {
            contextView.get<T>(contextKey)
        } catch (e: NoSuchElementException) {
            null
        }

        return try {
            try {
                this.hook(contextValue, mdc)
            } catch (e: ClassCastException) {
                this.hook(null, mdc)
            }
        } catch (e: Throwable) {
            mapOf()
        }
    }
}