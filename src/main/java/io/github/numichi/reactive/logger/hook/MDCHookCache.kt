package io.github.numichi.reactive.logger.hook

internal object MDCHookCache {
    var listBefore: Array<MDCHook<*>> = arrayOf()
    var listAfter: Array<MDCHook<*>> = arrayOf()
    private val storeHooks: MutableMap<String, MDCHook<*>> = mutableMapOf()

    private fun initCache() {
        listBefore = storeHooks.values.filter { it.order < 0 }.sortedBy { it.order }.toTypedArray()
        listAfter = storeHooks.values.filter { 0 <= it.order }.sortedBy { it.order }.toTypedArray()
    }

    fun clear() {
        storeHooks.clear()
        listBefore = arrayOf()
        listAfter = arrayOf()
    }

    fun getHooks(): Map<String, MDCHook<*>> {
        return storeHooks
    }

    fun existsHook(key: String): Boolean {
        return storeHooks.containsKey(key)
    }

    fun addHook(key: String, hook: MDCHook<*>) {
        storeHooks[key] = hook
        initCache()
    }

    fun removeHook(key: String) {
        storeHooks.remove(key)
        initCache()
    }
}