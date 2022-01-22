package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.MDC
import reactor.util.context.Context

fun putMdc(context: Context, vararg mdc: MDC): Context {
    var writableContext: Context = context

    mdc.forEach {
        writableContext = writableContext.put(it.contextKey, it.asMap())
    }

    return writableContext
}