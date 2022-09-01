package io.github.numichi.reactive.logger.hook

internal object MDCHookCache {
    var initialized: Boolean = false
    var listBefore: List<MDCHook<*>> = listOf()
    var listAfter: List<MDCHook<*>> = listOf()
    private val storeHooks: MutableMap<String, MDCHook<*>> = mutableMapOf()

    fun initCache() {
        if (!initialized) {
            listBefore = storeHooks.values.filter { it.order < 0 }.sortedBy { it.order }
            listAfter = storeHooks.values.filter { 0 <= it.order }.sortedBy { it.order }
            initialized = true
        }
    }

    fun clear() {
        storeHooks.clear()
        listBefore = listOf()
        listAfter = listOf()
    }

    fun getHooks(): Map<String, MDCHook<*>> {
        return storeHooks
    }

    fun existsHook(key: String): Boolean {
        return storeHooks.containsKey(key)
    }

    fun addHook(key: String, hook: MDCHook<*>) {
        storeHooks[key] = hook
        initialized = false
    }

    fun removeHook(key: String) {
        storeHooks.remove(key)
        initialized = false
    }
}