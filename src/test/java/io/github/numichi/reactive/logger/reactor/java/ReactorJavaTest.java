package io.github.numichi.reactive.logger.reactor.java;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.reactor.IReactorLogger;
import io.github.numichi.reactive.logger.reactor.MDCContext;
import io.github.numichi.reactive.logger.reactor.ReactiveLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.Mockito.mock;

public class ReactorJavaTest {
    
    @Test
    public void javaBaseTest1() {
        IReactorLogger logger = ReactiveLogger.builder().withLogger(mock(Logger.class)).build();
        
        MDC model = new MDC();
        model.put("bar", "baz");
        
        Mono<MDC> mono = Mono.deferContextual(logger::snapshot)
            .contextWrite((Context contextView) -> MDCContext.put(contextView, model));
    
        StepVerifier.create(mono)
            .expectNextMatches((MDC mdc) -> mdc.equals(model))
            .verifyComplete();
    }
}
