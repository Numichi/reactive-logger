package hu.numichi.reactive.logger.java;

import hu.numichi.reactive.logger.MDC;
import hu.numichi.reactive.logger.exception.InvalidContextDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;

import static hu.numichi.reactive.logger.Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MDCContextTest {
    
    public static final String ANOTHER_CONTEXT_KEY = "another-context-key";
    
    @Test
    @DisplayName("should give the MDC you are looking for")
    void shouldGiveTheMDCByDefault() {
        MDC mdc1 = new MDC();
        mdc1.put("mdcKey", "mdcValue");
        
        MDC mdc2 = new MDC(ANOTHER_CONTEXT_KEY);
        mdc2.put("mdcKey", "mdcValue");
        
        
        Mono<MDC> resultDefaultByContext = Mono.deferContextual(MDCContext::read)
            .contextWrite(it -> MDCContext.put(it, mdc1));
        
        StepVerifier.create(resultDefaultByContext)
            .expectNext(mdc1)
            .verifyComplete();
        
        
        Mono<MDC> resultDefault = Mono.defer(MDCContext::read)
            .contextWrite(it -> MDCContext.put(it, mdc1));
        
        StepVerifier.create(resultDefault)
            .expectNext(mdc1)
            .verifyComplete();
        
        
        Mono<MDC> resultAnotherByContext = Mono.deferContextual(ctx -> MDCContext.read(ctx, ANOTHER_CONTEXT_KEY))
            .contextWrite(it -> MDCContext.put(it, mdc2));
        
        StepVerifier.create(resultAnotherByContext)
            .expectNext(mdc2)
            .verifyComplete();
        
        
        Mono<MDC> resultAnother = Mono.defer(() -> MDCContext.read(ANOTHER_CONTEXT_KEY))
            .contextWrite(it -> MDCContext.put(it, mdc2));
        
        StepVerifier.create(resultAnother)
            .expectNext(mdc2)
            .verifyComplete();
    }
    
    @Test
    @DisplayName("should give the MDC you are looking for (more MDC Context)")
    void shouldGiveTheMDCWithMoreMdcContext() {
        MDC defaultMdc = new MDC();
        defaultMdc.put("mdcKey", "mdcValue");
        
        MDC anotherMdc = new MDC(ANOTHER_CONTEXT_KEY);
        anotherMdc.put("mdcKey", "mdcValue");
        
        
        Mono<MDC> resultDefault = Mono.defer(MDCContext::read)
            .contextWrite(it -> MDCContext.put(it, defaultMdc))
            .contextWrite(it -> MDCContext.put(it, anotherMdc));
        
        StepVerifier.create(resultDefault)
            .expectNext(defaultMdc)
            .verifyComplete();
        
        
        Mono<MDC> resultAnother = Mono.defer(() -> MDCContext.read(ANOTHER_CONTEXT_KEY))
            .contextWrite(it -> MDCContext.put(it, defaultMdc))
            .contextWrite(it -> MDCContext.put(it, anotherMdc));
        
        StepVerifier.create(resultAnother)
            .expectNext(anotherMdc)
            .verifyComplete();
    }
    
    @Test
    @DisplayName("should need to be able to store a map")
    void shouldNeedToBeAbleToStoreAMap() {
        Map<String, String> mdcMap = new HashMap<>();
        mdcMap.put("mdcKey", "mdcValue");
        
        
        Mono<MDC> resultDefault = Mono.defer(MDCContext::read)
            .contextWrite(it -> MDCContext.put(it, mdcMap));
        
        StepVerifier.create(resultDefault)
            .expectNextMatches(mdc1 -> mdc1.asMap().equals(mdcMap) && mdc1.getContextKey().equals(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            .verifyComplete();
        
        
        Mono<MDC> resultAnother = Mono.defer(() -> MDCContext.read(ANOTHER_CONTEXT_KEY))
            .contextWrite(it -> MDCContext.put(it, ANOTHER_CONTEXT_KEY, mdcMap));
        
        StepVerifier.create(resultAnother)
            .expectNextMatches(mdc1 -> mdc1.asMap().equals(mdcMap) && mdc1.getContextKey().equals(ANOTHER_CONTEXT_KEY))
            .verifyComplete();
    }
    
    @Test
    @DisplayName("should be MDC stored with the overridden context ID")
    void shouldBeStoredWithTheOverriddenContextIDTest() {
        MDC mdc = new MDC();
        mdc.put("mdcKey", "mdcValue");
        
        Mono<MDC> result = Mono.defer(() -> MDCContext.read(ANOTHER_CONTEXT_KEY))
            .contextWrite(it -> MDCContext.put(it, ANOTHER_CONTEXT_KEY, mdc));
        
        StepVerifier.create(result)
            .expectNextMatches(mdc1 -> mdc1.asMap().equals(mdc.asMap()) && mdc1.getContextKey().equals(ANOTHER_CONTEXT_KEY))
            .verifyComplete();
    }
    
    @Test
    @DisplayName("should throw IllegalArgumentException if any parameter is NULL")
    void shouldThrowIllegalArgumentExceptionTest() {
        Map<String, String> emptyMap = new HashMap<>();
        
        assertThrows(IllegalArgumentException.class, () -> MDCContext.put(null, "", emptyMap));
        assertThrows(IllegalArgumentException.class, () -> MDCContext.put(Context.empty(), null, emptyMap));
        assertThrows(IllegalArgumentException.class, () -> MDCContext.put(Context.empty(), "", null));
    
        StepVerifier.create(MDCContext.read((String) null))
            .expectError(IllegalArgumentException.class)
            .verify();
    
        StepVerifier.create(MDCContext.read(null, ""))
            .expectError(IllegalArgumentException.class)
            .verify();
    
        StepVerifier.create(MDCContext.read(Context.of("", ""), null))
            .expectError(IllegalArgumentException.class)
            .verify();
    }
    
    @Test
    @DisplayName("should throw InvalidContextDataException if context data is invalid")
    void shouldThrowInvalidContextDataException() {
        // ANOTHER_CONTEXT_KEY is not DEFAULT_REACTOR_CONTEXT_MDC_KEY
        MDC mdc = new MDC(ANOTHER_CONTEXT_KEY);
        mdc.put("mdcKey2", "mdcValue2");
        Mono<MDC> result1 = Mono.defer(MDCContext::read)
            .contextWrite(it -> MDCContext.put(it, mdc));
        StepVerifier.create(result1)
            .expectError(InvalidContextDataException.class)
            .verify();
    
        
       
        
        
        Mono<MDC> result2 = Mono.defer(() -> MDCContext.read("not-exist-context-id"))
            .contextWrite(it -> MDCContext.put(it, mdc));
        StepVerifier.create(result2)
            .expectError(InvalidContextDataException.class)
            .verify();
        
        
        Mono<MDC> result3 = Mono.defer(MDCContext::read)
            .contextWrite(it -> it.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, ""));
        StepVerifier.create(result3)
            .expectError(InvalidContextDataException.class)
            .verify();
        
        
        Mono<MDC> result4 = Mono.defer(MDCContext::read)
            .contextWrite(it -> it.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, 100));
        StepVerifier.create(result4)
            .expectError(InvalidContextDataException.class)
            .verify();
    }
}