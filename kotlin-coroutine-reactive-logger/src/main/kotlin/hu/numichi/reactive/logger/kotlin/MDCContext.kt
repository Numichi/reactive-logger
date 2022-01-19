package hu.numichi.reactive.logger.kotlin;

import hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import hu.numichi.reactive.logger.MDC
import hu.numichi.reactive.logger.exception.ContextNotExistException
import hu.numichi.reactive.logger.exception.InvalidContextDataException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.jvm.Throws

object MDCContext {
    fun put(context: Context, mdc: Map<String, String>): Context {
        return context.put(DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc)
    }

    fun put(context: Context, mdcContextKey: String, mdc: Map<String, String>): Context {
        return context.put(mdcContextKey, mdc)
    }

    fun put(context: Context, mdc: MDC): Context {
        return context.put(mdc.contextKey, mdc.map)
    }

    @Throws(InvalidContextDataException::class)
    suspend fun read(): MDC {
        return read(DEFAULT_REACTOR_CONTEXT_MDC_KEY)
    }

    @Throws(InvalidContextDataException::class)
    suspend fun read(mdcContextKey: String): MDC {
        return read(rectorContext(), mdcContextKey)
    }

    @Throws(InvalidContextDataException::class, ContextNotExistException::class)
    fun read(context: Context): MDC {
        return read(context, DEFAULT_REACTOR_CONTEXT_MDC_KEY)
    }

    @Throws(InvalidContextDataException::class, ContextNotExistException::class)
    fun read(contextView: ContextView?, mdcContextKey: String): MDC {
        if (contextView == null) {
            throw ContextNotExistException();
        }

        val mdc = MDC(mdcContextKey)

        try {
            val map = contextView.get<Map<String, String>>(mdcContextKey)
            mdc.putAll(map)
        } catch (exception: ClassCastException) {
            throw InvalidContextDataException(exception)
        } catch (exception: NullPointerException) {
            throw InvalidContextDataException(exception)
        }

        return mdc
    }

    suspend fun <T> withReactorContext(mdc: MDC, block: suspend CoroutineScope.() -> T): T {
        return withContext(listOf(mdc), rectorContext(), block)
    }

    suspend fun <T> withContext(mdc: MDC, context: Context, block: suspend CoroutineScope.() -> T): T {
        return withContext(listOf(mdc), context, block)
    }

    suspend fun <T> withReactorContext(mdc0: MDC, mdc1: MDC, block: suspend CoroutineScope.() -> T): T {
        return withContext(listOf(mdc0, mdc1), rectorContext(), block)
    }

    suspend fun <T> withContext(mdc0: MDC, mdc1: MDC, context: Context, block: suspend CoroutineScope.() -> T): T {
        return withContext(listOf(mdc0, mdc1), context, block)
    }

    suspend fun <T> withContext(mdcCollection: Collection<MDC>, context: Context?, block: suspend CoroutineScope.() -> T): T {
        var newContext = context ?: Context.empty()

        mdcCollection.forEach {
            newContext = newContext.delete(it.contextKey)
            newContext = newContext.put(it.contextKey, it.map)
        }

        return kotlinx.coroutines.withContext(newContext.asCoroutineContext()) {
            block()
        }
    }
}
