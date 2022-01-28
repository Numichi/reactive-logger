package io.github.numichi.reactive.logger.reactor;

import io.github.numichi.reactive.logger.java.MDCSnapshot;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MDCSnapshotTest {
    
    static String randomText() {
        return UUID.randomUUID().toString();
    }
    
    @Test
    void populateAndClear() {
        final Map<String, String> expected = new HashMap<>();
        expected.put(randomText(), randomText());
        expected.put(randomText(), randomText());
    
        assertEquals(new HashMap<>(), MDC.getCopyOfContextMap());
        
        try (final MDCSnapshot snapshot = MDCSnapshot.of(expected)) {
            assertEquals(MDC.getCopyOfContextMap(), expected);
        }
    
        assertEquals(new HashMap<>(), MDC.getCopyOfContextMap());
    }
    
    @Test
    void createEmptyInstance() {
        MDC.put(randomText(), randomText());
        
        try (final MDCSnapshot snapshot = MDCSnapshot.empty()) {
            assertEquals(new HashMap<>(), MDC.getCopyOfContextMap());
        }
    }
}