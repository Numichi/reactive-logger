package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger

interface LoggerRegistry {
    fun makeReactiveLogger(instance: String): ReactiveLogger
    fun makeReactiveKLogger(instance: String): ReactiveKLogger
    fun makeCoroutineLogger(instance: String): CoroutineLogger
    fun makeCoroutineKLogger(instance: String): CoroutineKLogger
}