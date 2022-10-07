package io.github.numichi.reactive.logger.example.java;

import io.github.numichi.reactive.logger.Configuration;
import io.github.numichi.reactive.logger.exceptions.ReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.*;

class ExampleComponentTest {
    
    private final Logger logger = Mockito.mock(Logger.class);
    private final ExampleComponent component = new ExampleComponent(logger);
    
    @BeforeEach
    void beforeEach() {
        Configuration.reset();
        Mockito.reset(logger);
    }
    
    @Test
    void example1Test() {
        StepVerifier.create(component.example1())
            .expectNext(Map.of("foo", "bar"))
            .verifyComplete();
        
        verify(logger).info("example1");
    }
    
    @Test
    void example2Test() {
        StepVerifier.create(component.example2()).verifyComplete();
        
        verify(logger, times(0)).trace(any());
        verify(logger, times(1)).debug("bar");
        verify(logger, times(1)).info("null");
        verify(logger, times(0)).warn(any());
    }
    
    @Test
    void example3Test() {
        StepVerifier.create(component.example3()).verifyComplete();
        
        verify(logger, times(0)).trace(any());
        verify(logger, times(1)).debug("bar");
        verify(logger, times(1)).info("null");
        verify(logger, times(0)).warn(any());
    }
    
    @Test
    void example4Test() {
        StepVerifier.create(component.example4()).verifyError(ReadException.class);

        verify(logger).error("DEFAULT_REACTOR_CONTEXT_MDC_KEY context key is not contain in context");
    }
}