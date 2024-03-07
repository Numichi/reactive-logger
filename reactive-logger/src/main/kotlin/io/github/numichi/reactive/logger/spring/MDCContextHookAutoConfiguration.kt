package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.exceptions.ContextHookNameAlreadyExistException
import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.MDCContextHookCache
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@AutoConfiguration(after = [DefaultValuesAutoConfiguration::class])
@ConditionalOnBean(value = [MDCContextHook::class])
open class MDCContextHookAutoConfiguration(mdcContextHooks: List<MDCContextHook>) {
    init {
        mdcContextHooks.forEach {
            if (MDCContextHookCache.existsHook(it.position)) {
                throw ContextHookNameAlreadyExistException("MDCContextHook in ${it.position.name} position already exist!")
            }

            MDCContextHookCache.addHook(it)
        }
    }
}