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
import static hu.numichi.reactive.logger.Consts.CTX_NOT_NULL;
import static hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static hu.numichi.reactive.logger.Consts.MAP_NOT_NULL;
import static hu.numichi.reactive.logger.Consts.MDC_NOT_NULL;

public final class MDCContext {
    private MDCContext() {
    }
    
    @NonNull
    public static Context put(Context context, Map<String, String> mdc) {
        try {
            Objects.requireNonNull(context, CTXK_NOT_NULL);
            Objects.requireNonNull(mdc, MDC_NOT_NULL);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(exception);
        }
        
        return context.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }
    
    @NonNull
    public static Context put(Context context, String mdcContextKey, Map<String, String> mdc) {
        try {
            Objects.requireNonNull(context, CTX_NOT_NULL);
            Objects.requireNonNull(mdcContextKey, CTXK_NOT_NULL);
            Objects.requireNonNull(mdc, MAP_NOT_NULL);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(exception);
        }
        
        return context.put(mdcContextKey, mdc);
    }
    
    @NonNull
    public static Context put(Context context, MDC mdc) {
        try {
            Objects.requireNonNull(context, CTXK_NOT_NULL);
            Objects.requireNonNull(mdc, MDC_NOT_NULL);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(exception);
        }
        
        return context.put(mdc.getContextKey(), mdc.getMap());
    }
    
    @NonNull
    public static Mono<MDC> read() {
        return read(DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static Mono<MDC> read(String mdcContextKey) {
        return Mono.deferContextual(ctx -> read(ctx, mdcContextKey));
    }
    
    @NonNull
    public static Mono<MDC> read(ContextView context) {
        return read(context, DEFAULT_REACTOR_CONTEXT_MDC_KEY);
    }
    
    @NonNull
    public static Mono<MDC> read(@NonNull ContextView contextView, @NonNull String mdcContextKey) {
        try {
            Objects.requireNonNull(contextView, CTXW_NOT_NULL);
            Objects.requireNonNull(mdcContextKey, CTXK_NOT_NULL);
        } catch (NullPointerException exception) {
            return Mono.error(new IllegalArgumentException(exception));
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
