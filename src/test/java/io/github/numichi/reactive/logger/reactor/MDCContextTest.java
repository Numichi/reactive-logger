package io.github.numichi.reactive.logger.reactor;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.exception.InvalidContextDataException;
import io.github.numichi.reactive.logger.java.MDCContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static io.github.numichi.reactive.logger.Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
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
    @DisplayName("should return the appropriate contexts count")
    void shouldReturnTheAppropriateContextsCount() {
        MDC defaultMdc = new MDC();
        defaultMdc.put("mdcKey", "mdcValue");
    
        MDC anotherMdc = new MDC(ANOTHER_CONTEXT_KEY);
        anotherMdc.put("mdcKey", "mdcValue");
    
        Mono<Integer> contextSize1 = Mono.deferContextual(ctx -> Mono.just(ctx.size()))
            .contextWrite(it -> MDCContext.put(it, defaultMdc))
            .contextWrite(it -> MDCContext.put(it, anotherMdc));
        StepVerifier.create(contextSize1)
            .expectNext(2)
            .verifyComplete();
    
    
        Mono<Integer> contextSize2 = Mono.deferContextual(ctx -> Mono.just(ctx.size()))
            .contextWrite(it -> MDCContext.put(it, defaultMdc, anotherMdc));
        StepVerifier.create(contextSize2)
            .expectNext(2)
            .verifyComplete();
    
    
        Mono<Integer> contextSize3 = Mono.deferContextual(ctx -> Mono.just(ctx.size()))
            .contextWrite(it -> MDCContext.put(it, defaultMdc, null));
        StepVerifier.create(contextSize3)
            .expectNext(1)
            .verifyComplete();
    
        
        Mono<Integer> contextSize4 = Mono.deferContextual(ctx -> Mono.just(ctx.size()))
            .contextWrite(it -> MDCContext.put(it, defaultMdc, null))
            .contextWrite(it -> it.put("A", "B"));
        StepVerifier.create(contextSize4)
            .expectNext(2)
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
    @DisplayName("should throw IllegalArgumentException if any parameter is NULL")
    void shouldThrowIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> MDCContext.put(null, new MDC()));
        assertThrows(IllegalArgumentException.class, () -> MDCContext.put(Context.empty(), null));
    
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