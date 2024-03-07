package io.github.numichi.reactive.logger.complex

import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.withMDCContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RunTest {
    @Test
    fun exampleRunTest() =
        runTest {
            withMDCContext(MDC(A::class.java)) {
                val mdc = readMdc(A::class.java)
                assertEquals(A::class.java, mdc.contextKey)

                val logger = CoroutineLogger.getLogger("name", A::class.java)
                assertEquals(A::class.java, logger.contextKey)

                withMDCContext(readMdc(A::class.java) + ("A" to "B")) {
                    val mdc2 = readMdc(A::class.java)
                    assertEquals(A::class.java, mdc2.contextKey)
                    assertEquals("B", mdc2["A"])
                }
            }
        }
}

interface A
