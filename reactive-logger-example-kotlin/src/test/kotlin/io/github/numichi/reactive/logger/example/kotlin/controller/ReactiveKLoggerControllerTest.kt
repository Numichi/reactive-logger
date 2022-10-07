package io.github.numichi.reactive.logger.example.kotlin.controller

import org.hamcrest.Matchers.isA
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class ReactiveKLoggerControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun getSnapshotTest() {
        webTestClient.get()
            .uri("/reactive/snapshot")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(4)
            .jsonPath("$.userId").value<Any> { isA<Any>(String::class.java) }
            .jsonPath("$.traceId").value<Any> { isA<Any>(String::class.java) }
            .jsonPath("$.spanId").value<Any> { isA<Any>(String::class.java) }
            .jsonPath("$.parentId").value<Any> { nullValue() }
    }

    @Test
    fun getReadTest() {
        webTestClient.get()
            .uri("/reactive/read")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value<Any> { isA<Any>(String::class.java) }
            .jsonPath("$.traceId").doesNotExist()
            .jsonPath("$.spanId").doesNotExist()
            .jsonPath("$.parentId").doesNotExist()
    }

    /**
     * Console example:
     * ```
     * {"message":"log0-information","context":{"spanId":"5a09cd61ad8429a6","traceId":"5a09cd61ad8429a6","userId":"de9a559a-0328-467c-8cbd-883062226d78","parentId":null}}
     * ```
     */
    @Test
    fun doInfo0Test() {
        webTestClient.get()
            .uri("/reactive/log0")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * Console example:
     * ```
     * {"message":"log1-information","context":{"spanId":"2a185b36d676440c","traceId":"2a185b36d676440c","parentId":null,"foo":"bar"}}
     * ```
     */
    @Test
    fun doInfo1Test() {
        webTestClient.get()
            .uri("/reactive/log1")
            .exchange()
            .expectStatus().isOk
    }

    /**
     * Console example:
     * ```
     * {"message":"log2-information","context":{"spanId":"36ff90c5ead25d26","traceId":"36ff90c5ead25d26","TraceContext.hashCode":"-1191884682","parentId":null}}
     * ```
     */
    @Test
    fun doInfo2Test() {
        webTestClient.get()
            .uri("/reactive/log2")
            .exchange()
            .expectStatus().isOk
    }
}