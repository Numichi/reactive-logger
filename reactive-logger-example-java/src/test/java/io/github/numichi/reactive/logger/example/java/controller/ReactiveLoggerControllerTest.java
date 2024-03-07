package io.github.numichi.reactive.logger.example.java.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReactiveLoggerControllerTest {
    public static final Logger log = LoggerFactory.getLogger(ReactiveLoggerControllerTest.class);

    public static Consumer<EntityExchangeResult<byte[]>> consumer() {
        return entityExchangeResult -> {
            byte[] responseBody = entityExchangeResult.getResponseBody();
            assert responseBody != null;
            String responseBodyString = new String(responseBody);

            log.info(responseBodyString);
        };
    }

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient.mutate()
            .responseTimeout(Duration.ofMinutes(1))
            .build();
    }

    /**
     * @see ReactiveLoggerController#getSnapshot()
     */
    @Test
    void getSnapshotTest() {
        webTestClient.get()
            .uri("/reactive/snapshot")
            .exchange()
            .expectBody()
            .consumeWith(consumer())
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$.userId").value(v -> isA(String.class))
            .jsonPath("$.example").value(v -> isA(String.class));
    }

    /**
     * @see ReactiveLoggerController#getRead()
     */
    @Test
    void getReadTest() {
        webTestClient.get()
            .uri("/reactive/read")
            .exchange()
            .expectBody()
            .consumeWith(consumer())
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$.userId").value(v -> isA(String.class));
    }

    /**
     * @see ReactiveLoggerController#doInfo0()
     */
    @Test
    void doInfo0Test() {
        webTestClient.get()
            .uri("/reactive/log0")
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * @see ReactiveLoggerController#doInfo1()
     */
    @Test
    void doInfo1Test() {
        webTestClient.get()
            .uri("/reactive/log1")
            .exchange()
            .expectStatus().isOk();
    }

    /**
     * @see ReactiveLoggerController#doInfo2()
     */
    @Test
    void doInfo2Test() {
        webTestClient.get()
            .uri("/reactive/log2")
            .exchange()
            .expectStatus().isOk();
    }
}