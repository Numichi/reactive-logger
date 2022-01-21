package hu.numichi.reactive.logger.java;

import hu.numichi.reactive.logger.exception.ContextNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.annotation.NonNull;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReactiveLoggerTest {
    
    private final Logger imperativeLogger = mock(Logger.class);
    private final ReactiveLogger logger = ReactiveLogger.builder().withLogger(imperativeLogger).build();
    private final ReactiveLogger loggerWithError = ReactiveLogger.builder().withLogger(imperativeLogger).enableError().build();
    
    static String randomText() {
        return UUID.randomUUID().toString();
    }
    
    @NonNull
    static Map<String, String> randomMap(int i) {
        final Map<String, String> expected = new HashMap<>();
        
        while (0 < i) {
            expected.put(randomText(), randomText());
            i--;
        }
        
        return expected;
    }
    
    @BeforeEach
    void afterEach() {
        reset(imperativeLogger);
    }
    
    @Test
    @SuppressWarnings("squid:S5778")
    void builderWithClassLogger() {
        assertThrows(NullPointerException.class, () -> ReactiveLogger.builder().withLogger((Class) null).build());
        assertThrows(NullPointerException.class, () -> ReactiveLogger.builder().withLogger((String) null).build());
        assertThrows(NullPointerException.class, () -> ReactiveLogger.builder().withLogger((Logger) null).build());
    
        assertNotNull(ReactiveLogger.builder().withLogger(this.getClass()).build());
        assertNotNull(ReactiveLogger.builder().withLogger("any").build());
        assertNotNull(ReactiveLogger.builder().withLogger(LoggerFactory.getLogger(this.getClass())).build());
    }
    
    @Test
    void getName() {
        final String name = randomText();
        when(imperativeLogger.getName()).thenReturn(name);
        assertEquals(name, logger.getName());
    }
    
    @Test
    void readMDC() {
        final Map<String, String> mdc = randomMap(1);
        final Context context = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
        assertEquals(Optional.of(mdc), logger.readMDC(context));
        assertEquals(Optional.of(mdc).get(), logger.readMDC(context).get());
    }
    
    @Test
    void takeMDCSnapshot() {
        final Map<String, String> mdc = randomMap(1);
        final Context context = Context.of(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
        try (final MDCSnapshot snapshot = logger.takeMDCSnapshot(context)) {
            assertEquals(MDC.getCopyOfContextMap(), mdc);
        }
    }
    
    @Test
    void imperative() {
        assertSame(logger.imperative(), imperativeLogger);
        assertSame(loggerWithError.imperative(), imperativeLogger);
    }
    
    @Test
    void contextKey() {
        final String contextKey = "another-context-key";
        final ReactiveLogger loggerWithCustomScheduler = ReactiveLogger.builder().withMDCContextKey(contextKey).build();
        assertSame(loggerWithCustomScheduler.mdcContextKey(), contextKey);
    
        assertThrows(IllegalArgumentException.class, () -> ReactiveLogger.builder().withMDCContextKey("").build());
        assertThrows(IllegalArgumentException.class, () -> ReactiveLogger.builder().withMDCContextKey(" ").build());
    }
    
    @Test
    void scheduler() {
        final Scheduler customScheduler = Schedulers.newBoundedElastic(10, 10, randomText());
        final ReactiveLogger loggerWithCustomScheduler = ReactiveLogger.builder().withScheduler(customScheduler).build();
        assertSame(loggerWithCustomScheduler.scheduler(), customScheduler);
    }
    
    @Test
    void traceEnabled() {
        when(imperativeLogger.isTraceEnabled()).thenReturn(true, false, true);
        assertTrue(logger.isTraceEnabled(), "trace not enabled when it should be");
        assertFalse(logger.isTraceEnabled(), "trace enabled when it should not be");
        assertTrue(logger.isTraceEnabled(), "trace not enabled when it should be");
    }
    
    @Test
    void traceEnabledMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        when(imperativeLogger.isTraceEnabled(marker)).thenReturn(true, false, true);
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be");
        assertFalse(logger.isTraceEnabled(marker), "trace enabled when it should not be");
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be");
    }
    
    @Test
    void debugEnabled() {
        when(imperativeLogger.isDebugEnabled()).thenReturn(true, false, true);
        assertTrue(logger.isDebugEnabled(), "debug not enabled when it should be");
        assertFalse(logger.isDebugEnabled(), "debug enabled when it should not be");
        assertTrue(logger.isDebugEnabled(), "debug not enabled when it should be");
    }
    
    @Test
    void debugEnabledMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        when(imperativeLogger.isDebugEnabled(marker)).thenReturn(true, false, true);
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be");
        assertFalse(logger.isDebugEnabled(marker), "debug enabled when it should not be");
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be");
    }
    
    @Test
    void infoEnabled() {
        when(imperativeLogger.isInfoEnabled()).thenReturn(true, false, true);
        assertTrue(logger.isInfoEnabled(), "info not enabled when it should be");
        assertFalse(logger.isInfoEnabled(), "info enabled when it should not be");
        assertTrue(logger.isInfoEnabled(), "info not enabled when it should be");
    }
    
    @Test
    void infoEnabledMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        when(imperativeLogger.isInfoEnabled(marker)).thenReturn(true, false, true);
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be");
        assertFalse(logger.isInfoEnabled(marker), "info enabled when it should not be");
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be");
    }
    
    @Test
    void warnEnabled() {
        when(imperativeLogger.isWarnEnabled()).thenReturn(true, false, true);
        assertTrue(logger.isWarnEnabled(), "warn not enabled when it should be");
        assertFalse(logger.isWarnEnabled(), "warn enabled when it should not be");
        assertTrue(logger.isWarnEnabled(), "warn not enabled when it should be");
    }
    
    @Test
    void warnEnabledMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        when(imperativeLogger.isWarnEnabled(marker)).thenReturn(true, false, true);
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be");
        assertFalse(logger.isWarnEnabled(marker), "warn enabled when it should not be");
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be");
    }
    
    @Test
    void errorEnabled() {
        when(imperativeLogger.isErrorEnabled()).thenReturn(true, false, true);
        assertTrue(logger.isErrorEnabled(), "error not enabled when it should be");
        assertFalse(logger.isErrorEnabled(), "error enabled when it should not be");
        assertTrue(logger.isErrorEnabled(), "error not enabled when it should be");
    }
    
    @Test
    void errorEnabledMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        when(imperativeLogger.isErrorEnabled(marker)).thenReturn(true, false, true);
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be");
        assertFalse(logger.isErrorEnabled(marker), "error enabled when it should not be");
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be");
    }
    
    @Test
    void traceMessage() {
        final String message = randomText();
        logger.trace(message).block();
        verify(imperativeLogger).trace(message);
    }
    
    @Test
    void traceFormatArgumentArray() {
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.trace(format, argument1, argument2, argument3).block();
        verify(imperativeLogger).trace(format, argument1, argument2, argument3);
    }
    
    @Test
    void traceMessageThrowable() {
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.trace(message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).trace(eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void traceMessageMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        logger.trace(marker, message).block();
        verify(imperativeLogger).trace(marker, message);
    }
    
    @Test
    void traceFormatArgumentArrayMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.trace(marker, format, argument1, argument2, argument3).block();
        verify(imperativeLogger).trace(marker, format, argument1, argument2, argument3);
    }
    
    @Test
    void traceMessageThrowableMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.trace(marker, message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).trace(eq(marker), eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void debugMessage() {
        final String message = randomText();
        logger.debug(message).block();
        verify(imperativeLogger).debug(message);
    }
    
    @Test
    void debugFormatArgumentArray() {
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.debug(format, argument1, argument2, argument3).block();
        verify(imperativeLogger).debug(format, argument1, argument2, argument3);
    }
    
    @Test
    void debugMessageThrowable() {
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.debug(message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).debug(eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void debugMessageMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        logger.debug(marker, message).block();
        verify(imperativeLogger).debug(marker, message);
    }
    
    @Test
    void debugFormatArgumentArrayMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.debug(marker, format, argument1, argument2, argument3).block();
        verify(imperativeLogger).debug(marker, format, argument1, argument2, argument3);
    }
    
    @Test
    void debugMessageThrowableMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.debug(marker, message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).debug(eq(marker), eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void infoMessage() {
        final String message = randomText();
        logger.info(message).block();
        verify(imperativeLogger).info(message);
    }
    
    @Test
    void infoFormatArgumentArray() {
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.info(format, argument1, argument2, argument3).block();
        verify(imperativeLogger).info(format, argument1, argument2, argument3);
    }
    
    @Test
    void infoMessageThrowable() {
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.info(message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).info(eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void infoMessageMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        logger.info(marker, message).block();
        verify(imperativeLogger).info(marker, message);
    }
    
    @Test
    void infoFormatArgumentArrayMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.info(marker, format, argument1, argument2, argument3).block();
        verify(imperativeLogger).info(marker, format, argument1, argument2, argument3);
    }
    
    @Test
    void infoMessageThrowableMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.info(marker, message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).info(eq(marker), eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void warnMessage() {
        final String message = randomText();
        logger.warn(message).block();
        verify(imperativeLogger).warn(message);
    }
    
    @Test
    void warnFormatArgumentArray() {
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.warn(format, argument1, argument2, argument3).block();
        verify(imperativeLogger).warn(format, argument1, argument2, argument3);
    }
    
    @Test
    void warnMessageThrowable() {
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.warn(message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).warn(eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void warnMessageMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        logger.warn(marker, message).block();
        verify(imperativeLogger).warn(marker, message);
    }
    
    @Test
    void warnFormatArgumentArrayMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.warn(marker, format, argument1, argument2, argument3).block();
        verify(imperativeLogger).warn(marker, format, argument1, argument2, argument3);
    }
    
    @Test
    void warnMessageThrowableMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.warn(marker, message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).warn(eq(marker), eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void errorMessage() {
        final String message = randomText();
        logger.error(message).block();
        verify(imperativeLogger).error(message);
    }
    
    @Test
    void errorFormatArgumentArray() {
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.error(format, argument1, argument2, argument3).block();
        verify(imperativeLogger).error(format, argument1, argument2, argument3);
    }
    
    @Test
    void errorMessageThrowable() {
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.error(message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).error(eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void errorMessageMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        logger.error(marker, message).block();
        verify(imperativeLogger).error(marker, message);
    }
    
    @Test
    void errorFormatArgumentArrayMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String format = randomText();
        final String argument1 = randomText();
        final String argument2 = randomText();
        final String argument3 = randomText();
        logger.error(marker, format, argument1, argument2, argument3).block();
        verify(imperativeLogger).error(marker, format, argument1, argument2, argument3);
    }
    
    @Test
    void errorMessageThrowableMarker() {
        final Marker marker = MarkerFactory.getMarker(randomText());
        final String message = randomText();
        final SimulatedException exception = new SimulatedException(randomText());
        logger.error(marker, message, exception).block();
        
        final ArgumentCaptor<SimulatedException> exceptionCaptor = ArgumentCaptor.forClass(SimulatedException.class);
        verify(imperativeLogger).error(eq(marker), eq(message), exceptionCaptor.capture());
        assertEquals(exceptionCaptor.getValue().getMessage(), exception.getMessage());
    }
    
    @Test
    void checkEnableErrorFlagDifferent() {
        final String message = randomText();
        
        Mono<Context> process = Mono.defer(() ->  loggerWithError.error(message))
            .contextWrite((ctx) -> Context.empty());
        
        StepVerifier.create(process).expectError(ContextNotExistException.class).verify();
    }
    
    public static class SimulatedException extends RuntimeException {
        public SimulatedException(final String message) {
            super(message);
        }
    }
}