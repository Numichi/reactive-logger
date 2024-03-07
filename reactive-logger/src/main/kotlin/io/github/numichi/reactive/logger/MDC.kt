package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.internal.toSafeMdcMap
import io.github.numichi.reactive.logger.internal.toSafeMdcPair
import reactor.util.function.Tuple2

class MDC(val contextKey: Any, data: Map<String, String?>) {
    val data: Map<String, String?> = data.toSafeMdcMap()

    constructor() : this(Configuration.defaultReactorContextMdcKey, mapOf())
    constructor(mdcMap: Map<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mdcMap.toSafeMdcMap())
    constructor(mdcPair: Pair<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mdcPair.toSafeMdcMap())
    constructor(mdcTuple: Tuple2<String, String?>) : this(Configuration.defaultReactorContextMdcKey, mdcTuple.toSafeMdcMap())
    constructor(mdcArrayTuple: Array<Tuple2<String, String?>>) : this(Configuration.defaultReactorContextMdcKey, toMap(mdcArrayTuple))
    constructor(mdcArrayTuple: Array<Pair<String, String?>>) : this(Configuration.defaultReactorContextMdcKey, toMap(mdcArrayTuple))
    constructor(contextKey: Any) : this(contextKey, mapOf())
    constructor(contextKey: Any, mdcPair: Pair<String, String?>) : this(contextKey, mdcPair.toSafeMdcMap())
    constructor(contextKey: Any, mdcTuple: Tuple2<String, String?>) : this(contextKey, mdcTuple.toSafeMdcMap())
    constructor(contextKey: Any, mdcArrayTuple: Array<Tuple2<String, String?>>) : this(contextKey, toMap(mdcArrayTuple))
    constructor(contextKey: Any, mdcArrayTuple: Array<Pair<String, String?>>) : this(contextKey, toMap(mdcArrayTuple))
    constructor(mdc: MDC) : this(mdc.contextKey, mdc.data)

    companion object {
        @JvmStatic
        private fun toMap(array: Array<Pair<String, String?>>): Map<String, String?> {
            return array.map { it.toSafeMdcMap() }.reduce { acc, map -> acc.plus(map) }
        }

        @JvmStatic
        private fun toMap(array: Array<Tuple2<String, String?>>): Map<String, String?> {
            return array.map { it.toSafeMdcMap() }.reduce { acc, map -> acc.plus(map) }
        }
    }

    val size: Int
        get() = data.size

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
        return MDC(contextKey, data.plus(map.toSafeMdcMap()))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(pair: Pair<String, String?>): MDC {
        return MDC(contextKey, data.plus(pair.toSafeMdcPair()))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(array: Array<Pair<String, String?>>): MDC {
        return MDC(contextKey, data.plus(toMap(array)))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(tuple2: Tuple2<String, String?>): MDC {
        return MDC(contextKey, data.plus(tuple2.toSafeMdcMap()))
    }

    /**
     * Add them value into MDC.
     */
    operator fun plus(array: Array<Tuple2<String, String?>>): MDC {
        return MDC(contextKey, data.plus(toMap(array)))
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MDC) return false
        if (contextKey != other.contextKey) return false
        if (data != other.data) return false
        return true
    }

    override fun hashCode(): Int {
        var result = contextKey.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }

    override fun toString(): String {
        return "MDC(contextKey=$contextKey, data=$data)"
    }
}
