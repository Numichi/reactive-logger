package io.github.numichi.reactive.logger.internal

import io.github.numichi.reactive.logger.KEY_MUST_NOT_BE_NULL
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

internal inline fun <reified K, reified V> Map<K, V>.toSafeMdcMap(): Map<String, String?> {
    return this.filter { it.key != null }
        .mapKeys { it.key.toString() }
        .mapValues { it.value?.toString() }
}

internal inline fun <reified K, reified V> Tuple2<K, V>.toSafeMdcTuple2(): Tuple2<String, String?> {
    requireNotNull(this.t1) { KEY_MUST_NOT_BE_NULL }

    return Tuples.of(this.t1.toString(), this.t2.toString())
}

internal inline fun <reified K, reified V> Tuple2<K, V>.toSafeMdcMap(): Map<String, String?> {
    val safe = this.toSafeMdcTuple2()
    return mapOf(safe.t1 to safe.t2)
}

internal inline fun <reified K, reified V> Pair<K, V>.toSafeMdcPair(): Pair<String, String?> {
    requireNotNull(this.first) { KEY_MUST_NOT_BE_NULL }

    if (this.second == null) {
        return this.first.toString() to null
    }

    return this.first.toString() to this.second.toString()
}

internal inline fun <reified K, reified V> Pair<K, V>.toSafeMdcMap(): Map<String, String?> {
    return mapOf(this.toSafeMdcPair())
}
