package hu.numichi.reactive.logger;

import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.context.ContextView;

import java.util.HashMap;
import java.util.Map;

public final class MDC {
    
    private final Map<String, String> mdcMap;
    private final String mdcContextKey;
    
    public Map<String, String> getMdcMap() {
        return mdcMap;
    }
    
    public String getMdcContextKey() {
        return mdcContextKey;
    }
    
    private MDC(
        String mdcContextKey,
        Map<String, String> mdc
    ) {
        this.mdcMap = mdc;
        this.mdcContextKey = mdcContextKey;
    }
    
    @NonNull
    public static MDC of() {
        return of(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static MDC of(@NonNull String mdcContextKey) {
        return new MDC(mdcContextKey, new HashMap<>());
    }
    
    @NonNull
    public static Mono<MDC> restore() {
        return Mono.deferContextual(MDC::restore);
    }
    
    @NonNull
    public static Mono<MDC> restore(@NonNull ContextView contextView) {
        return restore(contextView, ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static Mono<MDC> restore(@NonNull String mdcContextKey) {
        return Mono.deferContextual(contextView -> restore(contextView, mdcContextKey));
    }
    
    @NonNull
    public static Mono<MDC> restore(
        @NonNull ContextView contextView,
        @NonNull String mdcContextKey
    ) {
        Map<String, String> data;
        
        try {
            data = contextView.getOrDefault(mdcContextKey, new HashMap<>());
        } catch (ClassCastException exception) {
            data = new HashMap<>();
        }
        
        return Mono.just(new MDC(mdcContextKey, data));
    }
}
