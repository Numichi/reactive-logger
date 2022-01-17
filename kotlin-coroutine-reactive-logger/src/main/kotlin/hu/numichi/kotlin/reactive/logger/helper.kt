package hu.numichi.kotlin.reactive.logger

import kotlinx.coroutines.reactor.ReactorContext
import reactor.util.context.Context
import reactor.util.context.ContextView
import java.util.stream.Collectors
import kotlin.coroutines.coroutineContext

internal suspend fun reactorContextView(): ContextView {
    return rectorContext()?.readOnly() ?: Context.empty().readOnly()
}

internal suspend fun rectorContext(): Context? {
    return coroutineContext[ReactorContext]?.context
}