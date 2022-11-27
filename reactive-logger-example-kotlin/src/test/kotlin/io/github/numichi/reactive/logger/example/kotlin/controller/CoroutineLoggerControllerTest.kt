package io.github.numichi.reactive.logger.example.kotlin.controller

import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class CoroutineLoggerControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun getSnapshotTest() {
        webTestClient.get()
            .uri("/coroutine/snapshot")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
            .jsonPath("$.example").isEqualTo("example")
    }

    @Test
    fun getReadTest() {
        webTestClient.get()
            .uri("/coroutine/read")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
    }

    /**
     * Console example:
     * ```
     * {"message":"log0-information","context":{"userId":"5c827c39-9f5b-478f-b17e-5b8d493f57a7","example":"example"}}
     * ```
     */
    @Test
    fun doInfo0Test() {
        webTestClient.get()
            .uri("/coroutine/log0")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * Console example (used logger 2x, because called twice in controller):
     * ```
     * {"message":"log1-information","context":{"example":"example","foo":"bar"}}
     * {"message":"log1-information","context":{"example":"example","foo":"bar"}}
     * ```
     */
    @Test
    fun doInfo1Test() {
        webTestClient.get()
            .uri("/coroutine/log1")
            .exchange()
            .expectStatus().isOk
    }


    /**
     * Console example:
     * ```
     * {"message":"log2-information","context":{"example":"n/a"}}
     * ```
     */
    @Test
    fun doInfo2Test() {
        webTestClient.get()
            .uri("/coroutine/log2")
            .exchange()
            .expectStatus().isOk
    }
}