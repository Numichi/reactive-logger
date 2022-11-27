package io.github.numichi.reactive.logger.example.kotlin.controller

import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.reactor.MDCContext
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Similar with [io.github.numichi.reactive.logger.example.kotlin.controller.CoroutineLoggerController]
 *
 * - ReactiveLogger for Reactive with [org.slf4j.Logger]
 * - ReactiveKLogger for Reactive with [mu.KLogger]
 * - CoroutineLogger for Kotlin Coroutine with [org.slf4j.Logger]
 * - CoroutineKLogger for Kotlin Coroutine with [mu.KLogger]
 */
@RestController
@RequestMapping("reactive")
class ReactiveKLoggerController(loggerRegistry: LoggerRegistry) {

    /**
     * ### Manual like
     * ```kotlin
     * ReactiveKLogger.getLogger("io.github.numichi.reactive.logger.example.controllers.ReactiveLoggerController", "context-key-from-yml")
     * ```
     *
     * "example-instance" from application.yml
     */
    private val logger1 = loggerRegistry.getReactiveKLogger("example-instance")

    /**
     * ### Manual like
     * ```kotlin
     * ReactiveKLogger.getLogger(ReactiveLoggerController::class.java, "another-context-key-from-yml", Schedulers.parallel())
     * ```
     *
     * "another-example-instance" from application.yml
     */
    private val logger2 = loggerRegistry.getReactiveKLogger("another-example-instance", ReactiveKLoggerController::class.java)

    /**
     * Manual logger creation
     */
    private val logger = ReactiveKLogger.getLogger(ReactiveKLoggerController::class.java)

    /**
     * ### Response example
     * ```
     * HTTP/1.1 200 OK
     * content-Type: application/json
     * Content-Length: 106
     *
     * {
     *     "userId": "d50e311a-739b-4521-8143-033ad33af323",
     *     "example": "example"
     * }
     * ```
     *
     * ### Description
     *
     * The snapshot give MDC information and will be triggered MDC hooks.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.filter.UserFilter.filter]
     * @see [io.github.numichi.reactive.logger.example.kotlin.configuration.LoggerHookConfiguration.traceContextHook]
     */
    @GetMapping("snapshot")
    fun getSnapshot(): Mono<Map<String, String?>> {
        return MDCContext.snapshot().map { it.data }
    }

    /**
     * ### Response example
     * ```
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     * Content-Length: 49
     *
     * {
     *     "userId": "4cab2469-fbcc-40dc-91a8-88f4d553f1eb"
     * }
     * ```
     *
     * ### Description
     *
     * It's like a snapshot, except for MDC Hook information. The filter component added "userId", so there is one element of data.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.filter.UserFilter.filter]
     */
    @GetMapping("read")
    fun getRead(): Mono<Map<String, String?>> {
        return MDCContext.read().map { it.data }
    }

    /**
     * ### JSON Log format
     * ```json
     * {"message":"log0-information","context":{"userId":"4ec3f71c-631c-470a-840f-c3cd697986eb","example":"example"}}
     * ```
     */
    @GetMapping("log0")
    fun doInfo0(): Mono<Void> {
        return logger.info { "log0-information" }
    }

    /**
     * ### JSON Log format
     * ```json
     * {"message":"log1-information","context":{"example":"example","foo":"bar"}}
     * ```
     *
     * ### Description
     *
     * Logging by the normal way with MDC information appended from reactor context.
     * You can see, "userId" isn't shown here. It attached to default context key (DEFAULT_REACTOR_CONTEXT_MDC_KEY) in filter,
     * not the current context key (context-key-from-yml).
     * The hook will activate for all context keys, so there is "spanId" and "traceId".
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.configuration.LoggerHookConfiguration.traceContextHook]
     * @see [io.github.numichi.reactive.logger.example.kotlin.ExamplePlugin]
     */
    @GetMapping("log1")
    fun doInfo1(): Mono<Void> {
        return logger1.info { "log1-information" }
            .contextWrite { it.modifyMdc(logger1.contextKey, "foo" to "bar") }
            .contextWrite { it.modifyMdc("will-not-appear" to "will-not-appear") }
    }

    /**
     * ### JSON Log format
     *
     * ```json
     * {"message":"log2-information","context":{"example":"n/a"}}
     * ```
     *
     * ### Description
     *
     * Like previous method [doInfo1], just it used "another-context-key-from-yml" context key.
     * With this key, it will enable the other hook, which throws an exception for all other keys.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.configuration.LoggerHookConfiguration.traceContextHook]
     * @see [io.github.numichi.reactive.logger.example.kotlin.configuration.LoggerHookConfiguration.anotherTraceContextHook]
     * @see [io.github.numichi.reactive.logger.example.kotlin.ExamplePlugin]
     */
    @GetMapping("log2")
    fun doInfo2(): Mono<Void> {
        return logger2.info { "log2-information" }
    }
}