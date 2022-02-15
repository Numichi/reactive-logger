package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.IReactorKLogger
import mu.KLogger
import mu.Marker

interface ICoroutineKLogger : ICoroutineLogger<IReactorKLogger> {
    override val logger: KLogger
        get() = reactorLogger.logger

    suspend fun trace(msg: () -> Any?): Unit = wrap { it.trace(msg) }
    suspend fun trace(t: Throwable?, msg: () -> Any?): Unit = wrap { it.trace(t, msg) }
    suspend fun trace(marker: Marker?, msg: () -> Any?): Unit = wrap { it.trace(marker, msg) }
    suspend fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Unit = wrap { it.trace(marker, t, msg) }

    suspend fun debug(msg: () -> Any?): Unit = wrap { it.debug(msg) }
    suspend fun debug(t: Throwable?, msg: () -> Any?): Unit = wrap { it.debug(t, msg) }
    suspend fun debug(marker: Marker?, msg: () -> Any?): Unit = wrap { it.debug(marker, msg) }
    suspend fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Unit = wrap { it.debug(marker, t, msg) }

    suspend fun info(msg: () -> Any?): Unit = wrap { it.info(msg) }
    suspend fun info(t: Throwable?, msg: () -> Any?): Unit = wrap { it.info(t, msg) }
    suspend fun info(marker: Marker?, msg: () -> Any?): Unit = wrap { it.info(marker, msg) }
    suspend fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Unit = wrap { it.info(marker, t, msg) }

    suspend fun warn(msg: () -> Any?): Unit = wrap { it.warn(msg) }
    suspend fun warn(t: Throwable?, msg: () -> Any?): Unit = wrap { it.warn(t, msg) }
    suspend fun warn(marker: Marker?, msg: () -> Any?): Unit = wrap { it.warn(marker, msg) }
    suspend fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Unit = wrap { it.warn(marker, t, msg) }

    suspend fun error(msg: () -> Any?): Unit = wrap { it.error(msg) }
    suspend fun error(t: Throwable?, msg: () -> Any?): Unit = wrap { it.error(t, msg) }
    suspend fun error(marker: Marker?, msg: () -> Any?): Unit = wrap { it.error(marker, msg) }
    suspend fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Unit = wrap { it.error(marker, t, msg) }
    
    suspend fun entry(vararg argArray: Any?): Unit = wrap { it.entry(*argArray) }
    suspend fun exit(): Unit = wrap { it.exit() }
    suspend fun <T> exit(result: T): T = wrapResult { it.exit(result) }.second
    suspend fun <T : Throwable> throwing(throwable: T): T = wrapResult { it.throwing(throwable) }
    suspend fun <T : Throwable> catching(throwable: T): T = wrapResult { it.catching(throwable) }
}