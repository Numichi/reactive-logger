package io.github.numichi.reactive.logger.example.java;

import java.util.Map;

public class LogModel {
    private String message;
    private Map<String, String> context;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Map<String, String> getContext() {
        return context;
    }
    
    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
