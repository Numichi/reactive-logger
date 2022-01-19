package hu.numichi.reactive.logger.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.asCoroutineContext
import hu.numichi.reactive.logger.MDC
import reactor.util.context.Context

suspend fun <T> withReactorContext(mdc: MDC, block: suspend CoroutineScope.() -> T): T {
    return withContext(listOf(mdc), rectorContext(), block)
}

suspend fun <T> withContext(mdc: MDC, context: Context, block: suspend CoroutineScope.() -> T): T {
    return withContext(listOf(mdc), context, block)
}

suspend fun <T> withReactorContext(mdc0: MDC, mdc1: MDC, block: suspend CoroutineScope.() -> T): T {
    return withContext(listOf(mdc0, mdc1), rectorContext(), block)
}

suspend fun <T> withContext(mdc0: MDC, mdc1: MDC, context: Context, block: suspend CoroutineScope.() -> T): T {
    return withContext(listOf(mdc0, mdc1), context, block)
}

suspend fun <T> withContext(mdcCollection: Collection<MDC>, context: Context?, block: suspend CoroutineScope.() -> T): T {
    var newContext = context ?: Context.empty()

    mdcCollection.forEach {
        newContext = newContext.delete(it.contextKey)
        newContext = newContext.put(it.contextKey, it.map)
    }

    return kotlinx.coroutines.withContext(newContext.asCoroutineContext()) {
        block()
    }
}