package io.github.numichi.reactive.logger.kotlin

import io.github.numichi.reactive.logger.MDC
import reactor.util.context.Context

@Deprecated(
    "we are going to replace from coroutine package",
    ReplaceWith("putMdc(context, mdc)", "io.github.numichi.reactive.logger.coroutine")
)
fun putMdc(context: Context, vararg mdc: MDC): Context {
    return mdcCollectionIntoContext(context, mdc.iterator())
}