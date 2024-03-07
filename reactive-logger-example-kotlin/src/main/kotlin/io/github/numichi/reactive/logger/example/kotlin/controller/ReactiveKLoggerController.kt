package io.github.numichi.reactive.logger.example.kotlin.controller

import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.reactor.MDCContext
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Similar with [io.github.numichi.reactive.logger.example.kotlin.controller.CoroutineLoggerController]
 *
 * - ReactiveLogger for Reactive with [org.slf4j.Logger]
 * - ReactiveKLogger for Reactive with [io.github.oshai.kotlinlogging.KLogger]
 * - CoroutineLogger for Kotlin Coroutine with [org.slf4j.Logger]
 * - CoroutineKLogger for Kotlin Coroutine with [io.github.oshai.kotlinlogging.KLogger]
 */
@RestController
@RequestMapping("reactive")
class ReactiveKLoggerController {

    private val logger = ReactiveKLogger.getLogger(ReactiveKLoggerController::class.java)
    private val logger1 = ReactiveKLogger.getLogger("logger-name-1", "context-key-1")
    private val logger2 = ReactiveKLogger.getLogger(ReactiveKLoggerController::class.java, "context-key-2")

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("snapshot")
    fun getSnapshot(): Mono<Map<String, String?>> {
        return MDCContext.snapshot().map { it.data }
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("read")
    fun getRead(): Mono<Map<String, String?>> {
        return MDCContext.read().map { it.data }
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log0")
    fun doInfo0(): Mono<Void> {
        return logger.info { "log0-information" }
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log1")
    fun doInfo1(): Mono<Void> {
        return logger1.info { "log1-information" }
            .contextWrite { it.modifyMdc(logger1.contextKey, "foo" to "bar") }
            .contextWrite { it.modifyMdc("will-not-appear" to "will-not-appear") }
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log2")
    fun doInfo2(): Mono<Void> {
        return logger2.info { "log2-information" }
    }
}