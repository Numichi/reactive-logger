package io.github.numichi.reactive.logger.kotlin

import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.CoroutineScope

import reactor.util.context.Context

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith(
        "withMDCContext(mdc, block)",
        "io.github.numichi.reactive.logger.coroutine"
    )
)
suspend fun <T> withMDCContext(vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    return withContextBlock(reactorContextOrEmpty(), mdc.iterator(), block)
}

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith(
        "withMDCContext(context, mdc, block)",
        "io.github.numichi.reactive.logger.coroutine"
    )
)
suspend fun <T> withMDCContext(context: Context?, vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    requireNotNull(context) { "context must not be null" }
    return withContextBlock(context, mdc.iterator(), block)
}