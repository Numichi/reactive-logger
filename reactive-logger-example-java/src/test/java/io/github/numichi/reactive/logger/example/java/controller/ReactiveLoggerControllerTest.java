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
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$.userId").value(v -> isA(String.class))
            .jsonPath("$.example").value(v -> isA(String.class));
    }
    
    @Test
    void getReadTest() {
        webTestClient.get()
            .uri("/reactive/read")
            .exchange()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value(v -> isA(String.class));
    }
    
    /**
     * Console example:
     * <pre>{@code
     * {"message":"log0-information","context":{"userId":"c76cb63c-1742-4e77-9a52-d8593ce36236","example":"example"}}
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
     * {"message":"log1-information","context":{"example":"example","foo":"bar"}}
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
     * {"message":"log2-information","context":{"example":"n/a"}}
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