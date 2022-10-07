package io.github.numichi.reactive.logger.example.kotlin

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.coroutine.modifyMdc
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.snapshotMdc
import io.github.numichi.reactive.logger.coroutine.withMDCContext
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.github.numichi.reactive.logger.reactor.MDCContext
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Signal

class ExampleComponent(logger: Logger) {

    private val map = mapOf("foo" to "bar")
    private val coroutineLogger = CoroutineLogger.getLogger(logger)

    suspend fun example1(): Map<String, String?> {
        return withMDCContext(MDC(map)) {
            coroutineLogger.info("example1")
            snapshotMdc().data
        }
    }

    suspend fun example2() {
        withMDCContext(MDC(map)) {
            val mdc = readMdc()
            coroutineLogger.debug(mdc.data["foo"])
        }

        // ========= OR =========

        MDCContext.read() // Mono<MDC>
            .doOnEach {
                coroutineLogger.logOnSignal(it) { logger ->
                    val foo = getFooValue(it)

                    // Ofc, you can replace foo by be any message
                    if (it.isOnSubscribe) logger.trace(foo)
                    if (it.isOnNext) logger.debug(foo)
                    if (it.isOnComplete) logger.info(foo)
                    if (it.isOnError) logger.warn(foo)
                }
            }
            .contextWrite { it.modifyMdc(map) }
            .awaitSingleOrNull()
    }

    suspend fun example3() {
        withMDCContext(MDC(map)) {
            val mdc = readMdc()
            coroutineLogger.debug(mdc.data["foo"])
        }

        // ========= OR =========

        MDCContext.read() // Mono<MDC>
            .doOnEach(coroutineLogger.logOnEach { logger, signal ->
                val foo = getFooValue(signal)

                // Ofc, you can replace foo by be any message
                if (signal.isOnSubscribe) logger.trace(foo)
                if (signal.isOnNext) logger.debug(foo)
                if (signal.isOnComplete) logger.info(foo)
                if (signal.isOnError) logger.warn(foo)
            })
            .contextWrite { it.modifyMdc(map) }
            .awaitSingleOrNull()
    }

    suspend fun example4(): MDC {
        return try {
            readMdc()
        } catch (e: ReadException) {
            coroutineLogger.error(e.message)
            throw e
        }
    }

    private fun getFooValue(signal: Signal<MDC>): String? {
        val mdc = signal.get()
        var foo: String? = "null"
        if (mdc != null) {
            foo = mdc.data["foo"]
        }
        return foo
    }
}