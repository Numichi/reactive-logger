package io.github.numichi.reactive.logger.example.java.controller;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.reactor.MDCContext;
import io.github.numichi.reactive.logger.reactor.ReactiveLogger;
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("reactive")
public class ReactiveLoggerController {
    
    /**
     * <h3>Manual like</h3>
     * <pre>{@code
     * ReactiveLogger.getLogger("io.github.numichi.reactive.logger.example.controllers.ReactiveLoggerController", "context-key-from-yml")
     * }</pre>
     *
     * <p>"example-instance" from application.yml</p>
     */
    private final ReactiveLogger logger1;
    
    /**
     * <h3>Manual like</h2>
     * <pre>{@code
     * ReactiveLogger.getLogger(ReactiveLoggerController::class.java, "another-context-key-from-yml", Schedulers.parallel())
     * }</pre>
     *
     * <p>"another-example-instance" from application.yml</p>
     */
    private final ReactiveLogger logger2;
    
    /**
     * Manual logger creation
     */
    private final ReactiveLogger logger = ReactiveLogger.getLogger(ReactiveLoggerController.class);
    
    public ReactiveLoggerController(LoggerRegistry loggerRegistry) {
        this.logger1 = loggerRegistry.makeReactiveLogger("example-instance");
        this.logger2 = loggerRegistry.makeReactiveLogger("another-example-instance", ReactiveLoggerController.class);
    }
    
    /**
     * <h3>Response example</h2>
     * <pre>{@code
     * HTTP/1.1 200 OK
     * content-Type: application/json
     * Content-Length: 106
     *
     * {
     *   "userId": "c8780d8a-0e59-4b33-b4f3-ff026d102b3c",
     *   "traceId": "d3cc02d38cc222ad",
     *   "spanId": "d3cc02d38cc222ad"
     * }
     * }</pre>
     * <p></p>
     *
     * <h3>Description</h2>
     * <p>The snapshot give MDC information and will be triggered MDC hooks.</p>
     *
     * @see io.github.numichi.reactive.logger.example.java.filter.UserFilter#filter(org.springframework.web.server.ServerWebExchange, org.springframework.web.server.WebFilterChain)
     */
    @GetMapping("snapshot")
    public Mono<Map<String, String>> getSnapshot() {
        return MDCContext.snapshot().map(MDC::getData);
    }
    
    /**
     * <h3>Response example</h2>
     * <pre>{@code
     * HTTP/1.1 200 OK
     * content-Type: application/json
     * Content-Length: 106
     *
     * {
     *   "userId": "4cab2469-fbcc-40dc-91a8-88f4d553f1eb"
     * }
     * }</pre>
     * <p></p>
     *
     * <h3>Description</h2>
     * <p>It's like a snapshot, except for MDC Hook information. The filter component added "userId", so there is one element of data.</p>
     *
     * @see io.github.numichi.reactive.logger.example.java.filter.UserFilter#filter(org.springframework.web.server.ServerWebExchange, org.springframework.web.server.WebFilterChain)
     */
    @GetMapping("read")
    public Mono<Map<String, String>> getRead() {
        return MDCContext.read().map(MDC::getData);
    }
    
    /**
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {
     *   "message": "log1-information",
     *   "context": {
     *     "spanId": "ad864e3400b43977",
     *     "traceId": "ad864e3400b43977"
     *     "userId": "4cfd73ad-3f32-47f5-a2f4-4a2b54339d1c"
     *   }
     * }</pre>
     */
    @GetMapping("log0")
    public Mono<Void> doInfo0() {
        return Mono.just("log0-information")
            .flatMap(logger::info);
    }
    
    /**
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {
     *   "message": "log1-information",
     *   "context": {
     *     "spanId": "f1f351a78bac867a",
     *     "traceId": "f1f351a78bac867a"
     *     "parentId": null,
     *     "foo": "bar"
     *   }
     * }
     * }</pre>
     * <p></p>
     *
     * <h3>Description</h3>
     *
     * <p>Logging by the normal way with MDC information appended from reactor context.</p>
     * <p>You can see, "userId" isn't shown here. It attached to default context key (DEFAULT_REACTOR_CONTEXT_MDC_KEY) in filter,
     * not the current context key (context-key-from-yml).
     * The hook will activate for all context keys, so there is "spanId" and "traceId".</p>
     *
     * @see io.github.numichi.reactive.logger.example.java.configuration.LoggerHookConfiguration#traceContextHook()
     * @see io.github.numichi.reactive.logger.example.java.ExamplePlugin
     */
    @GetMapping("log1")
    public Mono<Void> doInfo1() {
        return Mono.just("log1-information")
            .flatMap(logger1::info)
            .contextWrite(context -> MDCContext.merge(context, logger1.getContextKey(), Map.of("foo", "bar")))
            .contextWrite(context -> MDCContext.merge(context, Map.of("will-not-appear", "will-not-appear")));
    }
    
    /**
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {
     *   "message": "log2-information",
     *   "context": {
     *     "spanId": "14843eb53ff7b259",
     *     "traceId": "14843eb53ff7b259",
     *     "parentId": null,
     *     "TraceContext.hashCode": "-697239192"
     *   }
     * }
     * }</pre>
     * <p></p>
     *
     * <h3>Description</h3>
     * <p>
     * Like previous method {@link io.github.numichi.reactive.logger.example.java.controller.ReactiveLoggerController#doInfo1()}, just it used "another-context-key-from-yml" context key.
     * With this key, it will enable the other hook, which throws an exception for all other keys.
     *
     * @see io.github.numichi.reactive.logger.example.java.configuration.LoggerHookConfiguration#traceContextHook()
     * @see io.github.numichi.reactive.logger.example.java.configuration.LoggerHookConfiguration#anotherTraceContextHook()
     * @see io.github.numichi.reactive.logger.example.java.ExamplePlugin
     */
    @GetMapping("log2")
    public Mono<Void> doInfo2() {
        return Mono.just("log2-information")
            .flatMap(logger2::info);
    }
}
