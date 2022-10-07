package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.toSafeMdcMap
import reactor.util.context.ContextView

class MDCHook<T>(
    val name: String,
    val contextKey: Any,
    val order: Int = 0,
    val hook: (T?, MDC) -> Map<String, String?>
) {
    constructor(name: String, contextKey: Any, hook: (T?, MDC) -> Map<String, String?>) : this(name, contextKey, 0, hook)

    internal fun hookEvent(contextView: ContextView, mdc: MDC): Map<String, String?> {
        val contextValue = try {
            contextView.get<T>(contextKey)
        } catch (e: Exception) {
            null
        }

        return try {
            try {
                this.hook(contextValue, mdc).toSafeMdcMap()
            } catch (e: ClassCastException) {
                this.hook(null, mdc).toSafeMdcMap()
            }
        } catch (e: Throwable) {
            mapOf()
        }
    }
}