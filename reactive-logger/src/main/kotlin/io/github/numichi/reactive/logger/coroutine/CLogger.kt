package io.github.numichi.reactive.logger.coroutine

import org.slf4j.Marker
import org.slf4j.event.Level

interface CLogger {
    fun isEnabledForLevel(level: Level): Boolean

    val isTraceEnabled: Boolean
    fun isTraceEnabled(marker: Marker?): Boolean
    suspend fun trace(msg: String?)
    suspend fun trace(format: String?, arg: Any?)
    suspend fun trace(format: String?, arg1: Any?, arg2: Any?)
    suspend fun trace(format: String?, vararg arguments: Any?)
    suspend fun trace(msg: String?, t: Throwable?)
    suspend fun trace(marker: Marker?, msg: String?)
    suspend fun trace(marker: Marker?, format: String?, arg: Any?)
    suspend fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?)
    suspend fun trace(marker: Marker?, format: String?, vararg argArray: Any?)
    suspend fun trace(marker: Marker?, msg: String?, t: Throwable?)

    val isDebugEnabled: Boolean
    fun isDebugEnabled(marker: Marker?): Boolean
    suspend fun debug(msg: String?)
    suspend fun debug(format: String?, arg: Any?)
    suspend fun debug(format: String?, arg1: Any?, arg2: Any?)
    suspend fun debug(format: String?, vararg arguments: Any?)
    suspend fun debug(msg: String?, t: Throwable?)
    suspend fun debug(marker: Marker?, msg: String?)
    suspend fun debug(marker: Marker?, format: String?, arg: Any?)
    suspend fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?)
    suspend fun debug(marker: Marker?, format: String?, vararg argArray: Any?)
    suspend fun debug(marker: Marker?, msg: String?, t: Throwable?)

    val isInfoEnabled: Boolean
    fun isInfoEnabled(marker: Marker?): Boolean
    suspend fun info(msg: String?)
    suspend fun info(format: String?, arg: Any?)
    suspend fun info(format: String?, arg1: Any?, arg2: Any?)
    suspend fun info(format: String?, vararg arguments: Any?)
    suspend fun info(msg: String?, t: Throwable?)
    suspend fun info(marker: Marker?, msg: String?)
    suspend fun info(marker: Marker?, format: String?, arg: Any?)
    suspend fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?)
    suspend fun info(marker: Marker?, format: String?, vararg argArray: Any?)
    suspend fun info(marker: Marker?, msg: String?, t: Throwable?)

    val isWarnEnabled: Boolean
    fun isWarnEnabled(marker: Marker?): Boolean
    suspend fun warn(msg: String?)
    suspend fun warn(format: String?, arg: Any?)
    suspend fun warn(format: String?, arg1: Any?, arg2: Any?)
    suspend fun warn(format: String?, vararg arguments: Any?)
    suspend fun warn(msg: String?, t: Throwable?)
    suspend fun warn(marker: Marker?, msg: String?)
    suspend fun warn(marker: Marker?, format: String?, arg: Any?)
    suspend fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?)
    suspend fun warn(marker: Marker?, format: String?, vararg argArray: Any?)
    suspend fun warn(marker: Marker?, msg: String?, t: Throwable?)

    val isErrorEnabled: Boolean
    fun isErrorEnabled(marker: Marker?): Boolean
    suspend fun error(msg: String?)
    suspend fun error(format: String?, arg: Any?)
    suspend fun error(format: String?, arg1: Any?, arg2: Any?)
    suspend fun error(format: String?, vararg arguments: Any?)
    suspend fun error(msg: String?, t: Throwable?)
    suspend fun error(marker: Marker?, msg: String?)
    suspend fun error(marker: Marker?, format: String?, arg: Any?)
    suspend fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?)
    suspend fun error(marker: Marker?, format: String?, vararg argArray: Any?)
    suspend fun error(marker: Marker?, msg: String?, t: Throwable?)
}