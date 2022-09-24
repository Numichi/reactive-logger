package io.github.numichi.reactive.logger.reactor

import mu.Marker
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

interface RKLogger : RLogger {
    fun trace(msg: () -> Any?): Mono<ContextView>
    fun trace(t: Throwable?, msg: () -> Any?): Mono<ContextView>
    fun trace(marker: Marker?, msg: () -> Any?): Mono<ContextView>
    fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView>

    fun debug(msg: () -> Any?): Mono<ContextView>
    fun debug(t: Throwable?, msg: () -> Any?): Mono<ContextView>
    fun debug(marker: Marker?, msg: () -> Any?): Mono<ContextView>
    fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView>

    fun info(msg: () -> Any?): Mono<ContextView>
    fun info(t: Throwable?, msg: () -> Any?): Mono<ContextView>
    fun info(marker: Marker?, msg: () -> Any?): Mono<ContextView>
    fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView>

    fun warn(msg: () -> Any?): Mono<ContextView>
    fun warn(t: Throwable?, msg: () -> Any?): Mono<ContextView>
    fun warn(marker: Marker?, msg: () -> Any?): Mono<ContextView>
    fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView>

    fun error(msg: () -> Any?): Mono<ContextView>
    fun error(t: Throwable?, msg: () -> Any?): Mono<ContextView>
    fun error(marker: Marker?, msg: () -> Any?): Mono<ContextView>
    fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView>

    fun entry(vararg argArray: Any?): Mono<ContextView>
    fun exit(): Mono<ContextView>
    fun <T> exit(result: T): Mono<T>
    fun <T : Throwable> throwing(throwable: T): Mono<T>
    fun <T : Throwable> catching(throwable: T): Mono<T>
}