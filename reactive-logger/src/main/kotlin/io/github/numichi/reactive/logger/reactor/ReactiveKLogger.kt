package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.core.ReactiveCore
import mu.KLogger
import org.slf4j.Logger
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

class ReactiveKLogger(
    override val logger: KLogger,
    override val contextKey: Any,
    override val scheduler: Scheduler
) : ReactiveCore<KLogger>(), RKLogger {

    companion object {
        //region String base
        @JvmStatic
        fun getLogger(name: String): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, null)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, null)
        }

        @JvmStatic
        fun getLogger(name: String, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), null, scheduler)
        }

        @JvmStatic
        fun getLogger(name: String, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(name), contextKey, scheduler)
        }
        //endregion

        //region Class base
        @JvmStatic
        fun getLogger(clazz: Class<*>): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, null)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), null, scheduler)
        }

        @JvmStatic
        fun getLogger(clazz: Class<*>, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(clazz), contextKey, scheduler)
        }
        //endregion

        //region Logger base
        @JvmStatic
        fun getLogger(logger: Logger): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), null, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: Logger, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: Logger, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(logger), contextKey, scheduler)
        }
        //endregion

        //region Lambda base
        @JvmStatic
        fun getLogger(func: () -> Unit): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, null)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), null, scheduler)
        }

        @JvmStatic
        fun getLogger(func: () -> Unit, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return getLogger(LoggerFactory.getKLogger(func), contextKey, scheduler)
        }
        //endregion

        //region KLogger base
        @JvmStatic
        fun getLogger(logger: KLogger): ReactiveKLogger {
            return getLogger(logger, null, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any): ReactiveKLogger {
            return getLogger(logger, contextKey, null)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, scheduler: Scheduler): ReactiveKLogger {
            return getLogger(logger, null, scheduler)
        }

        @JvmStatic
        fun getLogger(logger: KLogger, contextKey: Any?, scheduler: Scheduler?): ReactiveKLogger {
            return ReactiveKLogger(
                logger,
                contextKey ?: Configuration.defaultReactorContextMdcKey,
                scheduler ?: Configuration.defaultScheduler
            )
        }
        //endregion
    }

    override val isTraceEnabled: Boolean
        get() = logger.isTraceEnabled

    override fun isTraceEnabled(marker: Marker?): Boolean = logger.isTraceEnabled(marker)
    override fun trace(msg: String?): Mono<Void> = wrap { logger.trace(msg) }
    override fun trace(format: String?, arg: Any?): Mono<Void> = wrap { logger.trace(format, arg) }
    override fun trace(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.trace(format, arg1, arg2) }
    override fun trace(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.trace(format, *arguments) }
    override fun trace(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.trace(msg, t) }
    override fun trace(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.trace(marker, msg) }
    override fun trace(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.trace(marker, format, arg) }
    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.trace(marker, format, arg1, arg2) }
    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.trace(marker, format, *argArray) }
    override fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.trace(marker, msg, t) }
    override fun trace(msg: () -> Any?): Mono<Void> = wrap { logger.trace(msg) }
    override fun trace(t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.trace(t, msg) }
    override fun trace(marker: Marker?, msg: () -> Any?): Mono<Void> = wrap { logger.trace(marker, msg) }
    override fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.trace(marker, t, msg) }

    override val isDebugEnabled: Boolean
        get() = logger.isDebugEnabled

    override fun isDebugEnabled(marker: Marker?): Boolean = logger.isDebugEnabled(marker)
    override fun debug(msg: String?): Mono<Void> = wrap { logger.debug(msg) }
    override fun debug(format: String?, arg: Any?): Mono<Void> = wrap { logger.debug(format, arg) }
    override fun debug(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.debug(format, arg1, arg2) }
    override fun debug(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.debug(format, *arguments) }
    override fun debug(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.debug(msg, t) }
    override fun debug(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.debug(marker, msg) }
    override fun debug(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.debug(marker, format, arg) }
    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.debug(marker, format, arg1, arg2) }
    override fun debug(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.debug(marker, format, *argArray) }
    override fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.debug(marker, msg, t) }
    override fun debug(msg: () -> Any?): Mono<Void> = wrap { logger.debug(msg) }
    override fun debug(t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.debug(t, msg) }
    override fun debug(marker: Marker?, msg: () -> Any?): Mono<Void> = wrap { logger.debug(marker, msg) }
    override fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.debug(marker, t, msg) }

    override val isInfoEnabled: Boolean
        get() = logger.isInfoEnabled

    override fun isInfoEnabled(marker: Marker?): Boolean = logger.isInfoEnabled(marker)
    override fun info(msg: String?): Mono<Void> = wrap { logger.info(msg) }
    override fun info(format: String?, arg: Any?): Mono<Void> = wrap { logger.info(format, arg) }
    override fun info(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.info(format, arg1, arg2) }
    override fun info(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.info(format, *arguments) }
    override fun info(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.info(msg, t) }
    override fun info(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.info(marker, msg) }
    override fun info(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.info(marker, format, arg) }
    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.info(marker, format, arg1, arg2) }
    override fun info(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.info(marker, format, *argArray) }
    override fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.info(marker, msg, t) }
    override fun info(msg: () -> Any?): Mono<Void> = wrap { logger.info(msg) }
    override fun info(t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.info(t, msg) }
    override fun info(marker: Marker?, msg: () -> Any?): Mono<Void> = wrap { logger.info(marker, msg) }
    override fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.info(marker, t, msg) }

    override val isWarnEnabled: Boolean
        get() = logger.isWarnEnabled

    override fun isWarnEnabled(marker: Marker?): Boolean = logger.isWarnEnabled(marker)
    override fun warn(msg: String?): Mono<Void> = wrap { logger.warn(msg) }
    override fun warn(format: String?, arg: Any?): Mono<Void> = wrap { logger.warn(format, arg) }
    override fun warn(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.warn(format, arg1, arg2) }
    override fun warn(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.warn(format, *arguments) }
    override fun warn(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.warn(msg, t) }
    override fun warn(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.warn(marker, msg) }
    override fun warn(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.warn(marker, format, arg) }
    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.warn(marker, format, arg1, arg2) }
    override fun warn(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.warn(marker, format, *argArray) }
    override fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.warn(marker, msg, t) }
    override fun warn(msg: () -> Any?): Mono<Void> = wrap { logger.warn(msg) }
    override fun warn(t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.warn(t, msg) }
    override fun warn(marker: Marker?, msg: () -> Any?): Mono<Void> = wrap { logger.warn(marker, msg) }
    override fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.warn(marker, t, msg) }

    override val isErrorEnabled: Boolean
        get() = logger.isErrorEnabled

    override fun isErrorEnabled(marker: Marker?): Boolean = logger.isErrorEnabled(marker)
    override fun error(msg: String?): Mono<Void> = wrap { logger.error(msg) }
    override fun error(format: String?, arg: Any?): Mono<Void> = wrap { logger.error(format, arg) }
    override fun error(format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.error(format, arg1, arg2) }
    override fun error(format: String?, vararg arguments: Any?): Mono<Void> = wrap { logger.error(format, *arguments) }
    override fun error(msg: String?, t: Throwable?): Mono<Void> = wrap { logger.error(msg, t) }
    override fun error(marker: Marker?, msg: String?): Mono<Void> = wrap { logger.error(marker, msg) }
    override fun error(marker: Marker?, format: String?, arg: Any?): Mono<Void> = wrap { logger.error(marker, format, arg) }
    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void> = wrap { logger.error(marker, format, arg1, arg2) }
    override fun error(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void> = wrap { logger.error(marker, format, *argArray) }
    override fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<Void> = wrap { logger.error(marker, msg, t) }
    override fun error(msg: () -> Any?): Mono<Void> = wrap { logger.error(msg) }
    override fun error(t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.error(t, msg) }
    override fun error(marker: Marker?, msg: () -> Any?): Mono<Void> = wrap { logger.error(marker, msg) }
    override fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void> = wrap { logger.error(marker, t, msg) }

    override fun entry(vararg argArray: Any?): Mono<Void> = wrap { logger.entry(*argArray) }
    override fun exit(): Mono<Void> = wrap { logger.exit() }
    override fun <T : Any?> exit(result: T): Mono<T> = wrap { logger.exit(result) }.then(Mono.justOrEmpty(result))
    override fun <T : Throwable> throwing(throwable: T): Mono<T> = wrap { logger.throwing(throwable) }.thenReturn(throwable)
    override fun <T : Throwable> catching(throwable: T): Mono<Void> = wrap { logger.catching(throwable) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReactiveKLogger) return false
        if (logger != other.logger) return false
        if (contextKey != other.contextKey) return false
        if (scheduler != other.scheduler) return false
        if (isTraceEnabled != other.isTraceEnabled) return false
        if (isDebugEnabled != other.isDebugEnabled) return false
        if (isInfoEnabled != other.isInfoEnabled) return false
        if (isWarnEnabled != other.isWarnEnabled) return false
        if (isErrorEnabled != other.isErrorEnabled) return false
        return true
    }

    override fun hashCode(): Int {
        var result = logger.hashCode()
        result = 31 * result + contextKey.hashCode()
        result = 31 * result + scheduler.hashCode()
        result = 31 * result + isTraceEnabled.hashCode()
        result = 31 * result + isDebugEnabled.hashCode()
        result = 31 * result + isInfoEnabled.hashCode()
        result = 31 * result + isWarnEnabled.hashCode()
        result = 31 * result + isErrorEnabled.hashCode()
        return result
    }

    override fun toString(): String {
        return "ReactiveKLogger(logger=$logger, contextKey='$contextKey', scheduler=$scheduler, isTraceEnabled=$isTraceEnabled, isDebugEnabled=$isDebugEnabled, isInfoEnabled=$isInfoEnabled, isWarnEnabled=$isWarnEnabled, isErrorEnabled=$isErrorEnabled)"
    }
}
