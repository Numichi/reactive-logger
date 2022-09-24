package io.github.numichi.reactive.logger.configuration

import io.github.numichi.reactive.logger.exceptions.HookNameAlreadyExistException
import io.github.numichi.reactive.logger.hook.MDCHook
import io.github.numichi.reactive.logger.hook.MDCHookCache
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConditionalOnBean(value = [MDCHook::class])
open class MDCHookAutoConfiguration(private val mdcHooks: List<MDCHook<*>>) {

    @PostConstruct
    fun postConstruct() {
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