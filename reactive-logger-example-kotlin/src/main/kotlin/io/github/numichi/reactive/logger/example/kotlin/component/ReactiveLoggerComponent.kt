package io.github.numichi.reactive.logger.example.kotlin.component

import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class ReactiveLoggerComponent(factory: LoggerRegistry) {

    val reactiveLoggerFromRegistry = factory.makeReactiveLogger("example-instance", this::class.java)

    fun almafa(): Mono<String> {
        return Mono.just(UUID.randomUUID().toString())
            .flatMap { reactiveLoggerFromRegistry.info(it).thenReturn(it) }
            .doOnEach(reactiveLoggerFromRegistry.logOnEach { logger, signal ->
                if (signal.isOnNext) logger.info(signal.get())
            })
    }

}