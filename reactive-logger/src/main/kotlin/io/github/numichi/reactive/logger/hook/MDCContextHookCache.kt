package io.github.numichi.reactive.logger.hook

enum class Position {
    BEFORE,
    AFTER
}

internal object MDCContextHookCache {
    private val store = mutableMapOf<Position, MDCContextHook>()

    fun clear() {
        store.clear()
    }

    fun getHooks(): Map<Position, MDCContextHook> {
        return store
    }

    fun existsHook(position: Position): Boolean {
        return store.containsKey(position)
    }

    fun addHook(hook: MDCContextHook) {
        if (hook.position == Position.BEFORE) {
            store[Position.BEFORE] = hook
        } else {
            store[Position.AFTER] = hook
        }
    }

    fun removeHook(position: Position) {
        store.remove(position)
    }
}