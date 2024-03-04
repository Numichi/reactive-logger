package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.MDC
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

interface RSnapshot {
    fun snapshot(): Mono<MDC>

    fun snapshot(contextView: ContextView): Mono<MDC>
}