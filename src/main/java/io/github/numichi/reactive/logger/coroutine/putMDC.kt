package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import reactor.util.context.Context

fun putMdc(context: Context, vararg mdc: MDC): Context {
    return mdcCollectionIntoContext(context, mdc.iterator())
}