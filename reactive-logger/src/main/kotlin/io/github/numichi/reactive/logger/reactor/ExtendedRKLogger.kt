package io.github.numichi.reactive.logger.reactor

import io.github.oshai.kotlinlogging.Marker

/**
 * A repository of methods not included in the original interface but traceable back to it.
 */
interface ExtendedRKLogger : RKLogger {
    val isTraceEnabled: Boolean get() = isTraceEnabled()
    val isDebugEnabled: Boolean get() = isDebugEnabled()
    val isInfoEnabled: Boolean get() = isInfoEnabled()
    val isWarnEnabled: Boolean get() = isWarnEnabled()
    val isErrorEnabled: Boolean get() = isErrorEnabled()

    fun trace(message: String?) = trace { message }
    fun debug(message: String?) = debug { message }
    fun info(message: String?) = info { message }
    fun warn(message: String?) = warn { message }
    fun error(message: String?) = error { message }

    fun trace(throwable: Throwable?, message: String?) = trace(throwable) { message }
    fun debug(throwable: Throwable?, message: String?) = debug(throwable) { message }
    fun info(throwable: Throwable?, message: String?) = info(throwable) { message }
    fun warn(throwable: Throwable?, message: String?) = warn(throwable) { message }
    fun error(throwable: Throwable?, message: String?) = error(throwable) { message }

    fun trace(message: String?, throwable: Throwable?) = trace(throwable) { message }
    fun debug(message: String?, throwable: Throwable?) = debug(throwable) { message }
    fun info(message: String?, throwable: Throwable?) = info(throwable) { message }
    fun warn(message: String?, throwable: Throwable?) = warn(throwable) { message }
    fun error(message: String?, throwable: Throwable?) = error(throwable) { message }

    fun trace(marker: Marker?, message: () -> Any?) = trace(null as Throwable?, marker, message)
    fun debug(marker: Marker?, message: () -> Any?) = debug(null as Throwable?, marker, message)
    fun info(marker: Marker?, message: () -> Any?) = info(null as Throwable?, marker, message)
    fun warn(marker: Marker?, message: () -> Any?) = warn(null as Throwable?, marker, message)
    fun error(marker: Marker?, message: () -> Any?) = error(null as Throwable?, marker, message)
    
    fun trace(marker: Marker?, message: String?) = trace(null as Throwable?, marker) { message }
    fun debug(marker: Marker?, message: String?) = debug(null as Throwable?, marker) { message }
    fun info(marker: Marker?, message: String?) = info(null as Throwable?, marker) { message }
    fun warn(marker: Marker?, message: String?) = warn(null as Throwable?, marker) { message }
    fun error(marker: Marker?, message: String?) = error(null as Throwable?, marker) { message }

    fun trace(marker: Marker?, message: String?, throwable: Throwable?) = trace(throwable, marker) { message }
    fun debug(marker: Marker?, message: String?, throwable: Throwable?) = debug(throwable, marker) { message }
    fun info(marker: Marker?, message: String?, throwable: Throwable?) = info(throwable, marker) { message }
    fun warn(marker: Marker?, message: String?, throwable: Throwable?) = warn(throwable, marker) { message }
    fun error(marker: Marker?, message: String?, throwable: Throwable?) = error(throwable, marker) { message }

    fun trace(marker: Marker?, throwable: Throwable?, message: () -> Any?) = trace(throwable, marker) { message }
    fun debug(marker: Marker?, throwable: Throwable?, message: () -> Any?) = debug(throwable, marker) { message }
    fun info(marker: Marker?, throwable: Throwable?, message: () -> Any?) = info(throwable, marker) { message }
    fun warn(marker: Marker?, throwable: Throwable?, message: () -> Any?) = warn(throwable, marker) { message }
    fun error(marker: Marker?, throwable: Throwable?, message: () -> Any?) = error(throwable, marker) { message }
}