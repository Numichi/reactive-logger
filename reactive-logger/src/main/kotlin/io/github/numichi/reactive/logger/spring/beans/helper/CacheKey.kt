package io.github.numichi.reactive.logger.spring.beans.helper

import reactor.core.scheduler.Scheduler

internal data class CacheKey(
    val instance: String,
    val loggerName: String?,
    val contextKey: Any?,
    val scheduler: Scheduler?
)

internal data class CacheKKey(
    val instance: String,
    val loggerName: String?,
    val contextKey: Any?,
    val scheduler: Scheduler?
)