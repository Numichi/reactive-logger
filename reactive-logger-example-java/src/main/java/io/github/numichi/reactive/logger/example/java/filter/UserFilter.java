package io.github.numichi.reactive.logger.example.java.filter;

import io.github.numichi.reactive.logger.example.java.model.ExampleModel;
import io.github.numichi.reactive.logger.reactor.MDCContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
public class UserFilter implements WebFilter {
    
    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(context -> MDCContext.modify(context, mdc -> mdc.plus(Map.of("userId", UUID.randomUUID().toString()))))
            .contextWrite(context -> context.put(ExampleModel.class, new ExampleModel("example")));
    }
}
