package io.github.numichi.reactive.logger.reactor;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.DefaultValues;
import io.github.numichi.reactive.logger.exception.InvalidContextDataException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import static io.github.numichi.reactive.logger.exception.Messages.CTXK_NOT_NULL;
import static io.github.numichi.reactive.logger.exception.Messages.CTXW_NOT_NULL;
import static io.github.numichi.reactive.logger.exception.Messages.MDC_NOT_NULL;

public final class MDCContext {
    private MDCContext() {
    }
    
    @NonNull
    public static Context put(Context context, MDC... mdc) {
        try {
            Objects.requireNonNull(context, CTXK_NOT_NULL);
            Objects.requireNonNull(mdc, MDC_NOT_NULL);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException(exception);
        }
        
        for (MDC m : mdc) {
            if (m == null) {
                continue;
            }
            
            context = context.put(m.getAssignedContextKey(), m);
        }
        
        return context;
    }
    
    @NonNull
    public static Mono<MDC> read() {
        return read(DefaultValues.getInstance().getDefaultReactorContextMdcKey());
    }
    
    @NonNull
    public static Mono<MDC> read(String mdcContextKey) {
        return Mono.deferContextual(ctx -> read(ctx, mdcContextKey));
    }
    
    @NonNull
    public static Mono<MDC> read(ContextView context) {
        return read(context, DefaultValues.getInstance().getDefaultReactorContextMdcKey());
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
