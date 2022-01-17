package hu.numichi.kotlin.reactive.logger

import reactor.core.scheduler.Scheduler

val DEFAULT_SCHEDULER: Scheduler = hu.numichi.reactive.logger.ReactiveLogger.DEFAULT_SCHEDULER
const val DEFAULT_REACTOR_CONTEXT_MDC_KEY = hu.numichi.reactive.logger.ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY