package io.github.numichi.reactive.logger.example.java.configuration;

import io.github.numichi.reactive.logger.example.java.model.ExampleModel;
import io.github.numichi.reactive.logger.hook.MDCContextHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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

            // it will be removed because key is null
            result.put(null, null);

            if (mdc.getContextKey().equals("context-key-1")) {
                result.put("example", "n/a"); // it will overwrite the example value (22nd line)
            }

            return result;
        });
    }
}
