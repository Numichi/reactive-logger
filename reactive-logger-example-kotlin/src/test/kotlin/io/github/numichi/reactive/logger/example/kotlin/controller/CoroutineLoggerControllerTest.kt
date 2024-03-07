package io.github.numichi.reactive.logger.example.kotlin.controller

import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import java.util.function.Consumer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class CoroutineLoggerControllerTest {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CoroutineLoggerControllerTest::class.java)

        fun consumer(): Consumer<EntityExchangeResult<ByteArray?>> {
            return Consumer { entityExchangeResult: EntityExchangeResult<ByteArray?> ->
                val responseBody = entityExchangeResult.responseBody!!
                val responseBodyString = String(responseBody)
                log.info(responseBodyString)
            }
        }
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    /**
     * @see CoroutineLoggerController.getSnapshot
     */
    @Test
    fun getSnapshotTest() {
        webTestClient.get()
            .uri("/coroutine/snapshot")
            .exchange()
            .expectBody()
            .consumeWith(consumer())
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
            .jsonPath("$.example").isEqualTo("example")
    }

    /**
     * @see CoroutineLoggerController.getRead
     */
    @Test
    fun getReadTest() {
        webTestClient.get()
            .uri("/coroutine/read")
            .exchange()
            .expectBody()
            .consumeWith(consumer())
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
    }

    /**
     * @see CoroutineLoggerController.doInfo0
     */
    @Test
    fun doInfo0Test() {
        webTestClient.get()
            .uri("/coroutine/log0")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * @see CoroutineLoggerController.doInfo1
     */
    @Test
    fun doInfo1Test() {
        webTestClient.get()
            .uri("/coroutine/log1")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * @see CoroutineLoggerController.doInfo1Alternative
     */
    @Test
    fun doInfo1AlternativeTest() {
        webTestClient.get()
            .uri("/coroutine/log1-alternative")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * @see CoroutineLoggerController.doInfo2
     */
    @Test
    fun doInfo2Test() {
        webTestClient.get()
            .uri("/coroutine/log2")
            .exchange()
            .expectStatus().isOk
    }
}