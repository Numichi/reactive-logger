package hu.numichi.reactive.logger;

import reactor.util.annotation.NonNull;
import reactor.util.context.Context;

import java.util.Map;

public final class MDCContextWriter {
    private MDCContextWriter() {
    }
    
    @NonNull
    public static Context put(
        @NonNull Context context,
        @NonNull Map<String, String> mdc
    ) {
        return context.put(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }
    
    @NonNull
    public static Context put(
        @NonNull Context context,
        @NonNull String mdcContextKey,
        @NonNull Map<String, String> mdc
    ) {
        return context.put(mdcContextKey, mdc);
    }
    
    @NonNull
    public static Context put(
        @NonNull Context context,
        @NonNull MDC mdc
    ) {
        return context.put(mdc.getMdcContextKey(), mdc.getMdcMap());
    }
}
