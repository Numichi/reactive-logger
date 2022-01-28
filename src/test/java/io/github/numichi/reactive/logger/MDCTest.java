package io.github.numichi.reactive.logger;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MDCTest {
    
    @Test
    void asMap() {
        MDC mdc = new MDC();
        mdc.put("111", "222");
        
        assertEquals(mdc, mdc.asMap());
    }
    
    @Test
    void getContextKey() {
        MDC mdc = new MDC();
    
        assertEquals(mdc.getAssignedContextKey(), mdc.getContextKey());
    }
    
    @Test
    void testEquals() {
        MDC mdc1 = new MDC();
        mdc1.put("111", "222");
        
        MDC mdc2 = new MDC();
        mdc2.put("111", "222");
    
        MDC mdc3 = new MDC();
        mdc3.put("333", "444");
    
        assertEquals(mdc1, mdc1);
        assertEquals(mdc1, mdc2);
        assertNotEquals(mdc1, mdc3);
        assertFalse(mdc1.equals(Instant.now()));
    }
    
    @Test
    void testHashCode() {
        MDC mdc1 = new MDC();
        mdc1.put("111", "222");
    
        MDC mdc2 = new MDC();
        mdc2.put("111", "222");
        
        assertEquals(mdc1.hashCode(), mdc2.hashCode());
    }
}