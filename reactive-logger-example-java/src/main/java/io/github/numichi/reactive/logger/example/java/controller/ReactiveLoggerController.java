package io.github.numichi.reactive.logger.example.java.controller;

import io.github.numichi.reactive.logger.MDC;
import io.github.numichi.reactive.logger.example.java.configuration.LoggerHookConfiguration;
import io.github.numichi.reactive.logger.example.java.filter.UserFilter;
import io.github.numichi.reactive.logger.reactor.MDCContext;
import io.github.numichi.reactive.logger.reactor.ReactiveLogger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("reactive")
public class ReactiveLoggerController {

    private final ReactiveLogger logger = ReactiveLogger.getLogger(ReactiveLoggerController.class);
    private final ReactiveLogger logger1 = ReactiveLogger.getLogger("log-name-1", "context-key-1");
    private final ReactiveLogger logger2 = ReactiveLogger.getLogger("log-name-2", ReactiveLoggerController.class);

    /**
     * <p>The snapshot give MDC information and will be triggered MDC hooks.</p>
     * <p></p>
     *
     * <h3>Response example</h2>
     * <pre>{@code
     * HTTP/1.1 200 OK
     * content-Type: application/json
     * Content-Length: 106
     *
     * {
     *     "userId": "bbb57b94-7aa3-4a65-9631-2cab265cfe56",
     *     "example": "example"
     * }
     * }</pre>
     * <p></p>
     *
     * @see UserFilter#filter(ServerWebExchange, WebFilterChain)
     * @see LoggerHookConfiguration#mdcContextHook()
     */
    @GetMapping("snapshot")
    public Mono<Map<String, String>> getSnapshot() {
        return MDCContext.snapshot().map(MDC::getData);
    }

    /**
     * <p>It's like a snapshot (!) without MDC Hook information.</p>
     * <p>The filter component added "userId", so there is one element of data.</p>
     * <p></p>
     *
     * <h3>Response example</h2>
     * <pre>{@code
     * HTTP/1.1 200 OK
     * content-Type: application/json
     * Content-Length: 106
     *
     * {
     *     "userId": "e7cd6a75-c543-458c-9d19-3df6ba3d67f6"
     * }
     * }</pre>
     *
     * @see UserFilter#filter(ServerWebExchange, WebFilterChain)
     */
    @GetMapping("read")
    public Mono<Map<String, String>> getRead() {
        return MDCContext.read().map(MDC::getData);
    }

    /**
     * <p>Logging by the normal way with MDC information appended from reactor context.</p>
     * <p>The default log context key is the current MDC scope.
     * Due to the <code>LoggerHookConfiguration</code>, the <code>ExampleModel.class</code> specified in the UserFilter gets mapped.
     * Additionally, in the <code>UserFilter</code>, we add <code>"userId"</code> to the default MDC.
     * These will be present in the log.</p>
     * <p></p>
     *
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {"message":"log0-information","context":{"userId":"8d4d003c-eadf-407b-8fdc-712535b72362","example":"example"}}
     * }</pre>
     *
     * @see UserFilter#filter(ServerWebExchange, WebFilterChain)
     * @see LoggerHookConfiguration#mdcContextHook()
     */
    @GetMapping("log0")
    public Mono<Void> doInfo0() {
        return Mono.just("log0-information")
                .flatMap(logger::info);
    }

    /**
     * <p>Logging by the normal way with MDC information appended from reactor context.</p>
     * <p>The current context key is not the default but the configured <code>"context-key-1"</code>, causing a change in the experienced behavior.
     * On the hand, a <code>{"example":"n/a"}</code> has appeared due to a condition in the hook explicitly matching this context key.
     * Additionally, <code>{"foo":"bar"}</code> has been added because of this context key used in MDC modification.
     * The <code>"will-not-appear"</code> is tied to the default log context key, so it does not participate in the current MDC.</p>
     * <p></p>
     *
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {"message":"log1-information","context":{"example":"n/a","foo":"bar"}}
     * }</pre>
     *
     * @see LoggerHookConfiguration#mdcContextHook()
     */
    @GetMapping("log1")
    public Mono<Void> doInfo1() {
        return Mono.just("log1-information")
                .flatMap(logger1::info)
                .contextWrite(context -> MDCContext.modify(context, logger1.getContextKey(), Map.of("foo", "bar")))
                .contextWrite(context -> MDCContext.modify(context, Map.of("will-not-appear", "will-not-appear")));
    }

    /**
     * <p>Logging by the normal way with MDC information appended from reactor context.</p>
     * <p>The current log context key is the <code>ReactiveLoggerController.class</code>.
     * Currently, only <code>{"example":"example"}</code> appears in the context log here.
     * On one hand, <code>"userId"</code> is saved as the default log key, so it does not match the current one,
     * meaning it will not be logged. The Example.class goes into the reactor context and is added in the <code>LoggerHookConfiguration</code>.</p>
     * <p></p>
     *
     * <h3>JSON Log format</h3>
     * <pre>{@code
     * {"message":"log2-information","context":{"example":"example"}}
     * }</pre>
     *
     * @see UserFilter#filter(ServerWebExchange, WebFilterChain)
     * @see LoggerHookConfiguration#mdcContextHook()
     */
    @GetMapping("log2")
    public Mono<Void> doInfo2() {
        return Mono.just("log2-information")
                .flatMap(logger2::info);
    }
}
