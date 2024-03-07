package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.internal.toSafeMdcMap
import reactor.util.context.ContextView

class MDCContextHook(val position: Position, private val hook: (contextView: ContextView, MDC) -> Map<String, String?>) {
    constructor(hook: (ContextView, MDC) -> Map<String, String?>) : this(Position.AFTER, hook)

    internal fun hookEvent(
        contextView: ContextView,
        mdc: MDC,
    ): Map<String, String?> {
        return try {
            this.hook(contextView, mdc).toSafeMdcMap()
        } catch (e: Throwable) {
            mapOf()
        }
    }
}
