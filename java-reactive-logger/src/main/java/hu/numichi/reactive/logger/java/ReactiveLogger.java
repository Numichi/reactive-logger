package hu.numichi.reactive.logger.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;
import reactor.util.context.Context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static hu.numichi.reactive.logger.Consts.DEFAULT_SCHEDULER;

public final class ReactiveLogger {
    
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(ReactiveLogger.class);
    private final Scheduler scheduler;
    private final Logger logger;
    private final String mdcContextKey;
    
    private ReactiveLogger(@NonNull final Builder builder) {
        this.scheduler = builder.scheduler;
        this.logger = builder.logger;
        this.mdcContextKey = builder.mdcContextKey;
    }
    
    @NonNull
    public static Builder builder() {
        return new Builder();
    }
    
    public Logger imperative() {
        return logger;
    }
    
    public Scheduler scheduler() {
        return scheduler;
    }
    
    @NonNull
    public Optional<Map<String, String>> readMDC(@NonNull final Context context) {
        return context.getOrEmpty(mdcContextKey);
    }
    
    public String getName() {
        return logger.getName();
    }
    
    //region Trace
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }
    
    @NonNull
    public Mono<Context> trace(@NonNull final String msg) {
        return wrap(() -> logger.trace(msg));
    }
    
    @NonNull
    public Mono<Context> trace(
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.trace(format, arg));
    }
    
    @NonNull
    public Mono<Context> trace(
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.trace(format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> trace(
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.trace(format, arguments));
    }
    
    @NonNull
    public Mono<Context> trace(
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.trace(msg, t));
    }
    
    public boolean isTraceEnabled(final Marker marker) {
        return logger.isTraceEnabled(marker);
    }
    
    @NonNull
    public Mono<Context> trace(
        final Marker marker,
        final String msg
    ) {
        return wrap(() -> logger.trace(marker, msg));
    }
    
    @NonNull
    public Mono<Context> trace(
        final Marker marker,
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.trace(marker, format, arg));
    }
    
    @NonNull
    public Mono<Context> trace(
        final Marker marker,
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.trace(marker, format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> trace(
        final Marker marker,
        final String format,
        final Object... argArray
    ) {
        return wrap(() -> logger.trace(marker, format, argArray));
    }
    
    @NonNull
    public Mono<Context> trace(
        final Marker marker,
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.trace(marker, msg, t));
    }
    //endregion
    
    //region Debug
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
    
    @NonNull
    public Mono<Context> debug(final String msg) {
        return wrap(() -> logger.debug(msg));
    }
    
    @NonNull
    public Mono<Context> debug(
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.debug(format, arg));
    }
    
    @NonNull
    public Mono<Context> debug(
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.debug(format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> debug(
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.debug(format, arguments));
    }
    
    @NonNull
    public Mono<Context> debug(
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.debug(msg, t));
    }
    
    public boolean isDebugEnabled(final Marker marker) {
        return logger.isDebugEnabled(marker);
    }
    
    @NonNull
    public Mono<Context> debug(
        final Marker marker,
        final String msg
    ) {
        return wrap(() -> logger.debug(marker, msg));
    }
    
    @NonNull
    public Mono<Context> debug(
        final Marker marker,
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.debug(marker, format, arg));
    }
    
    @NonNull
    public Mono<Context> debug(
        final Marker marker,
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.debug(marker, format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> debug(
        final Marker marker,
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.debug(marker, format, arguments));
    }
    
    @NonNull
    public Mono<Context> debug(
        final Marker marker,
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.debug(marker, msg, t));
    }
    //endregion
    
    //region Info
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }
    
    @NonNull
    public Mono<Context> info(final String msg) {
        return wrap(() -> logger.info(msg));
    }
    
    @NonNull
    public Mono<Context> info(
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.info(format, arg));
    }
    
    @NonNull
    public Mono<Context> info(
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.info(format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> info(
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.info(format, arguments));
    }
    
    @NonNull
    public Mono<Context> info(
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.info(msg, t));
    }
    
    public boolean isInfoEnabled(final Marker marker) {
        return logger.isInfoEnabled(marker);
    }
    
    @NonNull
    public Mono<Context> info(
        final Marker marker,
        final String msg
    ) {
        return wrap(() -> logger.info(marker, msg));
    }
    
    @NonNull
    public Mono<Context> info(
        final Marker marker,
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.info(marker, format, arg));
    }
    
    @NonNull
    public Mono<Context> info(
        final Marker marker,
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.info(marker, format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> info(
        final Marker marker,
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.info(marker, format, arguments));
    }
    
    @NonNull
    public Mono<Context> info(
        final Marker marker,
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.info(marker, msg, t));
    }
    //endregion
    
    //region Warn
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }
    
    @NonNull
    public Mono<Context> warn(final String msg) {
        return wrap(() -> logger.warn(msg));
    }
    
    @NonNull
    public Mono<Context> warn(
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.warn(format, arg));
    }
    
    @NonNull
    public Mono<Context> warn(
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.warn(format, arguments));
    }
    
    @NonNull
    public Mono<Context> warn(
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.warn(format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> warn(
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.warn(msg, t));
    }
    
    public boolean isWarnEnabled(final Marker marker) {
        return logger.isWarnEnabled(marker);
    }
    
    @NonNull
    public Mono<Context> warn(
        final Marker marker,
        final String msg
    ) {
        return wrap(() -> logger.warn(marker, msg));
    }
    
    @NonNull
    public Mono<Context> warn(
        final Marker marker,
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.warn(marker, format, arg));
    }
    
    @NonNull
    public Mono<Context> warn(
        final Marker marker,
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.warn(marker, format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> warn(
        final Marker marker,
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.warn(marker, format, arguments));
    }
    
    @NonNull
    public Mono<Context> warn(
        final Marker marker,
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.warn(marker, msg, t));
    }
    //endregion
    
    //region Error
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }
    
    @NonNull
    public Mono<Context> error(final String msg) {
        return wrap(() -> logger.error(msg));
    }
    
    @NonNull
    public Mono<Context> error(
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.error(format, arg));
    }
    
    @NonNull
    public Mono<Context> error(
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.error(format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> error(
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.error(format, arguments));
    }
    
    @NonNull
    public Mono<Context> error(
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.error(msg, t));
    }
    
    public boolean isErrorEnabled(final Marker marker) {
        return logger.isErrorEnabled(marker);
    }
    
    @NonNull
    public Mono<Context> error(
        final Marker marker,
        final String msg
    ) {
        return wrap(() -> logger.error(marker, msg));
    }
    
    @NonNull
    public Mono<Context> error(
        final Marker marker,
        final String format,
        final Object arg
    ) {
        return wrap(() -> logger.error(marker, format, arg));
    }
    
    @NonNull
    public Mono<Context> error(
        final Marker marker,
        final String format,
        final Object arg1,
        final Object arg2
    ) {
        return wrap(() -> logger.error(marker, format, arg1, arg2));
    }
    
    @NonNull
    public Mono<Context> error(
        final Marker marker,
        final String format,
        final Object... arguments
    ) {
        return wrap(() -> logger.error(marker, format, arguments));
    }
    
    @NonNull
    public Mono<Context> error(
        final Marker marker,
        final String msg,
        final Throwable t
    ) {
        return wrap(() -> logger.error(marker, msg, t));
    }
    //endregion
    
    private MDCSnapshot takeMDCSnapshot(final Context context) {
        return readMDC(context).map(MDCSnapshot::of).orElseGet(MDCSnapshot::empty);
    }
    
    private Mono<Context> wrap(final Runnable runnable) {
        return Mono.deferContextual(contextView -> {
            Context context = Context.of(contextView);
            
            try (final MDCSnapshot snapshot = takeMDCSnapshot(context)) {
                runnable.run();
            }
            
            return Mono.just(context);
        }).subscribeOn(scheduler);
    }
    
    public static class Builder {
        private Scheduler scheduler = DEFAULT_SCHEDULER;
        private Logger logger = DEFAULT_LOGGER;
        private String mdcContextKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY;
        private static final String LOGGER_MUST_NOT_BE_NULL = "logger must not be null";
        
        private Builder() {
        }
    
        public Builder withLogger(@NonNull final Class<?> logger) {
            this.logger = LoggerFactory.getLogger(Objects.requireNonNull(logger, LOGGER_MUST_NOT_BE_NULL));
            return this;
        }
    
        public Builder withLogger(@NonNull final String logger) {
            this.logger = LoggerFactory.getLogger(Objects.requireNonNull(logger, LOGGER_MUST_NOT_BE_NULL));
            return this;
        }
        
        public Builder withLogger(@NonNull final Logger logger) {
            this.logger = Objects.requireNonNull(logger, LOGGER_MUST_NOT_BE_NULL);
            return this;
        }
        
        public Builder withScheduler(@NonNull final Scheduler scheduler) {
            this.scheduler = Objects.requireNonNull(scheduler, "scheduler must not be null");
            return this;
        }
        
        public Builder withMDCContextKey(@NonNull final String mdcContextKey) {
            if (mdcContextKey.trim().isEmpty()) {
                throw new IllegalArgumentException("MDC context key must not be blank");
            }
            
            this.mdcContextKey = mdcContextKey;
            return this;
        }
        
        public ReactiveLogger build() {
            return new ReactiveLogger(this);
        }
    }
}
