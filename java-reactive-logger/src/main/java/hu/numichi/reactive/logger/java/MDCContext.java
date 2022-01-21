package hu.numichi.reactive.logger.java;

import hu.numichi.reactive.logger.MDC;
import hu.numichi.reactive.logger.exception.InvalidContextDataException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Objects;

import static hu.numichi.reactive.logger.Consts.CTXK_NOT_NULL;
import static hu.numichi.reactive.logger.Consts.CTXW_NOT_NULL;
import static hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static hu.numichi.reactive.logger.Consts.MDC_NOT_NULL;

public final class MDCContext {
    private MDCContext() {
    }
    
    @NonNull
    public static Context put(@NonNull Context context, @NonNull Map<String, String> mdc) {
        Objects.requireNonNull(context, CTXK_NOT_NULL);
        
        return context.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }
    
    @NonNull
    public static Context put(@NonNull Context context, @NonNull String mdcContextKey, @NonNull Map<String, String> mdc) {
        Objects.requireNonNull(context, CTXK_NOT_NULL);
        
        return context.put(mdcContextKey, mdc);
    }
    
    @NonNull
    public static Context put(@NonNull Context context, @NonNull MDC mdc) {
        Objects.requireNonNull(context, CTXK_NOT_NULL);
        Objects.requireNonNull(mdc, MDC_NOT_NULL);
        
        return context.put(mdc.getContextKey(), mdc.getMap());
    }
    
    @NonNull
    public static Mono<MDC> read() {
        return read(DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static Mono<MDC> read(@NonNull String mdcContextKey) {
        return Mono.deferContextual(ctx -> read(ctx, mdcContextKey));
    }
    
    @NonNull
    public static Mono<MDC> read(@NonNull ContextView context) {
        return read(context, DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static Mono<MDC> read(@NonNull ContextView contextView, @NonNull String mdcContextKey) {
        try {
            Objects.requireNonNull(contextView, CTXW_NOT_NULL);
            Objects.requireNonNull(mdcContextKey, CTXK_NOT_NULL);
        } catch (NullPointerException exception) {
            return Mono.error(exception);
        }
        
        MDC mdc = new MDC(mdcContextKey);
        
        try {
            Map<String, String> map = contextView.get(mdcContextKey);
            mdc.putAll(map);
        } catch (Exception exception) {
            return Mono.error(new InvalidContextDataException(exception));
        }
        
        return Mono.just(mdc);
    }
}
