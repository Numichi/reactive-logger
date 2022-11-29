package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.exceptions.ContextHookNameAlreadyExistException
import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.MDCContextHookCache
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@AutoConfiguration(after = [DefaultValuesAutoConfiguration::class])
@ConditionalOnBean(value = [MDCContextHook::class])
open class MDCContextHookAutoConfiguration(mdcHooks: List<MDCContextHook>) {
    init {
        mdcHooks.forEach {
            if (MDCContextHookCache.existsHook(it.beforeSnapshot)) {
                throw ContextHookNameAlreadyExistException("MDCContextHook in ${it.beforeSnapshot.name} position already exist!")
            }

            MDCContextHookCache.addHook(it)
        }
    }
}