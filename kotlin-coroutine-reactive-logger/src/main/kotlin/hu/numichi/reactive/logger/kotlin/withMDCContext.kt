package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.MDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context

suspend fun <T> withMDCContext(vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    var readableContext = rectorContext() ?: Context.empty()

    mdc.forEach {
        readableContext = readableContext.put(it.contextKey, it.map)
    }

    return kotlinx.coroutines.withContext(readableContext.asCoroutineContext()) {
        block()
    }
}

suspend fun <T> withMDCContext(context: Context?, vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    requireNotNull(context) { "context must not be null"}

    var readableContext: Context = context

    mdc.forEach {
        readableContext = readableContext.put(it.contextKey, it.map)
    }

    return kotlinx.coroutines.withContext(readableContext.asCoroutineContext()) {
        block()
    }
}