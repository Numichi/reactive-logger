package io.github.numichi.reactive.logger.coroutine

import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker

/**
 * Coroutine mirror of [io.github.oshai.kotlinlogging.KLogger].
 */
interface CKLogger {

    val name: String

    suspend fun trace(message: () -> Any?)
    suspend fun debug(message: () -> Any?)
    suspend fun info(message: () -> Any?)
    suspend fun warn(message: () -> Any?)
    suspend fun error(message: () -> Any?)

    suspend fun trace(throwable: Throwable?, message: () -> Any?)
    suspend fun debug(throwable: Throwable?, message: () -> Any?)
    suspend fun info(throwable: Throwable?, message: () -> Any?)
    suspend fun warn(throwable: Throwable?, message: () -> Any?)
    suspend fun error(throwable: Throwable?, message: () -> Any?)

    suspend fun trace(throwable: Throwable?, marker: Marker?, message: () -> Any?)
    suspend fun debug(throwable: Throwable?, marker: Marker?, message: () -> Any?)
    suspend fun info(throwable: Throwable?, marker: Marker?, message: () -> Any?)
    suspend fun warn(throwable: Throwable?, marker: Marker?, message: () -> Any?)
    suspend fun error(throwable: Throwable?, marker: Marker?, message: () -> Any?)

    suspend fun atTrace(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)
    suspend fun atTrace(block: KLoggingEventBuilder.() -> Unit)
    suspend fun atDebug(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)
    suspend fun atDebug(block: KLoggingEventBuilder.() -> Unit)
    suspend fun atInfo(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)
    suspend fun atInfo(block: KLoggingEventBuilder.() -> Unit)
    suspend fun atWarn(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)
    suspend fun atWarn(block: KLoggingEventBuilder.() -> Unit)
    suspend fun atError(marker: Marker?, block: KLoggingEventBuilder.() -> Unit)
    suspend fun atError(block: KLoggingEventBuilder.() -> Unit)
    suspend fun at(level: Level, marker: Marker? = null, block: KLoggingEventBuilder.() -> Unit)

    suspend fun entry(vararg arguments: Any?)
    suspend fun exit()
    suspend fun <T> exit(result: T): T
    suspend fun <T> throwing(throwable: T): T where T : Throwable
    suspend fun <T> catching(throwable: T) where T : Throwable

    fun isTraceEnabled(marker: Marker? = null): Boolean
    fun isDebugEnabled(marker: Marker? = null): Boolean
    fun isInfoEnabled(marker: Marker? = null): Boolean
    fun isWarnEnabled(marker: Marker? = null): Boolean
    fun isErrorEnabled(marker: Marker? = null): Boolean
    fun isLoggingOff(marker: Marker? = null): Boolean
    fun isLoggingEnabledFor(level: Level, marker: Marker? = null): Boolean
}