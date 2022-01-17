package hu.numichi.kotlin.reactive.logger;

import hu.numichi.reactive.logger.ReactiveLogger;
import reactor.util.context.Context;

object MDCContextWriter {
    fun put(context: Context, mdc: Map<String, String>): Context {
        return context.put(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }

    fun put(context: Context, mdcContextKey: String, mdc: Map<String, String>): Context {
        return context.put(ReactiveLogger.DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc);
    }
}
