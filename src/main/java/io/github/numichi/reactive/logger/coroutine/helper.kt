package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

internal typealias ContextResolver = suspend () -> ContextView?

internal suspend fun reactorContextOrEmpty(): Context {
    return rectorContext() ?: Context.empty()
}

internal suspend fun rectorContext(): Context? {
    return coroutineContext[ReactorContext]?.context
}

internal fun mdcCollectionIntoContext(context: Context, iterator: Iterator<MDC>): Context {
    var writableContext = context
    iterator.forEach {
        writableContext = writableContext.put(it.contextKey, it)
    }
    return writableContext
}

internal suspend fun <T> withContextBlock(context: Context, iterator: Iterator<MDC>, block: suspend CoroutineScope.() -> T): T {
    return kotlinx.coroutines.withContext(mdcCollectionIntoContext(context, iterator).asCoroutineContext()) {
        block()
    }
}