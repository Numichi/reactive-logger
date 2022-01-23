package hu.numichi.reactive.logger;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Values {
    private Values() {}
    
    public static final String DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY";
    public static final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();
}
