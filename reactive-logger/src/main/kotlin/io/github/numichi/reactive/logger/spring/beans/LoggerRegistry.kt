package io.github.numichi.reactive.logger.spring.beans

import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.handler.ContentHandlerCoroutine
import io.github.numichi.reactive.logger.spring.handler.ContentHandlerReactive
import mu.KLogger
import org.slf4j.Logger

interface LoggerRegistry {
    fun reset()

    fun getReactiveLogger(instance: String): ReactiveLogger
    fun getReactiveLogger(instance: String, logger: String): ReactiveLogger
    fun getReactiveLogger(instance: String, logger: Class<*>): ReactiveLogger
    fun getReactiveLogger(instance: String, logger: Logger): ReactiveLogger

    fun getReactiveKLogger(instance: String): ReactiveKLogger
    fun getReactiveKLogger(instance: String, logger: String): ReactiveKLogger
    fun getReactiveKLogger(instance: String, logger: Class<*>): ReactiveKLogger
    fun getReactiveKLogger(instance: String, logger: KLogger): ReactiveKLogger

    fun getCoroutineLogger(instance: String): CoroutineLogger
    fun getCoroutineLogger(instance: String, logger: String): CoroutineLogger
    fun getCoroutineLogger(instance: String, logger: Class<*>): CoroutineLogger
    fun getCoroutineLogger(instance: String, logger: Logger): CoroutineLogger

    fun getCoroutineKLogger(instance: String): CoroutineKLogger
    fun getCoroutineKLogger(instance: String, logger: String): CoroutineKLogger
    fun getCoroutineKLogger(instance: String, logger: Class<*>): CoroutineKLogger
    fun getCoroutineKLogger(instance: String, logger: KLogger): CoroutineKLogger

    fun getContentHandlerReactive(instance: String): ContentHandlerReactive
    fun getContentHandlerCoroutine(instance: String): ContentHandlerCoroutine
}