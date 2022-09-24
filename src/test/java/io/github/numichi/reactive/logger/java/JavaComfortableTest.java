package io.github.numichi.reactive.logger.java;

import io.github.numichi.reactive.logger.Configuration;
import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.reactor.ReactiveLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class JavaComfortableTest {
    
    @BeforeEach
    void beforeEach() {
        Configuration.reset();
    }
    
    @Test
    void comfortableTest() {
        Configuration.addGenericHook("test", "foo", (String value, MDC mdc) -> Map.of("key", value));
        assertEquals(1, Configuration.getHooks().size());
        
        Configuration.setDefaultReactorContextMdcKey("custom1");
        
        var logger = ReactiveLogger.getLogger(JavaComfortableTest.class);
        assertEquals("custom1", logger.getContextKey());
        
        Configuration.setDefaultReactorContextMdcKey("custom2");
        
        var logger2 = ReactiveLogger.getLogger(JavaComfortableTest.class);
        assertEquals("custom1", logger.getContextKey());
        assertEquals("custom2", logger2.getContextKey());
        
        var logger3 = ReactiveLogger.getLogger(JavaComfortableTest.class, "custom3", null);
        assertEquals("custom1", logger.getContextKey());
        assertEquals("custom2", logger2.getContextKey());
        assertEquals("custom3", logger3.getContextKey());
        assertSame(Schedulers.boundedElastic(), logger3.getScheduler());
    }
}
