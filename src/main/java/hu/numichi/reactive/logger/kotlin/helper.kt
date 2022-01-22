package hu.numichi.reactive.logger.kotlin

import kotlinx.coroutines.reactor.ReactorContext
import reactor.util.context.Context
import kotlin.coroutines.coroutineContext

internal suspend fun rectorContext(): Context? {
    return coroutineContext[ReactorContext]?.context
}