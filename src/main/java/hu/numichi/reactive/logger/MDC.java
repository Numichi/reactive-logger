package hu.numichi.reactive.logger;

import hu.numichi.reactive.logger.annotations.JacocoSkipGeneratedReport;
import reactor.util.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static hu.numichi.reactive.logger.Values.DEFAULT_REACTOR_CONTEXT_MDC_KEY;
import static hu.numichi.reactive.logger.exception.Messages.KEY_NOT_NULL;
import static hu.numichi.reactive.logger.exception.Messages.MAP_NOT_NULL;
import static hu.numichi.reactive.logger.exception.Messages.VALUE_NOT_NULL;

@JacocoSkipGeneratedReport
public class MDC implements Map<String, String> {
    private final Map<String, String> mdcMap;
    private final String mdcContextKey;
    
    public MDC() {
        this(DEFAULT_REACTOR_CONTEXT_MDC_KEY, new HashMap<>());
    }
    
    public MDC(String mdcContextKey) {
        this(mdcContextKey, new HashMap<>());
    }
    
    public MDC(Map<String, String> mdc) {
        this(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }
    
    public MDC(String mdcContextKey, Map<String, String> mdc) {
        this.mdcMap = mdc;
        this.mdcContextKey = mdcContextKey;
    }
    
    public Map<String, String> asMap() {
        return mdcMap;
    }
    
    public String getContextKey() {
        return mdcContextKey;
    }
    
    @Override
    public int size() {
        return mdcMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return mdcMap.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return mdcMap.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        return mdcMap.containsValue(value);
    }
    
    @Override
    public String get(Object key) {
        return mdcMap.get(key);
    }
    
    @Override
    public String put(@NonNull String key, @NonNull String value) {
        Objects.requireNonNull(key, KEY_NOT_NULL);
        Objects.requireNonNull(value, VALUE_NOT_NULL);
        
        return mdcMap.put(key, value);
    }
    
    @Override
    public String remove(Object key) {
        return mdcMap.remove(key);
    }
    
    @Override
    public void putAll(@NonNull Map<? extends String, ? extends String> map) {
        if (map.containsKey(null) || map.containsValue(null)) {
            throw new NullPointerException(MAP_NOT_NULL);
        }
        
        mdcMap.putAll(map);
    }
    
    @Override
    public void clear() {
        mdcMap.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return mdcMap.keySet();
    }
    
    @Override
    public Collection<String> values() {
        return mdcMap.values();
    }
    
    @Override
    public Set<Entry<String, String>> entrySet() {
        return mdcMap.entrySet();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof MDC)) {
            return false;
        }
        
        MDC mdc = (MDC) o;
        return mdcMap.equals(mdc.mdcMap) && mdcContextKey.equals(mdc.mdcContextKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mdcMap, mdcContextKey);
    }
}