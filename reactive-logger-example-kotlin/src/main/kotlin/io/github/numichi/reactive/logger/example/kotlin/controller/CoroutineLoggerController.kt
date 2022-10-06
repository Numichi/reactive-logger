package io.github.numichi.reactive.logger.example.kotlin.controller

import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.readOrDefaultMdc
import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import io.github.numichi.reactive.logger.coroutine.withMDCContext
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Similar with [io.github.numichi.reactive.logger.example.controllers.ReactiveLoggerController]
 */
@RestController
@RequestMapping("coroutine")
class CoroutineLoggerController(loggerRegistry: LoggerRegistry) {

    /**
     * ### Manual like
     * ```kotlin
     * CoroutineLogger.getLogger("io.github.numichi.reactive.logger.example.controllers.ReactiveLoggerController", "context-key-from-yml")
     * ```
     *
     * "example-instance" from application.yml
     */
    val logger1 = loggerRegistry.makeCoroutineLogger("example-instance")

    /**
     * ### Manual like
     * ```kotlin
     * CoroutineLogger.getLogger(CoroutineLoggerController::class.java, "another-context-key-from-yml", Schedulers.parallel())
     * ```
     *
     * "another-example-instance" from application.yml
     */
    val logger2 = loggerRegistry.makeCoroutineLogger("another-example-instance", CoroutineLoggerController::class.java)

    /**
     * Manual logger creation
     */
    val logger = CoroutineLogger.getLogger(CoroutineLoggerController::class.java)

    /**
     * See the equivalent of reactive.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.controller.ReactiveKLoggerController.getSnapshot]
     */
    @GetMapping("snapshot")
    suspend fun getSnapshot(): Map<String, String?> {
        return snapshotMdc().data
    }

    /**
     * See the equivalent of reactive.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.controller.ReactiveKLoggerController.getRead]
     */
    @GetMapping("read")
    suspend fun getRead(): Map<String, String?> {
        return readMdc().data
    }

    /**
     * See the equivalent of reactive.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.controller.ReactiveKLoggerController.doInfo0]
     */
    @GetMapping("log0")
    suspend fun doInfo0() {
        logger.info("log0-information")
    }

    /**
     * <h2>Description</h2>
     *
     * See the equivalent of reactive.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.controller.ReactiveKLoggerController.doInfo1]
     */
    @GetMapping("log1")
    suspend fun doInfo1() {
        // readMdc(logger1.contextKey) will throw Exception because this context key already not initialized here.
        val mdc1 = readOrDefaultMdc(logger1.contextKey) + mapOf("foo" to "bar")
        val mdc2 = readMdc() + mapOf("will-not-appear" to "will-not-appear")

        withMDCContext(mdc1, mdc2) {
            logger1.info("log1-information")
        }

        // ========= OR =========

        mono { logger1.info("log1-information") }
            .contextWrite { it.modifyMdc(logger1.contextKey, mapOf("foo" to "bar")) }
            .contextWrite { it.modifyMdc(mapOf("will-not-appear" to "will-not-appear")) }
            .awaitSingleOrNull()
    }

    /**
     * See the equivalent of reactive.
     *
     * @see [io.github.numichi.reactive.logger.example.kotlin.controller.ReactiveKLoggerController.doInfo2]
     */
    @GetMapping("log2")
    suspend fun doInfo2() {
        logger1.info("log2-information")
    }
}