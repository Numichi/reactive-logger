package io.github.numichi.reactive.logger.example.java;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.reactor.MDCContext;
import io.github.numichi.reactive.logger.reactor.ReactiveLogger;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ExampleComponent {
    
    private final Map<String, String> map = new HashMap<>();
    private final ReactiveLogger reactiveLogger;
    
    public ExampleComponent(Logger logger) {
        this.reactiveLogger = ReactiveLogger.getLogger(logger);
        map.put("foo", "bar");
        map.put(null, null);
    }
    
    public Mono<Map<String, String>> example1() {
        return Mono.just("example1")
            .flatMap(data -> reactiveLogger.info(data).thenReturn(data))
            .flatMap(data -> MDCContext.snapshot())
            .map(MDC::getData)
            .contextWrite(context -> MDCContext.merge(context, map));
    }
    
    public Mono<Void> example2() {
        return MDCContext.read()
            .doOnEach(reactiveLogger.logOnEach((Logger logger, Signal<MDC> signal) -> {
                String foo = getFooValue(signal);
                
                // Ofc, you can replace foo by be any message
                if (signal.isOnSubscribe()) logger.trace(foo);
                if (signal.isOnNext()) logger.debug(foo);
                if (signal.isOnComplete()) logger.info(foo);
                if (signal.isOnError()) logger.warn(foo);
            }))
            .contextWrite(context -> MDCContext.merge(context, map))
            .then();
    }
    
    public Mono<Void> example3() {
        return MDCContext.read()
            .doOnEach(signal -> reactiveLogger.logOnSignal(signal, (Logger logger) -> {
                String foo = getFooValue(signal);
                
                // Ofc, you can replace foo by be any message
                if (signal.isOnSubscribe()) logger.trace(foo);
                if (signal.isOnNext()) logger.debug(foo);
                if (signal.isOnComplete()) logger.info(foo);
                if (signal.isOnError()) logger.warn(foo);
            }))
            .contextWrite(context -> MDCContext.merge(context, map))
            .then();
    }
    
    public Mono<MDC> example4() {
        return MDCContext.read()
            .doOnEach(reactiveLogger.logOnEach((Logger logger, Signal<MDC> signal) -> {
                if (signal.isOnError()) {
                    logger.error(signal.getThrowable().getMessage());
                }
            }));
    }
    
    private String getFooValue(@NonNull Signal<MDC> signal)  {
        var mdc = signal.get();
        String foo = "null";
        
        if (mdc != null) {
            foo = mdc.getData().get("foo");
        }
        
        return foo;
    }
}