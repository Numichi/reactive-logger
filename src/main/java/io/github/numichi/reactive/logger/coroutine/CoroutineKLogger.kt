package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
import io.github.numichi.reactive.logger.core.CoroutineCore
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import kotlinx.coroutines.reactor.awaitSingle
import mu.KLogger
import mu.Marker
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

class CoroutineKLogger(
    override val reactiveLogger: ReactiveKLogger
) : CoroutineCore<ReactiveKLogger, KLogger>(reactiveLogger.logger, reactiveLogger.mdcContextKey, reactiveLogger.scheduler), CKLogger {

    companion object {
        @JvmStatic
        fun getLogger(string: String, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(string), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            return getLogger(LoggerFactory.getKLogger(func), mdcContextKey, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, mdcContextKey: String? = null, scheduler: Scheduler? = null): CoroutineKLogger {
            mdcContextKey?.also {
                check(it.trim().isNotEmpty()) { MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE }
            }

            return CoroutineKLogger(
                ReactiveKLogger(
                    logger,
                    mdcContextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                )
            )
        }
    }

    override val isTraceEnabled: Boolean
        get() = reactiveLogger.isTraceEnabled

    override fun isTraceEnabled(marker: Marker?): Boolean = reactiveLogger.isTraceEnabled(marker)
    override suspend fun trace(msg: String?) = wrapUnit { it.trace(msg) }
    override suspend fun trace(format: String?, arg: Any?) = wrapUnit { it.trace(format, arg) }
    override suspend fun trace(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.trace(format, arg1, arg2) }
    override suspend fun trace(format: String?, vararg arguments: Any?) = wrapUnit { it.trace(format, *arguments) }
    override suspend fun trace(msg: String?, t: Throwable?) = wrapUnit { it.trace(msg, t) }
    override suspend fun trace(marker: Marker?, msg: String?) = wrapUnit { it.trace(marker, msg) }
    override suspend fun trace(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.trace(marker, format, arg) }
    override suspend fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.trace(marker, format, arg1, arg2) }
    override suspend fun trace(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.trace(marker, format, *argArray) }
    override suspend fun trace(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.trace(marker, msg, t) }
    override suspend fun trace(msg: () -> Any?) = wrapUnit { it.trace(msg) }
    override suspend fun trace(t: Throwable?, msg: () -> Any?) = wrapUnit { it.trace(t, msg) }
    override suspend fun trace(marker: Marker?, msg: () -> Any?) = wrapUnit { it.trace(marker, msg) }
    override suspend fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?) = wrapUnit { it.trace(marker, t, msg) }

    override val isDebugEnabled: Boolean
        get() = reactiveLogger.isDebugEnabled

    override fun isDebugEnabled(marker: Marker?): Boolean = reactiveLogger.isDebugEnabled(marker)
    override suspend fun debug(msg: String?) = wrapUnit { it.debug(msg) }
    override suspend fun debug(format: String?, arg: Any?) = wrapUnit { it.debug(format, arg) }
    override suspend fun debug(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.debug(format, arg1, arg2) }
    override suspend fun debug(format: String?, vararg arguments: Any?) = wrapUnit { it.debug(format, *arguments) }
    override suspend fun debug(msg: String?, t: Throwable?) = wrapUnit { it.debug(msg, t) }
    override suspend fun debug(marker: Marker?, msg: String?) = wrapUnit { it.debug(marker, msg) }
    override suspend fun debug(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.debug(marker, format, arg) }
    override suspend fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.debug(marker, format, arg1, arg2) }
    override suspend fun debug(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.debug(marker, format, *argArray) }
    override suspend fun debug(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.debug(marker, msg, t) }
    override suspend fun debug(msg: () -> Any?) = wrapUnit { it.debug(msg) }
    override suspend fun debug(t: Throwable?, msg: () -> Any?) = wrapUnit { it.debug(t, msg) }
    override suspend fun debug(marker: Marker?, msg: () -> Any?) = wrapUnit { it.debug(marker, msg) }
    override suspend fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?) = wrapUnit { it.debug(marker, t, msg) }

    override val isInfoEnabled: Boolean
        get() = reactiveLogger.isInfoEnabled

    override fun isInfoEnabled(marker: Marker?): Boolean = reactiveLogger.isInfoEnabled(marker)
    override suspend fun info(msg: String?) = wrapUnit { it.info(msg) }
    override suspend fun info(format: String?, arg: Any?) = wrapUnit { it.info(format, arg) }
    override suspend fun info(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.info(format, arg1, arg2) }
    override suspend fun info(format: String?, vararg arguments: Any?) = wrapUnit { it.info(format, *arguments) }
    override suspend fun info(msg: String?, t: Throwable?) = wrapUnit { it.info(msg, t) }
    override suspend fun info(marker: Marker?, msg: String?) = wrapUnit { it.info(marker, msg) }
    override suspend fun info(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.info(marker, format, arg) }
    override suspend fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.info(marker, format, arg1, arg2) }
    override suspend fun info(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.info(marker, format, *argArray) }
    override suspend fun info(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.info(marker, msg, t) }
    override suspend fun info(msg: () -> Any?) = wrapUnit { it.info(msg) }
    override suspend fun info(t: Throwable?, msg: () -> Any?) = wrapUnit { it.info(t, msg) }
    override suspend fun info(marker: Marker?, msg: () -> Any?) = wrapUnit { it.info(marker, msg) }
    override suspend fun info(marker: Marker?, t: Throwable?, msg: () -> Any?) = wrapUnit { it.info(marker, t, msg) }

    override val isWarnEnabled: Boolean
        get() = reactiveLogger.isWarnEnabled

    override fun isWarnEnabled(marker: Marker?): Boolean = reactiveLogger.isWarnEnabled(marker)
    override suspend fun warn(msg: String?) = wrapUnit { it.warn(msg) }
    override suspend fun warn(format: String?, arg: Any?) = wrapUnit { it.warn(format, arg) }
    override suspend fun warn(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.warn(format, arg1, arg2) }
    override suspend fun warn(format: String?, vararg arguments: Any?) = wrapUnit { it.warn(format, *arguments) }
    override suspend fun warn(msg: String?, t: Throwable?) = wrapUnit { it.warn(msg, t) }
    override suspend fun warn(marker: Marker?, msg: String?) = wrapUnit { it.warn(marker, msg) }
    override suspend fun warn(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.warn(marker, format, arg) }
    override suspend fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.warn(marker, format, arg1, arg2) }
    override suspend fun warn(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.warn(marker, format, *argArray) }
    override suspend fun warn(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.warn(marker, msg, t) }
    override suspend fun warn(msg: () -> Any?) = wrapUnit { it.warn(msg) }
    override suspend fun warn(t: Throwable?, msg: () -> Any?) = wrapUnit { it.warn(t, msg) }
    override suspend fun warn(marker: Marker?, msg: () -> Any?) = wrapUnit { it.warn(marker, msg) }
    override suspend fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?) = wrapUnit { it.warn(marker, t, msg) }

    override val isErrorEnabled: Boolean
        get() = reactiveLogger.isErrorEnabled

    override fun isErrorEnabled(marker: Marker?): Boolean = reactiveLogger.isErrorEnabled(marker)
    override suspend fun error(msg: String?) = wrapUnit { it.error(msg) }
    override suspend fun error(format: String?, arg: Any?) = wrapUnit { it.error(format, arg) }
    override suspend fun error(format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.error(format, arg1, arg2) }
    override suspend fun error(format: String?, vararg arguments: Any?) = wrapUnit { it.error(format, *arguments) }
    override suspend fun error(msg: String?, t: Throwable?) = wrapUnit { it.error(msg, t) }
    override suspend fun error(marker: Marker?, msg: String?) = wrapUnit { it.error(marker, msg) }
    override suspend fun error(marker: Marker?, format: String?, arg: Any?) = wrapUnit { it.error(marker, format, arg) }
    override suspend fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapUnit { it.error(marker, format, arg1, arg2) }
    override suspend fun error(marker: Marker?, format: String?, vararg argArray: Any?) = wrapUnit { it.error(marker, format, *argArray) }
    override suspend fun error(marker: Marker?, msg: String?, t: Throwable?) = wrapUnit { it.error(marker, msg, t) }
    override suspend fun error(msg: () -> Any?) = wrapUnit { it.error(msg) }
    override suspend fun error(t: Throwable?, msg: () -> Any?) = wrapUnit { it.error(t, msg) }
    override suspend fun error(marker: Marker?, msg: () -> Any?) = wrapUnit { it.error(marker, msg) }
    override suspend fun error(marker: Marker?, t: Throwable?, msg: () -> Any?) = wrapUnit { it.error(marker, t, msg) }

    override suspend fun entry(vararg argArray: Any?) = wrapUnit { it.entry(*argArray) }
    override suspend fun exit() = wrapUnit { it.exit() }
    override suspend fun <T> exit(result: T) = wrap { it.exit(result) }
    override suspend fun <T : Throwable> throwing(throwable: T) = wrap { it.throwing(throwable) }
    override suspend fun <T : Throwable> catching(throwable: T) = wrap { it.catching(throwable) }

    // ------------------------------ DEPRECATED ------------------------------

    @Deprecated("Already out of use. It will be deleted in v3.3.0.", ReplaceWith("wrap(function)"))
    override suspend fun <R> wrapResult(function: (ReactiveKLogger) -> Mono<R>): R {
        val context = getContextView()
        return function(reactiveLogger)
            .contextWrite { it.putAll(context) }
            .awaitSingle()
    }
}

