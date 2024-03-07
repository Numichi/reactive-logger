package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.core.RSnapshot
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker
import reactor.core.publisher.Mono

/**
 * Reactive mirror of [io.github.oshai.kotlinlogging.KLogger].
 */
interface RKLogger : RSnapshot {
    val name: String

    fun trace(message: () -> Any?): Mono<Void>

    fun debug(message: () -> Any?): Mono<Void>

    fun info(message: () -> Any?): Mono<Void>

    fun warn(message: () -> Any?): Mono<Void>

    fun error(message: () -> Any?): Mono<Void>

    fun trace(
        throwable: Throwable?,
        message: () -> Any?,
    ): Mono<Void>

    fun debug(
        throwable: Throwable?,
        message: () -> Any?,
    ): Mono<Void>

    fun info(
        throwable: Throwable?,
        message: () -> Any?,
    ): Mono<Void>

    fun warn(
        throwable: Throwable?,
        message: () -> Any?,
    ): Mono<Void>

    fun error(
        throwable: Throwable?,
        message: () -> Any?,
    ): Mono<Void>

    fun trace(
        throwable: Throwable?,
        marker: Marker?,
        message: () -> Any?,
    ): Mono<Void>

    fun debug(
        throwable: Throwable?,
        marker: Marker?,
        message: () -> Any?,
    ): Mono<Void>

    fun info(
        throwable: Throwable?,
        marker: Marker?,
        message: () -> Any?,
    ): Mono<Void>

    fun warn(
        throwable: Throwable?,
        marker: Marker?,
        message: () -> Any?,
    ): Mono<Void>

    fun error(
        throwable: Throwable?,
        marker: Marker?,
        message: () -> Any?,
    ): Mono<Void>

    fun atTrace(
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun atTrace(block: KLoggingEventBuilder.() -> Unit): Mono<Void>

    fun atDebug(
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun atDebug(block: KLoggingEventBuilder.() -> Unit): Mono<Void>

    fun atInfo(
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun atInfo(block: KLoggingEventBuilder.() -> Unit): Mono<Void>

    fun atWarn(
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun atWarn(block: KLoggingEventBuilder.() -> Unit): Mono<Void>

    fun atError(
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun atError(block: KLoggingEventBuilder.() -> Unit): Mono<Void>

    fun at(
        level: Level,
        marker: Marker? = null,
        block: KLoggingEventBuilder.() -> Unit,
    ): Mono<Void>

    fun entry(vararg arguments: Any?): Mono<Void>

    fun exit(): Mono<Void>

    fun <T> exit(result: T): Mono<T>

    fun <T> throwing(throwable: T): Mono<T> where T : Throwable

    fun <T> catching(throwable: T): Mono<Void> where T : Throwable

    fun isTraceEnabled(marker: Marker? = null): Boolean

    fun isDebugEnabled(marker: Marker? = null): Boolean

    fun isInfoEnabled(marker: Marker? = null): Boolean

    fun isWarnEnabled(marker: Marker? = null): Boolean

    fun isErrorEnabled(marker: Marker? = null): Boolean

    fun isLoggingOff(marker: Marker? = null): Boolean

    fun isLoggingEnabledFor(
        level: Level,
        marker: Marker? = null,
    ): Boolean
}
