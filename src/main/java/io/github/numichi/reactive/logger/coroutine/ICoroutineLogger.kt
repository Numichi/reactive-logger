package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.IReactorLogger
import org.slf4j.Logger
import org.slf4j.Marker

interface ICoroutineLogger<T : IReactorLogger> : ICoroutineCore<T> {
    val logger: Logger
        get() = reactorLogger.logger

    val isTraceEnabled: Boolean
        get() = reactorLogger.isTraceEnabled
    suspend fun isTraceEnabled(marker: Marker?): Boolean = reactorLogger.isTraceEnabled(marker)
    suspend fun trace(msg: String?): Unit = wrap { it.trace(msg) }
    suspend fun trace(format: String?, arg: Any?): Unit = wrap { it.trace(format, arg) }
    suspend fun trace(format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.trace(format, arg1, arg2) }
    suspend fun trace(format: String?, vararg arguments: Any?): Unit = wrap { it.trace(format, *arguments) }
    suspend fun trace(msg: String?, t: Throwable?): Unit = wrap { it.trace(msg, t) }
    suspend fun trace(marker: Marker?, msg: String?): Unit = wrap { it.trace(marker, msg) }
    suspend fun trace(marker: Marker?, format: String?, arg: Any?): Unit = wrap { it.trace(marker, format, arg) }
    suspend fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.trace(marker, format, arg1, arg2) }
    suspend fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Unit = wrap { it.trace(marker, format, *argArray) }
    suspend fun trace(marker: Marker?, msg: String?, t: Throwable?): Unit = wrap { it.trace(marker, msg, t) }

    val isDebugEnabled: Boolean
        get() = reactorLogger.isDebugEnabled
    suspend fun isDebugEnabled(marker: Marker?): Boolean = reactorLogger.isDebugEnabled(marker)
    suspend fun debug(msg: String?): Unit = wrap { it.debug(msg) }
    suspend fun debug(format: String?, arg: Any?): Unit = wrap { it.debug(format, arg) }
    suspend fun debug(format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.debug(format, arg1, arg2) }
    suspend fun debug(format: String?, vararg arguments: Any?): Unit = wrap { it.debug(format, *arguments) }
    suspend fun debug(msg: String?, t: Throwable?): Unit = wrap { it.debug(msg, t) }
    suspend fun debug(marker: Marker?, msg: String?): Unit = wrap { it.debug(marker, msg) }
    suspend fun debug(marker: Marker?, format: String?, arg: Any?): Unit = wrap { it.debug(marker, format, arg) }
    suspend fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.debug(marker, format, arg1, arg2) }
    suspend fun debug(marker: Marker?, format: String?, vararg argArray: Any?): Unit = wrap { it.debug(marker, format, *argArray) }
    suspend fun debug(marker: Marker?, msg: String?, t: Throwable?): Unit = wrap { it.debug(marker, msg, t) }

    val isInfoEnabled: Boolean
        get() = reactorLogger.isInfoEnabled
    suspend fun isInfoEnabled(marker: Marker?): Boolean = reactorLogger.isInfoEnabled(marker)
    suspend fun info(msg: String?): Unit = wrap { it.info(msg) }
    suspend fun info(format: String?, arg: Any?): Unit = wrap { it.info(format, arg) }
    suspend fun info(format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.info(format, arg1, arg2) }
    suspend fun info(format: String?, vararg arguments: Any?): Unit = wrap { it.info(format, *arguments) }
    suspend fun info(msg: String?, t: Throwable?): Unit = wrap { it.info(msg, t) }
    suspend fun info(marker: Marker?, msg: String?): Unit = wrap { it.info(marker, msg) }
    suspend fun info(marker: Marker?, format: String?, arg: Any?): Unit = wrap { it.info(marker, format, arg) }
    suspend fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.info(marker, format, arg1, arg2) }
    suspend fun info(marker: Marker?, format: String?, vararg argArray: Any?): Unit = wrap { it.info(marker, format, *argArray) }
    suspend fun info(marker: Marker?, msg: String?, t: Throwable?): Unit = wrap { it.info(marker, msg, t) }

    val isWarnEnabled: Boolean
        get() = reactorLogger.isWarnEnabled
    suspend fun isWarnEnabled(marker: Marker?): Boolean = reactorLogger.isWarnEnabled(marker)
    suspend fun warn(msg: String?): Unit = wrap { it.warn(msg) }
    suspend fun warn(format: String?, arg: Any?): Unit = wrap { it.warn(format, arg) }
    suspend fun warn(format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.warn(format, arg1, arg2) }
    suspend fun warn(format: String?, vararg arguments: Any?): Unit = wrap { it.warn(format, *arguments) }
    suspend fun warn(msg: String?, t: Throwable?): Unit = wrap { it.warn(msg, t) }
    suspend fun warn(marker: Marker?, msg: String?): Unit = wrap { it.warn(marker, msg) }
    suspend fun warn(marker: Marker?, format: String?, arg: Any?): Unit = wrap { it.warn(marker, format, arg) }
    suspend fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.warn(marker, format, arg1, arg2) }
    suspend fun warn(marker: Marker?, format: String?, vararg argArray: Any?): Unit = wrap { it.warn(marker, format, *argArray) }
    suspend fun warn(marker: Marker?, msg: String?, t: Throwable?): Unit = wrap { it.warn(marker, msg, t) }

    val isErrorEnabled: Boolean
        get() = reactorLogger.isErrorEnabled
    suspend fun isErrorEnabled(marker: Marker?): Boolean = reactorLogger.isErrorEnabled(marker)
    suspend fun error(msg: String?): Unit = wrap { it.error(msg) }
    suspend fun error(format: String?, arg: Any?): Unit = wrap { it.error(format, arg) }
    suspend fun error(format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.error(format, arg1, arg2) }
    suspend fun error(format: String?, vararg arguments: Any?): Unit = wrap { it.error(format, *arguments) }
    suspend fun error(msg: String?, t: Throwable?): Unit = wrap { it.error(msg, t) }
    suspend fun error(marker: Marker?, msg: String?): Unit = wrap { it.error(marker, msg) }
    suspend fun error(marker: Marker?, format: String?, arg: Any?): Unit = wrap { it.error(marker, format, arg) }
    suspend fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Unit = wrap { it.error(marker, format, arg1, arg2) }
    suspend fun error(marker: Marker?, format: String?, vararg argArray: Any?): Unit = wrap { it.error(marker, format, *argArray) }
    suspend fun error(marker: Marker?, msg: String?, t: Throwable?): Unit = wrap { it.error(marker, msg, t) }
}