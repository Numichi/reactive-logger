package io.github.numichi.reactive.logger

internal fun Map<Any?, Any?>.toStringStringMap(): Map<String, String?> {
    return this.filter { it.key != null }
        .mapKeys { it.key.toString() }
        .mapValues { it.value?.toString() }
}

@Suppress("SENSELESS_COMPARISON")
@JvmName("toStringStringMapStringString?")
internal fun Map<String, String?>.toStringStringMap(): Map<String, String?> {
    return this.filter { it.key != null }
        .mapKeys { (it.key as Any).toString() }
}