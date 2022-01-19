package hu.numichi.reactive.logger.kotlin

import kotlinx.coroutines.reactor.ReactorContext
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext

internal suspend fun reactorContextView(): ContextView {
    return rectorContext()?.readOnly() ?: Context.empty().readOnly()
}

internal suspend fun rectorContext(): Context? {
    return coroutineContext[ReactorContext]?.context
}