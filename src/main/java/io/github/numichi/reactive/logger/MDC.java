package io.github.numichi.reactive.logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MDC extends ConcurrentHashMap<String, String> {
    private final String assignedContextKey;
    
    public MDC() {
        this(null, null);
    }
    
    public MDC(String assignedContextKey) {
        this(assignedContextKey, null);
    }
    
    public MDC(Map<String, String> mdcMap) {
        this(null, mdcMap);
    }
    
    public MDC(String assignedContextKey, Map<String, String> mdcMap) {
        if (mdcMap != null) {
            this.putAll(mdcMap);
        }
    
        if (assignedContextKey != null) {
            this.assignedContextKey = assignedContextKey;
        } else {
            this.assignedContextKey = DefaultValues.getInstance().getDefaultReactorContextMdcKey();
        }
    }
    
    public String getAssignedContextKey() {
        return assignedContextKey;
    }
    
    @Deprecated
    public Map<String, String> asMap() {
        return this;
    }
    
    @Deprecated
    public String getContextKey() {
        return assignedContextKey;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MDC)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MDC mdc = (MDC) o;
        return getAssignedContextKey().equals(mdc.getAssignedContextKey());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAssignedContextKey());
    }
}