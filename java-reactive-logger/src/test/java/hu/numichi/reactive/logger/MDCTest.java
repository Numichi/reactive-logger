package hu.numichi.reactive.logger;

import hu.numichi.reactive.logger.exception.InvalidContextDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MDCTest {
    
//    @Test
//    @DisplayName("MDC.of() - should give default values")
//    void mdcOfTest() {
//        MDC mdc1 = MDC.of();
//        assertEquals(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc1.getMdcContextKey());
//        assertEquals(new HashMap<String, String>(), mdc1.getMdcMap());
//    }
//
//    @Test
//    @DisplayName("MDC.of(String) - should give empty map and configured key")
//    void mdcOfContextKeyTest() {
//        String randomString = UUID.randomUUID().toString();
//        MDC mdc2 = MDC.of(randomString);
//        assertEquals(randomString, mdc2.getMdcContextKey());
//        assertEquals(new HashMap<String, String>(), mdc2.getMdcMap());
//    }
//
//    @Test
//    @DisplayName("MDC.restore() - should give default values")
//    void mdcRestoreWithEmptyContextTest() {
//        Mono<MDC> mdc1 = MDC.restore();
//        StepVerifier.create(mdc1)
//            .expectNextMatches((predicate) ->
//                predicate.getMdcContextKey().equals(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY) && predicate.getMdcMap().isEmpty()
//            )
//            .verifyComplete();
//    }
//
//    @Test
//    @DisplayName("MDC.restore(String) - should give empty map and configured key")
//    void mdcRestoreContextKeyWithEmptyContextTest() {
//        String randomString = UUID.randomUUID().toString();
//        Mono<MDC> mdc2 = MDC.restore(randomString);
//        StepVerifier.create(mdc2)
//            .expectNextMatches((predicate) ->
//                predicate.getMdcContextKey().equals(randomString) && predicate.getMdcMap().isEmpty()
//            )
//            .verifyComplete();
//    }
//
//    @Test
//    @DisplayName("MDC.restore(ContextView) - should give empty map and configured key")
//    void mdcRestoreContextViewWithEmptyContextTest() {
//        Map<String, Map<String, String>> map = new HashMap<>();
//        map.put(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, Collections.singletonMap("key", "value"));
//
//        Mono<MDC> mdc2 = MDC.restore(Context.of(map));
//
//        StepVerifier.create(mdc2)
//            .expectNextMatches((predicate) ->
//                predicate.getMdcContextKey().equals(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY)
//                    && predicate.getMdcMap().get("key").equals("value")
//            )
//            .verifyComplete();
//    }
//
//    @Test
//    @DisplayName("MDC.restore(ContextView) - should give error with wrong context")
//    void mdcRestoreContextViewWithWrongContextTest() {
//        Map<String, Object> map = new HashMap<>();
//        map.put(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, 1000);
//
//        Mono<MDC> mdc2 = MDC.restore(Context.of(map));
//
//        StepVerifier.create(mdc2)
//            .expectError(InvalidContextDataException.class)
//            .verify();
//    }
}
