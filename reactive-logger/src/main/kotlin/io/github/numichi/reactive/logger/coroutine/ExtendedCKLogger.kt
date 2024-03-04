package io.github.numichi.reactive.logger.coroutine

import io.github.oshai.kotlinlogging.Marker

/**
 * A repository of metrics not included in the original interface but traceable back to it.
 */
interface ExtendedCKLogger : CKLogger {
    val isTraceEnabled: Boolean get() = isTraceEnabled()
    val isDebugEnabled: Boolean get() = isDebugEnabled()
    val isInfoEnabled: Boolean get() = isInfoEnabled()
    val isWarnEnabled: Boolean get() = isWarnEnabled()
    val isErrorEnabled: Boolean get() = isErrorEnabled()

    suspend fun trace(message: String?) = trace { message }
    suspend fun debug(message: String?) = debug { message }
    suspend fun info(message: String?) = info { message }
    suspend fun warn(message: String?) = warn { message }
    suspend fun error(message: String?) = error { message }

    suspend fun trace(throwable: Throwable?, message: String?) = trace(throwable) { message }
    suspend fun debug(throwable: Throwable?, message: String?) = debug(throwable) { message }
    suspend fun info(throwable: Throwable?, message: String?) = info(throwable) { message }
    suspend fun warn(throwable: Throwable?, message: String?) = warn(throwable) { message }
    suspend fun error(throwable: Throwable?, message: String?) = error(throwable) { message }

    suspend fun trace(message: String?, throwable: Throwable?) = trace(throwable) { message }
    suspend fun debug(message: String?, throwable: Throwable?) = debug(throwable) { message }
    suspend fun info(message: String?, throwable: Throwable?) = info(throwable) { message }
    suspend fun warn(message: String?, throwable: Throwable?) = warn(throwable) { message }
    suspend fun error(message: String?, throwable: Throwable?) = error(throwable) { message }

    suspend fun trace(marker: Marker?, message: () -> Any?) = trace(null as Throwable?, marker, message)
    suspend fun debug(marker: Marker?, message: () -> Any?) = debug(null as Throwable?, marker, message)
    suspend fun info(marker: Marker?, message: () -> Any?) = info(null as Throwable?, marker, message)
    suspend fun warn(marker: Marker?, message: () -> Any?) = warn(null as Throwable?, marker, message)
    suspend fun error(marker: Marker?, message: () -> Any?) = error(null as Throwable?, marker, message)

    suspend fun trace(marker: Marker?, message: String?) = trace(null as Throwable?, marker) { message }
    suspend fun debug(marker: Marker?, message: String?) = debug(null as Throwable?, marker) { message }
    suspend fun info(marker: Marker?, message: String?) = info(null as Throwable?, marker) { message }
    suspend fun warn(marker: Marker?, message: String?) = warn(null as Throwable?, marker) { message }
    suspend fun error(marker: Marker?, message: String?) = error(null as Throwable?, marker) { message }

    suspend fun trace(marker: Marker?, message: String?, throwable: Throwable?) = trace(throwable, marker) { message }
    suspend fun debug(marker: Marker?, message: String?, throwable: Throwable?) = debug(throwable, marker) { message }
    suspend fun info(marker: Marker?, message: String?, throwable: Throwable?) = info(throwable, marker) { message }
    suspend fun warn(marker: Marker?, message: String?, throwable: Throwable?) = warn(throwable, marker) { message }
    suspend fun error(marker: Marker?, message: String?, throwable: Throwable?) = error(throwable, marker) { message }

    suspend fun trace(marker: Marker?, throwable: Throwable?, message: () -> Any?) = trace(throwable, marker) { message }
    suspend fun debug(marker: Marker?, throwable: Throwable?, message: () -> Any?) = debug(throwable, marker) { message }
    suspend fun info(marker: Marker?, throwable: Throwable?, message: () -> Any?) = info(throwable, marker) { message }
    suspend fun warn(marker: Marker?, throwable: Throwable?, message: () -> Any?) = warn(throwable, marker) { message }
    suspend fun error(marker: Marker?, throwable: Throwable?, message: () -> Any?) = error(throwable, marker) { message }
}