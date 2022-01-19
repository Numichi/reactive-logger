package hu.numichi.reactive.logger;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Consts {
    public static final String KEY_NOT_NULL = "key must not be null";
    public static final String VALUE_NOT_NULL = "value must not be null";
    public static final String CTXK_NOT_NULL = "mdcContextKey must not be null";
    public static final String CTXW_NOT_NULL = "contextView must not be null";
    public static final String MDC_NOT_NULL = "mdc must not be null";
    public static final String MAP_NOT_NULL = "map must not be null";
    
    public static final String DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY";
    public static final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();
    
    private Consts() {}
}
