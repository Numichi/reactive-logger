package hu.numichi.reactive.logger.kotlin;

import hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import hu.numichi.reactive.logger.MDC
import hu.numichi.reactive.logger.exception.ContextNotExistException
import hu.numichi.reactive.logger.exception.InvalidContextDataException
import kotlinx.coroutines.reactor.ReactorContext
import reactor.util.context.Context
import reactor.util.context.ContextView
import kotlin.coroutines.coroutineContext
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
        return read(coroutineContext[ReactorContext]?.context, mdcContextKey)
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
}
