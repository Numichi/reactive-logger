package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import reactor.util.context.ContextView

@Throws(ContextNotExistException::class, InvalidContextDataException::class)
suspend fun readMdc(): MDC {
    return readMdc(DefaultValues.getInstance().defaultReactorContextMdcKey)
}

@Throws(ContextNotExistException::class, InvalidContextDataException::class)
suspend fun readMdc(mdcContextKey: String): MDC {
    return readMdc(rectorContext(), mdcContextKey)
}

@Throws(ContextNotExistException::class, InvalidContextDataException::class)
fun readMdc(contextView: ContextView?): MDC {
    return readMdc(contextView, DefaultValues.getInstance().defaultReactorContextMdcKey)
}

@Throws(ContextNotExistException::class, InvalidContextDataException::class)
fun readMdc(contextView: ContextView?, mdcContextKey: String): MDC {
    requireNotNull(contextView) { "contentView must not be null" }

    val mdc = MDC(mdcContextKey)

    try {
        val map: Map<String, String> = contextView.get(mdcContextKey)
        mdc.putAll(map)
    } catch (noSuchException: NoSuchElementException) {
        throw ContextNotExistException("\"$mdcContextKey\" context not found", noSuchException)
    } catch (exception: Exception) {
        throw InvalidContextDataException(exception)
    }

    return mdc
}

suspend fun readMdcOrNull(): MDC? {
    return readMdcOrNull(DefaultValues.getInstance().defaultReactorContextMdcKey)
}

suspend fun readMdcOrNull(mdcContextKey: String): MDC? {
    return readMdcOrNull(rectorContext(), mdcContextKey)
}

fun readMdcOrNull(contextView: ContextView?): MDC? {
    return readMdcOrNull(contextView, DefaultValues.getInstance().defaultReactorContextMdcKey)
}

fun readMdcOrNull(contextView: ContextView?, mdcContextKey: String): MDC? {
    return readMDCResult(contextView, mdcContextKey).getOrNull()
}

fun readMDCResult(contextView: ContextView?, mdcContextKey: String): Result<MDC> {
    return runCatching { readMdc(contextView, mdcContextKey) }
}