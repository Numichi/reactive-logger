package io.github.numichi.reactive.logger

import reactor.util.function.Tuple2
import kotlin.jvm.Throws

data class MDC(
    val contextKey: Any,
    val data: Map<String, String?>,
) {
    val size: Int
        get() = data.size

    constructor() : this(Configuration.defaultReactorContextMdcKey, mapOf())
    constructor(mdcMap: Map<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mdcMap)
    constructor(mdcPair: Pair<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mapOf(mdcPair))
    constructor(mdcTuple: Tuple2<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mapOf(mdcTuple.t1 to mdcTuple.t2))
    constructor(contextKey: Any) : this(contextKey, mapOf())
    constructor(contextKey: Any, mdcPair: Pair<String, String?>) : this(contextKey, mapOf(mdcPair))
    constructor(contextKey: Any, mdcTuple: Tuple2<String, String?>) : this(contextKey, mapOf(mdcTuple.t1 to mdcTuple.t2))
    constructor(mdc: MDC) : this(mdc.contextKey, mdc.data)

    @Throws(IllegalStateException::class)
    private fun checkContextKey(mdc: MDC) {
        check(contextKey == mdc.contextKey) { "The got MDC context key is different from the current one." }
    }

    /**
     * Get one of the MDC values by key.
     */
    operator fun get(key: String): String? {
        return data[key]
    }

    /**
     * Add them value into MDC.
     *
     * @throws IllegalStateException If two MDC context key values are different.
     */
    @Throws(IllegalStateException::class)
    operator fun plus(mdc: MDC): MDC {
        checkContextKey(mdc)
        return MDC(contextKey, this.data.plus(mdc.data))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(map: Map<String, String?>): MDC {
        return MDC(contextKey, data.plus(map))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(map: Pair<String, String?>): MDC {
        return MDC(contextKey, data.plus(map))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(map: Tuple2<String, String?>): MDC {
        return MDC(contextKey, data.plus(map.t1 to map.t2))
    }

    /**
     * Releasing the listed keys. Result MDC will not contain them.
     */
    operator fun minus(key: String): MDC {
        return MDC(contextKey, data.filterKeys { key != it })
    }

    /**
     * Releasing the listed keys. Result MDC will not contain them.
     */
    operator fun minus(keys: Collection<String>): MDC {
        return MDC(contextKey, data.filterKeys { !keys.contains(it) })
    }

    /**
     * Releasing the listed keys. Result MDC will not contain them.
     */
    operator fun minus(keys: Array<String>): MDC {
        return MDC(contextKey, data.filterKeys { !keys.contains(it) })
    }

    /**
     * Releasing the listed keys. Result MDC will not contain them.
     *
     * @throws IllegalStateException If two MDC context key values are different.
     */
    @Throws(IllegalStateException::class)
    operator fun minus(mdc: MDC): MDC {
        checkContextKey(mdc)
        return MDC(contextKey, data.filterKeys { !mdc.data.keys.contains(it) })
    }

    /**
     * Keeps the specified keys and discards the rest.
     */
    fun keep(key: String): MDC {
        val value = data[key]
        return if (value == null) MDC(contextKey) else MDC(contextKey, mapOf(key to value))
    }

    /**
     * Keeps the specified keys and discards the rest.
     */
    fun keep(keys: Collection<String>): MDC {
        return MDC(contextKey, data.filterKeys { keys.contains(it) })
    }

    /**
     * Keeps the specified keys and discards the rest.
     */
    fun keep(keys: Array<String>): MDC {
        keys.iterator()
        return MDC(contextKey, data.filterKeys { keys.contains(it) })
    }

    /**
     * Keeps the specified keys and discards the rest.
     *
     * @throws IllegalStateException If two MDC context key values are different.
     */
    @Throws(IllegalStateException::class)
    fun keep(mdc: MDC): MDC {
        checkContextKey(mdc)
        return MDC(contextKey, data.filterKeys { mdc.data.contains(it) })
    }

    /**
     * Create an empty MDC with the current context key.
     */
    fun clean() = MDC(contextKey, mapOf())
}