package io.github.numichi.reactive.logger.reactor.kotlin

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.stepVerifier
import io.github.numichi.reactive.logger.stepVerifierError
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class KotlinExtensionsTest {

    private val logger = mockk<Logger>(relaxed = true)
    private val reactiveLogger = ReactiveLogger.getLogger(logger)
    private val coroutineLogger = CoroutineLogger.getLogger(logger)

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun logOnNextMonoTest() {
        val mono = Mono.just("test")
            .logOnNext { reactiveLogger.info(it) }

        stepVerifier("test") { mono }
        verify(exactly = 1) { logger.info("test") }
    }

    @Test
    fun logOnNextFluxTest() {
        val flux = Flux.just("test")
            .logOnNext { reactiveLogger.info(it) }

        StepVerifier.create(flux).expectNext("test").verifyComplete()
        verify(exactly = 1) { logger.info("test") }
    }

    @Test
    fun logOnErrorMonoTest() {
        val mono = Mono.error<RuntimeException>(RuntimeException("error"))
            .logOnError { reactiveLogger.error(it.message) }

        stepVerifierError(RuntimeException::class.java) { mono }
        verify(exactly = 1) { logger.error("error") }
    }

    @Test
    fun logOnErrorFluxTest() {
        val flux = Flux.error<RuntimeException>(RuntimeException("error"))
            .logOnError { reactiveLogger.error(it.message) }

        StepVerifier.create(flux).verifyError(RuntimeException::class.java)
        verify(exactly = 1) { logger.error("error") }
    }

    @Test
    fun logOnNextMonoCoroutineTest() {
        val mono = Mono.just("test")
            .logOnNextCoroutine { coroutineLogger.info(it) }

        stepVerifier("test") { mono }
        verify(exactly = 1) { logger.info("test") }
    }

    @Test
    fun logOnNextFluxCoroutineTest() {
        val flux = Flux.just("test")
            .logOnNextCoroutine { coroutineLogger.info(it) }

        StepVerifier.create(flux).expectNext("test").verifyComplete()
        verify(exactly = 1) { logger.info("test") }
    }

    @Test
    fun logOnErrorMonoCoroutineTest() {
        val mono = Mono.error<RuntimeException>(RuntimeException("error"))
            .logOnErrorCoroutine { coroutineLogger.error(it.message) }

        stepVerifierError(RuntimeException::class.java) { mono }
        verify(exactly = 1) { logger.error("error") }
    }

    @Test
    fun logOnErrorFluxCoroutineTest() {
        val flux = Flux.error<RuntimeException>(RuntimeException("error"))
            .logOnErrorCoroutine { coroutineLogger.error(it.message) }

        StepVerifier.create(flux).verifyError(RuntimeException::class.java)
        verify(exactly = 1) { logger.error("error") }
    }
}