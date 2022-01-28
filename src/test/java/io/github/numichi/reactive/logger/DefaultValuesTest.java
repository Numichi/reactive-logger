package io.github.numichi.reactive.logger;

import io.github.numichi.reactive.logger.exception.AlreadyConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class DefaultValuesTest {
    
    private static final String DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY";

    @BeforeEach
    public void setUp() {
        DefaultValues.reset();
    }
    
    @AfterAll
    public static void tearDown() {
        DefaultValues.reset();
    }
    
    @Test
    public void configurationNotThrowExceptionIfUsedReset() {
        try {
            DefaultValues.configuration();
            DefaultValues.reset();
            DefaultValues.configuration();
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void configurationByDefault() {
        DefaultValues.configuration();
        DefaultValues instance = DefaultValues.getInstance();
        
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance.getDefaultReactorContextMdcKey());
        assertSame(Schedulers.boundedElastic(), instance.getDefaultScheduler());
    }
    
    @Test
    public void configurationByCustomKey() {
        DefaultValues.configuration("other-key");
        DefaultValues instance = DefaultValues.getInstance();
        
        assertEquals("other-key", instance.getDefaultReactorContextMdcKey());
        assertSame(Schedulers.boundedElastic(), instance.getDefaultScheduler());
    }
    
    @Test
    public void configurationByCustomScheduler() {
        DefaultValues.configuration(Schedulers.parallel());
        DefaultValues instance = DefaultValues.getInstance();
        
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance.getDefaultReactorContextMdcKey());
        assertSame(Schedulers.parallel(), instance.getDefaultScheduler());
    }
    
    @Test
    public void configurationByCustomSchedulerAndKey() {
        DefaultValues.configuration("other-key", Schedulers.parallel());
        DefaultValues instance = DefaultValues.getInstance();
        
        assertEquals("other-key", instance.getDefaultReactorContextMdcKey());
        assertSame(Schedulers.parallel(), instance.getDefaultScheduler());
    }
    
    @Test
    public void configurationErrorByDefault() {
        DefaultValues.configuration();
        
        assertThrows(AlreadyConfigurationException.class, DefaultValues::configuration);
    }
    
    @Test
    public void configurationErrorByCustomKey() {
        DefaultValues.configuration("other-key");
    
        assertThrows(AlreadyConfigurationException.class, DefaultValues::configuration);
        
        assertThrows(AlreadyConfigurationException.class, () -> {
            DefaultValues.configuration("other-key");
        });
    }
    
    @Test
    public void configurationErrorByCustomScheduler() {
        DefaultValues.configuration(Schedulers.parallel());
    
        assertThrows(AlreadyConfigurationException.class, DefaultValues::configuration);
        
        assertThrows(AlreadyConfigurationException.class, () -> {
            DefaultValues.configuration(Schedulers.parallel());
        });
    }
    
    @Test
    public void configurationErrorByCustomSchedulerAndKey() {
        DefaultValues.configuration("other-key", Schedulers.parallel());
    
        assertThrows(AlreadyConfigurationException.class, DefaultValues::configuration);
        
        assertThrows(AlreadyConfigurationException.class, () -> {
            DefaultValues.configuration("other-key", Schedulers.parallel());
        });
    }
}