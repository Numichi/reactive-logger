package io.github.numichi.reactive.logger.reactor

import mu.Marker
import reactor.core.publisher.Mono

/**
 * @see [mu.KLogger]
 */
interface RKLogger : RLogger {
    fun trace(msg: () -> Any?): Mono<Void>
    fun trace(t: Throwable?, msg: () -> Any?): Mono<Void>
    fun trace(marker: Marker?, msg: () -> Any?): Mono<Void>
    fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void>

    fun debug(msg: () -> Any?): Mono<Void>
    fun debug(t: Throwable?, msg: () -> Any?): Mono<Void>
    fun debug(marker: Marker?, msg: () -> Any?): Mono<Void>
    fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void>

    fun info(msg: () -> Any?): Mono<Void>
    fun info(t: Throwable?, msg: () -> Any?): Mono<Void>
    fun info(marker: Marker?, msg: () -> Any?): Mono<Void>
    fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void>

    fun warn(msg: () -> Any?): Mono<Void>
    fun warn(t: Throwable?, msg: () -> Any?): Mono<Void>
    fun warn(marker: Marker?, msg: () -> Any?): Mono<Void>
    fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void>

    fun error(msg: () -> Any?): Mono<Void>
    fun error(t: Throwable?, msg: () -> Any?): Mono<Void>
    fun error(marker: Marker?, msg: () -> Any?): Mono<Void>
    fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<Void>

    fun entry(vararg argArray: Any?): Mono<Void>
    fun exit(): Mono<Void>
    fun <T> exit(result: T): Mono<T>
    fun <T : Throwable> throwing(throwable: T): Mono<T>
    fun <T : Throwable> catching(throwable: T): Mono<Void>
}