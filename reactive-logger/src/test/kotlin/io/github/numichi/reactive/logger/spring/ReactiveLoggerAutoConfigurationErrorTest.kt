package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.exceptions.ContextHookNameAlreadyExistException
import io.github.numichi.reactive.logger.exceptions.HookNameAlreadyExistException
import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.MDCHook
import io.github.numichi.reactive.logger.hook.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReactiveLoggerAutoConfigurationErrorTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun afterEach() {
            Configuration.reset()
        }
    }

    @Test
    fun autoConfigurationError() {
        run {
            val error = assertThrows<HookNameAlreadyExistException> {
                val list = mutableListOf<MDCHook<*>>()
                list.add(MDCHook<String>("hook0", "", 1) { _, _ -> mapOf() })
                list.add(MDCHook<String>("hook0", "", 0) { _, _ -> mapOf() })

                MDCHookAutoConfiguration(list)
            }

            assertEquals("The name \"hook0\" already exists. Order=1 ContextKey=", error.message)
        }

        run {
            val error = assertThrows<ContextHookNameAlreadyExistException> {
                val list = mutableListOf<MDCContextHook>()
                list.add(MDCContextHook(Position.BEFORE) { _, _ -> mapOf() })
                list.add(MDCContextHook(Position.BEFORE) { _, _ -> mapOf() })

                MDCContextHookAutoConfiguration(list)
            }

            assertEquals("MDCContextHook in BEFORE position already exist!", error.message)
        }
    }
}