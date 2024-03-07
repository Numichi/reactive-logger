package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.core.RSnapshot
import org.slf4j.Marker
import org.slf4j.event.Level
import reactor.core.publisher.Mono

/**
 * @see [org.slf4j.Logger]
 */
interface RLogger : RSnapshot {
    fun isEnabledForLevel(level: Level): Boolean

    fun isTraceEnabled(): Boolean
    fun trace(msg: String?): Mono<Void>
    fun trace(format: String?, arg: Any?): Mono<Void>
    fun trace(format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun trace(format: String?, vararg arguments: Any?): Mono<Void>
    fun trace(msg: String?, t: Throwable?): Mono<Void>
    fun isTraceEnabled(marker: Marker?): Boolean
    fun trace(marker: Marker?, msg: String?): Mono<Void>
    fun trace(marker: Marker?, format: String?, arg: Any?): Mono<Void>
    fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Mono<Void>
    fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<Void>

    fun isDebugEnabled(): Boolean
    fun debug(msg: String?): Mono<Void>
    fun debug(format: String?, arg: Any?): Mono<Void>
    fun debug(format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun debug(format: String?, vararg arguments: Any?): Mono<Void>
    fun debug(msg: String?, t: Throwable?): Mono<Void>
    fun isDebugEnabled(marker: Marker?): Boolean
    fun debug(marker: Marker?, msg: String?): Mono<Void>
    fun debug(marker: Marker?, format: String?, arg: Any?): Mono<Void>
    fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun debug(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Void>
    fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<Void>

    fun isInfoEnabled(): Boolean
    fun info(msg: String?): Mono<Void>
    fun info(format: String?, arg: Any?): Mono<Void>
    fun info(format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun info(format: String?, vararg arguments: Any?): Mono<Void>
    fun info(msg: String?, t: Throwable?): Mono<Void>
    fun isInfoEnabled(marker: Marker?): Boolean
    fun info(marker: Marker?, msg: String?): Mono<Void>
    fun info(marker: Marker?, format: String?, arg: Any?): Mono<Void>
    fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun info(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Void>
    fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<Void>

    fun isWarnEnabled(): Boolean
    fun warn(msg: String?): Mono<Void>
    fun warn(format: String?, arg: Any?): Mono<Void>
    fun warn(format: String?, vararg arguments: Any?): Mono<Void>
    fun warn(format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun warn(msg: String?, t: Throwable?): Mono<Void>
    fun isWarnEnabled(marker: Marker?): Boolean
    fun warn(marker: Marker?, msg: String?): Mono<Void>
    fun warn(marker: Marker?, format: String?, arg: Any?): Mono<Void>
    fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun warn(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Void>
    fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<Void>

    fun isErrorEnabled(): Boolean
    fun error(msg: String?): Mono<Void>
    fun error(format: String?, arg: Any?): Mono<Void>
    fun error(format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun error(format: String?, vararg arguments: Any?): Mono<Void>
    fun error(msg: String?, t: Throwable?): Mono<Void>
    fun isErrorEnabled(marker: Marker?): Boolean
    fun error(marker: Marker?, msg: String?): Mono<Void>
    fun error(marker: Marker?, format: String?, arg: Any?): Mono<Void>
    fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<Void>
    fun error(marker: Marker?, format: String?, vararg arguments: Any?): Mono<Void>
    fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<Void>
}