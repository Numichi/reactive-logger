package io.github.numichi.reactive.logger.reactor

import mu.KLogger
import mu.Marker
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

interface IReactorKLogger : IReactorLogger {
    override val logger: KLogger

    fun trace(msg: () -> Any?): Mono<ContextView> = wrap { logger.trace(msg) }
    fun trace(t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.trace(t, msg) }
    fun trace(marker: Marker?, msg: () -> Any?): Mono<ContextView> = wrap { logger.trace(marker, msg) }
    fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.trace(marker, t, msg) }

    fun debug(msg: () -> Any?): Mono<ContextView> = wrap { logger.debug(msg) }
    fun debug(t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.debug(t, msg) }
    fun debug(marker: Marker?, msg: () -> Any?): Mono<ContextView> = wrap { logger.debug(marker, msg) }
    fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.debug(marker, t, msg) }

    fun info(msg: () -> Any?): Mono<ContextView> = wrap { logger.info(msg) }
    fun info(t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.info(t, msg) }
    fun info(marker: Marker?, msg: () -> Any?): Mono<ContextView> = wrap { logger.info(marker, msg) }
    fun info(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.info(marker, t, msg) }

    fun warn(msg: () -> Any?): Mono<ContextView> = wrap { logger.warn(msg) }
    fun warn(t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.warn(t, msg) }
    fun warn(marker: Marker?, msg: () -> Any?): Mono<ContextView> = wrap { logger.warn(marker, msg) }
    fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.warn(marker, t, msg) }

    fun error(msg: () -> Any?): Mono<ContextView> = wrap { logger.error(msg) }
    fun error(t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.error(t, msg) }
    fun error(marker: Marker?, msg: () -> Any?): Mono<ContextView> = wrap { logger.error(marker, msg) }
    fun error(marker: Marker?, t: Throwable?, msg: () -> Any?): Mono<ContextView> = wrap { logger.error(marker, t, msg) }

    fun entry(vararg argArray: Any?): Mono<ContextView> = wrap { logger.entry(*argArray) }
    fun exit(): Mono<ContextView> = wrap { logger.exit() }
    fun <T> exit(result: T): Mono<Pair<ContextView?, T>> =  wrap { logger.exit(result) }.map { Pair(it, result) }
    fun <T : Throwable> throwing(throwable: T): Mono<T> = wrap { logger.throwing(throwable) }.map { throwable }
    fun <T : Throwable> catching(throwable: T): Mono<T> = wrap { logger.catching(throwable) }.map { throwable }
}