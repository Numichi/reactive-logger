package io.github.numichi.reactive.logger.coroutine.internal

import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.withContext

internal suspend fun reactorContextOrEmpty(): Context {
    return rectorContext() ?: Context.empty()
}

internal suspend fun rectorContext(): Context? {
    return coroutineContext[ReactorContext]?.context
}

internal fun mdcCollectionIntoContext(context: Context, iterator: Iterator<MDC>): Context {
    val addedKeys = mutableSetOf<String>()
    var writableContext = context

    for (mdc in iterator) {
        check(mdc.contextKey !in addedKeys) { "Duplicate context key writing." }
        addedKeys += mdc.contextKey.toString()
        writableContext = writableContext.put(mdc.contextKey, mdc.data)
    }

    return writableContext
}

internal suspend fun <T> withContextBlock(context: Context, iterator: Iterator<MDC>, block: suspend CoroutineScope.() -> T): T {
    return withContext(mdcCollectionIntoContext(context, iterator).asCoroutineContext()) {
        block()
    }
}