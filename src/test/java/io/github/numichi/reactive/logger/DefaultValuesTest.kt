package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.DefaultValues.Companion.configuration
import io.github.numichi.reactive.logger.DefaultValues.Companion.getInstance
import io.github.numichi.reactive.logger.exception.AlreadyConfigurationException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.scheduler.Schedulers

class DefaultValuesTest {
    companion object {
        private const val DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY"

        @AfterAll
        @JvmStatic
        fun tearDown() {
            DefaultValues.reset()
        }
    }

    @BeforeEach
    fun setUp() {
        DefaultValues.reset()
    }

    @Test
    fun configurationNotThrowExceptionIfUsedReset() {
        try {
            configuration()
            DefaultValues.reset()
            configuration()
        } catch (e: Exception) {
            fail<Any>()
        }
    }

    @Test
    fun configurationByDefault() {
        configuration()
        val instance = getInstance()
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.boundedElastic(), instance.defaultScheduler)
    }

    @Test
    fun configurationByCustomKey() {
        configuration("other-key")
        val instance = getInstance()
        assertEquals("other-key", instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.boundedElastic(), instance.defaultScheduler)
    }

    @Test
    fun configurationByCustomScheduler() {
        configuration(Schedulers.parallel())
        val instance = getInstance()
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.parallel(), instance.defaultScheduler)
    }

    @Test
    fun configurationByCustomSchedulerAndKey() {
        configuration("other-key", Schedulers.parallel())
        val instance = getInstance()

        assertEquals("other-key", instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.parallel(), instance.defaultScheduler)
    }

    @Test
    fun configurationErrorByDefault() {
        configuration()

        assertThrows<AlreadyConfigurationException> {
            configuration()
        }
    }

    @Test
    fun configurationErrorByCustomKey() {
        configuration("other-key")

        assertThrows<AlreadyConfigurationException> {
            configuration()
        }

        assertThrows<AlreadyConfigurationException> {
            configuration("other-key")
        }
    }

    @Test
    fun configurationErrorByCustomScheduler() {
        configuration(Schedulers.parallel())

        assertThrows<AlreadyConfigurationException> {
            configuration()
        }

        assertThrows<AlreadyConfigurationException> {
            configuration(Schedulers.parallel())
        }
    }

    @Test
    fun configurationErrorByCustomSchedulerAndKey() {
        configuration("other-key", Schedulers.parallel())

        assertThrows<AlreadyConfigurationException> {
            configuration()
        }

        assertThrows<AlreadyConfigurationException> {
            configuration("other-key", Schedulers.parallel())
        }
    }
}