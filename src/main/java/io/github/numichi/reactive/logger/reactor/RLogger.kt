package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.core.RSnapshot
import org.slf4j.Marker
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

interface RLogger : RSnapshot {
    val isTraceEnabled: Boolean
    fun isTraceEnabled(marker: Marker?): Boolean
    fun trace(msg: String?): Mono<ContextView>
    fun trace(format: String?, arg: Any?): Mono<ContextView>
    fun trace(format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun trace(format: String?, vararg arguments: Any?): Mono<ContextView>
    fun trace(msg: String?, t: Throwable?): Mono<ContextView>
    fun trace(marker: Marker?, msg: String?): Mono<ContextView>
    fun trace(marker: Marker?, format: String?, arg: Any?): Mono<ContextView>
    fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun trace(marker: Marker?, format: String?, vararg argArray: Any?): Mono<ContextView>
    fun trace(marker: Marker?, msg: String?, t: Throwable?): Mono<ContextView>

    val isDebugEnabled: Boolean
    fun isDebugEnabled(marker: Marker?): Boolean
    fun debug(msg: String?): Mono<ContextView>
    fun debug(format: String?, arg: Any?): Mono<ContextView>
    fun debug(format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun debug(format: String?, vararg arguments: Any?): Mono<ContextView>
    fun debug(msg: String?, t: Throwable?): Mono<ContextView>
    fun debug(marker: Marker?, msg: String?): Mono<ContextView>
    fun debug(marker: Marker?, format: String?, arg: Any?): Mono<ContextView>
    fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun debug(marker: Marker?, format: String?, vararg argArray: Any?): Mono<ContextView>
    fun debug(marker: Marker?, msg: String?, t: Throwable?): Mono<ContextView>

    val isInfoEnabled: Boolean
    fun isInfoEnabled(marker: Marker?): Boolean
    fun info(msg: String?): Mono<ContextView>
    fun info(format: String?, arg: Any?): Mono<ContextView>
    fun info(format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun info(format: String?, vararg arguments: Any?): Mono<ContextView>
    fun info(msg: String?, t: Throwable?): Mono<ContextView>
    fun info(marker: Marker?, msg: String?): Mono<ContextView>
    fun info(marker: Marker?, format: String?, arg: Any?): Mono<ContextView>
    fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun info(marker: Marker?, format: String?, vararg argArray: Any?): Mono<ContextView>
    fun info(marker: Marker?, msg: String?, t: Throwable?): Mono<ContextView>

    val isWarnEnabled: Boolean
    fun isWarnEnabled(marker: Marker?): Boolean
    fun warn(msg: String?): Mono<ContextView>
    fun warn(format: String?, arg: Any?): Mono<ContextView>
    fun warn(format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun warn(format: String?, vararg arguments: Any?): Mono<ContextView>
    fun warn(msg: String?, t: Throwable?): Mono<ContextView>
    fun warn(marker: Marker?, msg: String?): Mono<ContextView>
    fun warn(marker: Marker?, format: String?, arg: Any?): Mono<ContextView>
    fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun warn(marker: Marker?, format: String?, vararg argArray: Any?): Mono<ContextView>
    fun warn(marker: Marker?, msg: String?, t: Throwable?): Mono<ContextView>

    val isErrorEnabled: Boolean
    fun isErrorEnabled(marker: Marker?): Boolean
    fun error(msg: String?): Mono<ContextView>
    fun error(format: String?, arg: Any?): Mono<ContextView>
    fun error(format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun error(format: String?, vararg arguments: Any?): Mono<ContextView>
    fun error(msg: String?, t: Throwable?): Mono<ContextView>
    fun error(marker: Marker?, msg: String?): Mono<ContextView>
    fun error(marker: Marker?, format: String?, arg: Any?): Mono<ContextView>
    fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?): Mono<ContextView>
    fun error(marker: Marker?, format: String?, vararg argArray: Any?): Mono<ContextView>
    fun error(marker: Marker?, msg: String?, t: Throwable?): Mono<ContextView>
}