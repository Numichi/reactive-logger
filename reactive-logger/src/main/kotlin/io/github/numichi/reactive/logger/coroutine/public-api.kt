package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.internal.reactorContextOrEmpty
import io.github.numichi.reactive.logger.coroutine.internal.withContextBlock
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.github.numichi.reactive.logger.hook.mdcReferenceContentLoad
import io.github.numichi.reactive.logger.internal.toSafeMdcMap
import kotlinx.coroutines.CoroutineScope
import reactor.util.context.Context
import reactor.util.context.ContextView

//region readMdc / readOrDefaultMdc
suspend fun readMdc(): MDC {
    return readMdc(Configuration.defaultReactorContextMdcKey)
}

suspend fun readMdc(contextKey: Any): MDC {
    return readMdc(reactorContextOrEmpty(), contextKey)
}

fun readMdc(contextView: ContextView): MDC {
    return readMdc(contextView, Configuration.defaultReactorContextMdcKey)
}

fun readMdc(
    contextView: ContextView,
    contextKey: Any,
): MDC {
    return try {
        val mdcMap = contextView.get<Map<Any?, Any?>>(contextKey).toSafeMdcMap()
        MDC(contextKey, mdcMap)
    } catch (e: ClassCastException) {
        throw ReadException("The content type is not java.util.Map<Object, Object>", e)
    } catch (e: NoSuchElementException) {
        throw ReadException("$contextKey context key is not contain in context", e)
    }
}

suspend fun readOrDefaultMdc(): MDC {
    return readOrDefaultMdc(Configuration.defaultReactorContextMdcKey)
}

suspend fun readOrDefaultMdc(contextKey: Any): MDC {
    return readOrDefaultMdc(reactorContextOrEmpty(), contextKey)
}

fun readOrDefaultMdc(contextView: ContextView): MDC {
    return readOrDefaultMdc(contextView, Configuration.defaultReactorContextMdcKey)
}

fun readOrDefaultMdc(
    contextView: ContextView,
    contextKey: Any,
): MDC {
    return try {
        readMdc(contextView, contextKey)
    } catch (e: ReadException) {
        if (e.cause is ClassCastException) {
            throw e
        }

        MDC(contextKey)
    }
}
//endregion

//region snapshot
suspend fun snapshotMdc(): MDC {
    return snapshotMdc(Configuration.defaultReactorContextMdcKey)
}

suspend fun snapshotMdc(contextKey: Any): MDC {
    return snapshotMdc(reactorContextOrEmpty(), contextKey)
}

fun snapshotMdc(contextView: ContextView): MDC {
    return snapshotMdc(contextView, Configuration.defaultReactorContextMdcKey)
}

fun snapshotMdc(
    contextView: ContextView,
    contextKey: Any,
): MDC {
    return mdcReferenceContentLoad(contextView, MDC(contextKey))
}
//endregion

//region withMDCContext
suspend fun <T> withMDCContext(
    vararg mdc: MDC,
    block: suspend CoroutineScope.() -> T,
): T {
    return withContextBlock(reactorContextOrEmpty(), mdc.iterator(), block)
}

suspend fun <T> withMDCContext(
    context: Context,
    vararg mdc: MDC,
    block: suspend CoroutineScope.() -> T,
): T {
    return withContextBlock(context, mdc.iterator(), block)
}
//endregion
