package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import mu.KLogger
import org.slf4j.Logger

interface LoggerRegistry {
    fun makeReactiveLogger(instance: String): ReactiveLogger
    fun makeReactiveLogger(instance: String, logger: String): ReactiveLogger
    fun makeReactiveLogger(instance: String, logger: Class<*>): ReactiveLogger
    fun makeReactiveLogger(instance: String, logger: Logger): ReactiveLogger

    fun makeReactiveKLogger(instance: String): ReactiveKLogger
    fun makeReactiveKLogger(instance: String, logger: String): ReactiveKLogger
    fun makeReactiveKLogger(instance: String, logger: Class<*>): ReactiveKLogger
    fun makeReactiveKLogger(instance: String, logger: KLogger): ReactiveKLogger

    fun makeCoroutineLogger(instance: String): CoroutineLogger
    fun makeCoroutineLogger(instance: String, logger: String): CoroutineLogger
    fun makeCoroutineLogger(instance: String, logger: Class<*>): CoroutineLogger
    fun makeCoroutineLogger(instance: String, logger: Logger): CoroutineLogger

    fun makeCoroutineKLogger(instance: String): CoroutineKLogger
    fun makeCoroutineKLogger(instance: String, logger: String): CoroutineKLogger
    fun makeCoroutineKLogger(instance: String, logger: Class<*>): CoroutineKLogger
    fun makeCoroutineKLogger(instance: String, logger: KLogger): CoroutineKLogger
}