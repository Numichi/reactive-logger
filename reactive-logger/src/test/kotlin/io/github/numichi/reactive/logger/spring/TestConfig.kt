package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.Position
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

data class HookMock(val mock: (String?) -> Unit)

@TestConfiguration
open class TestConfig {

    @Bean
    open fun hookMock(): HookMock {
        return HookMock(mockk())
    }

    @Bean
    open fun hook3(mock: HookMock): MDCContextHook {
        return MDCContextHook(Position.BEFORE) { ctx, _ ->
            mock.mock(ctx["before"])
            mapOf("before" to ctx["before"])
        }
    }

    @Bean
    open fun hook4(): MDCContextHook {
        return MDCContextHook { ctx, _ ->
            mapOf("after" to ctx["after"])
        }
    }
}