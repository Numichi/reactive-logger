package io.github.numichi.reactive.logger.coroutine

import mu.Marker

interface CKLogger : CLogger {
    suspend fun trace(msg: () -> Any?)
    suspend fun trace(t: Throwable?, msg: () -> Any?)
    suspend fun trace(marker: Marker?, msg: () -> Any?)
    suspend fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?)

    suspend fun debug(msg: () -> Any?)
    suspend fun debug(t: Throwable?, msg: () -> Any?)
    suspend fun debug(marker: Marker?, msg: () -> Any?)
    suspend fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?)

    suspend fun info(msg: () -> Any?)
    suspend fun info(t: Throwable?, msg: () -> Any?)
    suspend fun info(marker: Marker?, msg: () -> Any?)
    suspend fun info(marker: Marker?, t: Throwable?, msg: () -> Any?)

    suspend fun warn(msg: () -> Any?)
    suspend fun warn(t: Throwable?, msg: () -> Any?)
    suspend fun warn(marker: Marker?, msg: () -> Any?)
    suspend fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?)

    suspend fun error(msg: () -> Any?)
    suspend fun error(t: Throwable?, msg: () -> Any?)
    suspend fun error(marker: Marker?, msg: () -> Any?)
    suspend fun error(marker: Marker?, t: Throwable?, msg: () -> Any?)

    suspend fun entry(vararg argArray: Any?)
    suspend fun exit()
    suspend fun <T> exit(result: T): T
    suspend fun <T : Throwable> throwing(throwable: T): Throwable
    suspend fun <T : Throwable> catching(throwable: T)
}