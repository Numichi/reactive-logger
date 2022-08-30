package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.CoroutineScope

import reactor.util.context.Context

suspend fun <T> withMDCContext(vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    return withContextBlock(reactorContextOrEmpty(), mdc.iterator(), block)
}

suspend fun <T> withMDCContext(context: Context?, vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    requireNotNull(context) { "context must not be null" }
    return withContextBlock(context, mdc.iterator(), block)
}