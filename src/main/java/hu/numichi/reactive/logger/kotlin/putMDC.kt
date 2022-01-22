package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.MDC
import reactor.util.context.Context

fun putMdc(context: Context, vararg mdc: MDC): Context {
    return mdcCollectionIntoContext(context, mdc.iterator())
}