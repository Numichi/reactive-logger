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
internal class CoroutineLoggerControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun getSnapshotTest() {
        webTestClient.get()
            .uri("/coroutine/snapshot")
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
            .uri("/coroutine/read")
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
     * {"message":"log0-information","context":{"spanId":"8a71c35ea16503dd","traceId":"8a71c35ea16503dd","userId":"57b512fc-d979-46e4-8b20-6c65ebd86916","parentId":null}}
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
     * Console example (used logger 2x):
     * ```
     * {"message":"log1-information","context":{"spanId":"71cb0fdcaa1dcf48","traceId":"71cb0fdcaa1dcf48","parentId":null,"foo":"bar"}}
     * {"message":"log1-information","context":{"spanId":"71cb0fdcaa1dcf48","traceId":"71cb0fdcaa1dcf48","parentId":null,"foo":"bar"}}
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
     * {"message":"log2-information","context":{"spanId":"bdd6f60e89b518db","traceId":"bdd6f60e89b518db","TraceContext.hashCode":"-1527658078","parentId":null}}`
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