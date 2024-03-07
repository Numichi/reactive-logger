package io.github.numichi.reactive.logger.example.kotlin.controller

import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.readOrDefaultMdc
import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import io.github.numichi.reactive.logger.coroutine.withMDCContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("coroutine")
class CoroutineLoggerController {
    companion object {
        const val WILL_NOT_APPEAR = "will-not-appear"
    }

    private val logger = CoroutineLogger.getLogger(CoroutineLoggerController::class.java)
    private val logger1 = CoroutineLogger.getLogger("log-name-1", "context-key-1")
    private val logger2 = CoroutineLogger.getLogger("log-name-2", CoroutineLoggerController::class.java)

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("snapshot")
    suspend fun getSnapshot(): Map<String, String?> {
        return snapshotMdc().data
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("read")
    suspend fun getRead(): Map<String, String?> {
        return readMdc().data
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log0")
    suspend fun doInfo0() {
        logger.info("log0-information")
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log1")
    suspend fun doInfo1() {
        val mdc1 = readOrDefaultMdc(logger1.contextKey) + mapOf("foo" to "bar")
        val mdc2 = readMdc() + mapOf(WILL_NOT_APPEAR to WILL_NOT_APPEAR)

        withMDCContext(mdc1, mdc2) {
            logger1.info("log1-information")
        }
    }

    /**
     * Alternative to [doInfo1]
     */
    @GetMapping("log1-alternative")
    suspend fun doInfo1Alternative() {
        mono { logger1.info("log1-information") }
            .contextWrite { it.modifyMdc(logger1.contextKey, mapOf("foo" to "bar")) }
            .contextWrite { it.modifyMdc(mapOf(WILL_NOT_APPEAR to WILL_NOT_APPEAR)) }
            .awaitSingleOrNull()
    }

    /**
     * It is similar to the one inside the **reactive-logger-example-java** directory.
     */
    @GetMapping("log2")
    suspend fun doInfo2() {
        logger2.info("log2-information")
    }
}