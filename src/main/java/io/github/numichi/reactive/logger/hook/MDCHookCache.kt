package io.github.numichi.reactive.logger.hook

import kotlin.properties.Delegates

internal object MDCHookCache {
    var initialized: Boolean = false
    var listBefore: List<MDCHook<*>> by Delegates.notNull()
    var listAfter: List<MDCHook<*>> by Delegates.notNull()

    fun initCache(mdcHookList: List<MDCHook<*>>) {
        if (!initialized) {
            listBefore = mdcHookList.filter { it.order < 0 }.sortedBy { it.order }
            listAfter = mdcHookList.filter { 0 <= it.order }.sortedBy { it.order }
            initialized = true
        }
    }
}