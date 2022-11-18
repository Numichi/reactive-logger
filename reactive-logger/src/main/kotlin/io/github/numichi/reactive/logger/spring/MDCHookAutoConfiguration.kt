package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.exceptions.HookNameAlreadyExistException
import io.github.numichi.reactive.logger.hook.MDCHook
import io.github.numichi.reactive.logger.hook.MDCHookCache
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean

@AutoConfiguration(after = [DefaultValuesAutoConfiguration::class])
@ConditionalOnBean(value = [MDCHook::class])
open class MDCHookAutoConfiguration(mdcHooks: List<MDCHook<*>>) {
    init {
        val nameCheck = mutableSetOf<String>()

        mdcHooks.sortedBy { it.order }.forEach {
            if (nameCheck.contains(it.name)) {
                throw HookNameAlreadyExistException(it)
            }

            nameCheck.add(it.name)

            MDCHookCache.addHook(it)
        }
    }
}