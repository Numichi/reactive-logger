package io.github.numichi.reactive.logger;

import io.github.numichi.reactive.logger.annotations.JacocoSkipGeneratedReport;
import io.github.numichi.reactive.logger.exception.AlreadyConfigurationException;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@JacocoSkipGeneratedReport
public class DefaultValues {
    private static final String DEFAULT_REACTOR_CONTEXT_MDC_KEY = "EFAULT_REACTOR_CONTEXT_MDC_KEY";
    private static final Scheduler DEFAULT_SCHEDULER = Schedulers.boundedElastic();
    private static DefaultValues instance = null;
    private final String defaultReactorContextMdcKey;
    private final Scheduler defaultScheduler;
    
    private DefaultValues() {
        this(DEFAULT_REACTOR_CONTEXT_MDC_KEY, DEFAULT_SCHEDULER);
    }
    
    private DefaultValues(Scheduler defaultScheduler) {
        this(DEFAULT_REACTOR_CONTEXT_MDC_KEY, defaultScheduler);
    }
    
    private DefaultValues(String defaultReactorContextMdcKey) {
        this(defaultReactorContextMdcKey, DEFAULT_SCHEDULER);
    }
    
    private DefaultValues(String defaultReactorContextMdcKey, Scheduler defaultScheduler) {
        this.defaultReactorContextMdcKey = defaultReactorContextMdcKey;
        this.defaultScheduler = defaultScheduler;
    }
    
    public static DefaultValues getInstance() {
        if (instance == null) {
            instance = configuration();
        }
        
        return instance;
    }
    
    public static DefaultValues configuration() throws AlreadyConfigurationException {
        if (instance == null) {
            instance = new DefaultValues();
            return instance;
        } else {
            throw new AlreadyConfigurationException();
        }
    }
    
    public static DefaultValues configuration(Scheduler defaultScheduler) throws AlreadyConfigurationException {
        if (instance == null) {
            instance = new DefaultValues(defaultScheduler);
            return instance;
        } else {
            throw new AlreadyConfigurationException();
        }
    }
    
    public static DefaultValues configuration(String defaultReactorContextMdcKey) throws AlreadyConfigurationException {
        if (instance == null) {
            instance = new DefaultValues(defaultReactorContextMdcKey);
            return instance;
        } else {
            throw new AlreadyConfigurationException();
        }
    }
    
    public static DefaultValues configuration(String defaultReactorContextMdcKey, Scheduler defaultScheduler) throws AlreadyConfigurationException {
        if (instance == null) {
            instance = new DefaultValues(defaultReactorContextMdcKey, defaultScheduler);
            return instance;
        } else {
            throw new AlreadyConfigurationException();
        }
    }
    
    public String getDefaultReactorContextMdcKey() {
        return defaultReactorContextMdcKey;
    }
    
    public Scheduler getDefaultScheduler() {
        return defaultScheduler;
    }
}
