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
internal class ReactiveKLoggerControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    /**
     * @see ReactiveKLoggerController.getSnapshot
     */
    @Test
    fun getSnapshotTest() {
        webTestClient.get()
            .uri("/reactive/snapshot")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
            .jsonPath("$.example").isEqualTo("example")
    }

    /**
     * @see ReactiveKLoggerController.getRead
     */
    @Test
    fun getReadTest() {
        webTestClient.get()
            .uri("/reactive/read")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value<Any> { isA<Any>(UUID::class.java) }
    }

    /**
     * @see ReactiveKLoggerController.doInfo0
     */
    @Test
    fun doInfo0Test() {
        webTestClient.get()
            .uri("/reactive/log0")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * @see ReactiveKLoggerController.doInfo1
     */
    @Test
    fun doInfo1Test() {
        webTestClient.get()
            .uri("/reactive/log1")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * @see ReactiveKLoggerController.doInfo2
     */
    @Test
    fun doInfo2Test() {
        webTestClient.get()
            .uri("/reactive/log2")
            .exchange()
            .expectStatus().isOk
    }
}