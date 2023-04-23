package io.github.numichi.reactive.logger.example.java.configuration;

import io.github.numichi.reactive.logger.example.java.model.ExampleModel;
import io.github.numichi.reactive.logger.hook.MDCContextHook;
import io.github.numichi.reactive.logger.hook.MDCHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class LoggerHookConfiguration {

    @Bean
    public MDCContextHook mdcContextHook() {
        return new MDCContextHook((ctx, mdc) -> {
            Map<String, String> result = new HashMap<>();

            var model = ctx.getOrDefault(ExampleModel.class, null);

            if (model instanceof ExampleModel exampleModel) {
                result.put("example", exampleModel.value());
            }

            result.put(null, null); // it will be removed because key is null

            if (mdc.getContextKey().equals("another-context-key-from-yml")) {
                return Map.of("example", "n/a");
            }

            return result;
        });
    }

    // @Bean
    @Deprecated
    public MDCHook<ExampleModel> traceContextHook() {
        return new MDCHook<>("ExampleModel_1", ExampleModel.class, (model, mdc) -> {
            Objects.requireNonNull(model);

            Map<String, String> result = new HashMap<>();
            result.put("example", model.value());
            result.put(null, null); // it will be removed because key is null

            return result;
        });
    }

    // @Bean
    @Deprecated
    public MDCHook<ExampleModel> anotherTraceContextHook() {
        return new MDCHook<>("ExampleModel_2", ExampleModel.class, (model, mdc) -> {
            Objects.requireNonNull(model);

            if (!mdc.getContextKey().equals("another-context-key-from-yml")) {
                throw new IllegalStateException("key must be \"another-context-key-from-yml\"!");
            }

            return Map.of("example", "n/a");
        });
    }
}
