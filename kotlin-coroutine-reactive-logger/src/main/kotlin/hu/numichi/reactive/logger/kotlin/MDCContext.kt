package hu.numichi.reactive.logger.kotlin;

import hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import hu.numichi.reactive.logger.MDC
import hu.numichi.reactive.logger.exception.InvalidContextDataException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context
import reactor.util.context.ContextView

object MDCContext {

    suspend fun read(): MDC {
        return read(DEFAULT_REACTOR_CONTEXT_MDC_KEY)
    }

    suspend fun read(mdcContextKey: String): MDC {
        return read(rectorContext(), mdcContextKey)
    }

    fun read(context: ContextView?): MDC {
        return read(context, DEFAULT_REACTOR_CONTEXT_MDC_KEY)
    }

    fun read(contextView: ContextView?, mdcContextKey: String): MDC {
        if (contextView == null) {
            throw NullPointerException("ContextView must not be null or this context is not exist")
        }

        val mdc = MDC(mdcContextKey)

        try {
            val map: Map<String, String> = contextView.get(mdcContextKey)
            mdc.putAll(map)
        } catch (exception: Exception) {
            throw InvalidContextDataException(exception)
        }

        return mdc
    }

    suspend fun <T> withContext(vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
        return withContext(mdc.toList(), rectorContext(), block)
    }

    suspend fun <T> withContext(context: Context, vararg mdc: MDC, block: suspend CoroutineScope.() -> T): T {
        return withContext(mdc.toList(), context, block)
    }

    private suspend fun <T> withContext(mdcCollection: Collection<MDC>, context: Context?, block: suspend CoroutineScope.() -> T): T {
        var newContext = context ?: Context.empty()

        mdcCollection.forEach {
            newContext = newContext.put(it.contextKey, it.map)
        }

        return kotlinx.coroutines.withContext(newContext.asCoroutineContext()) {
            block()
        }
    }
}
