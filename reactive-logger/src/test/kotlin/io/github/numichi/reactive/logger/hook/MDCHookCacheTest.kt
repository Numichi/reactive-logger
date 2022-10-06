package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.Configuration
import org.junit.jupiter.api.BeforeEach

internal class MDCHookCacheTest {

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
    }
}