package io.github.numichi.reactive.logger.reactor;

import org.slf4j.MDC;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

import java.util.Map;

public final class MDCSnapshot implements AutoCloseable {
    private MDCSnapshot(@Nullable final Map<String, String> context) {
        if (context == null) {
            MDC.clear();
        } else {
            MDC.setContextMap(context);
        }
    }
    
    @NonNull
    public static MDCSnapshot of(@Nullable final Map<String, String> context) {
        return new MDCSnapshot(context);
    }
    
    @NonNull
    public static MDCSnapshot empty() {
        return new MDCSnapshot(null);
    }
    
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }
    
    @Override
    public void close() {
        MDC.clear();
    }
}
