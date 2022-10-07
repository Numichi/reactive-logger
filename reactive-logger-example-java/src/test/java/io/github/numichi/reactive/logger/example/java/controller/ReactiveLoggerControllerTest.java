package io.github.numichi.reactive.logger.example.java.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReactiveLoggerControllerTest {
    
    @Autowired
    WebTestClient webTestClient;
    
    @Test
    void getSnapshotTest() {
        webTestClient.get()
            .uri("/reactive/snapshot")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(4)
            .jsonPath("$.userId").value(v -> isA(String.class))
            .jsonPath("$.traceId").value(v -> isA(String.class))
            .jsonPath("$.spanId").value(v -> isA(String.class))
            .jsonPath("$.parentId").value(nullValue());
    }
    
    @Test
    void getReadTest() {
        webTestClient.get()
            .uri("/reactive/read")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value(v -> isA(String.class))
            .jsonPath("$.traceId").doesNotExist()
            .jsonPath("$.spanId").doesNotExist()
            .jsonPath("$.parentId").doesNotExist();
    }
    
    /**
     * Console example:
     * <pre>{@code
     * {"message":"log0-information","context":{"spanId":"407116d8f3b75f95","traceId":"407116d8f3b75f95","userId":"875bc35e-136f-40a3-b999-312978e706d7","parentId":null}}
     * }</pre>
     */
    @Test
    void doInfo0Test() {
        webTestClient.get()
            .uri("/reactive/log0")
            .exchange()
            .expectStatus().isOk();
    }
    
    /**
     * Console example:
     * <pre>{@code
     * {"message":"log1-information","context":{"spanId":"e4ff28fe00e63ef5","traceId":"e4ff28fe00e63ef5","parentId":null,"foo":"bar"}}
     * }</pre>
     */
    @Test
    void doInfo1Test() {
        webTestClient.get()
            .uri("/reactive/log1")
            .exchange()
            .expectStatus().isOk();
    }
    
    /**
     * Console example:
     * <pre>{@code
     * {"message":"log2-information","context":{"spanId":"57b177a1db629600","traceId":"57b177a1db629600","TraceContext.hashCode":"2033809386","parentId":null}}
     * }</pre>
     */
    @Test
    void doInfo2Test() {
        webTestClient.get()
            .uri("/reactive/log2")
            .exchange()
            .expectStatus().isOk();
    }
}