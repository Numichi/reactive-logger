package io.github.numichi.reactive.logger

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KMarkerFactory
import io.github.oshai.kotlinlogging.slf4j.internal.Slf4jLogger
import io.github.oshai.kotlinlogging.slf4j.toKLogger
import io.github.oshai.kotlinlogging.slf4j.toSlf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.event.Level
import io.github.oshai.kotlinlogging.Level as KLevel
import io.github.oshai.kotlinlogging.Marker as KMarker

fun KMarker.toSlf4j(): Marker = this.toSlf4j()

fun KLogger.toSlf4j(): Logger {
    if (this is Slf4jLogger<*>) {
        return this.underlyingLogger
    }

    return LoggerFactory.getLogger(this.name)
}

fun KLevel.toSlf4j(): Level = this.toSlf4j()

fun Marker.toKMarker(): KMarker = KMarkerFactory.getMarker(this.name)

fun Logger.toKLogger(): KLogger = this.toKLogger()

fun Level.toKLevel(off: Boolean = false): KLevel {
    if (off) {
        return KLevel.OFF
    }

    return when (this) {
        Level.TRACE -> KLevel.TRACE
        Level.DEBUG -> KLevel.DEBUG
        Level.INFO -> KLevel.INFO
        Level.WARN -> KLevel.WARN
        Level.ERROR -> KLevel.ERROR
    }
}
