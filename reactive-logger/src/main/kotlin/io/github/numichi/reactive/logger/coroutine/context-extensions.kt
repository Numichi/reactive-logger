package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.MDC
import reactor.util.context.Context
import reactor.util.context.ContextView

//region modify / merge
fun Context.modifyMdc(modify: (MDC) -> MDC): Context {
    return this.merge(modify(readOrDefaultMdc(this)))
}

fun Context.modifyMdc(modify: Map<String, String?>): Context {
    return this.merge(readOrDefaultMdc(this).plus(modify))
}

fun Context.modifyMdc(contextKey: Any, modify: (MDC) -> MDC): Context {
    return this.merge(modify(readOrDefaultMdc(this, contextKey)))
}

fun Context.modifyMdc(contextKey: Any, modify: Map<String, String?>): Context {
    val mdc = readOrDefaultMdc(this, contextKey).plus(modify)
    return put(mdc.contextKey, mdc.data)
}

fun Context.merge(mdc: MDC): Context {
    return this.modifyMdc(mdc.contextKey, mdc.data)
}
//endregion

//region getMdc / getOrDefaultMdc
fun ContextView.getMdc(): MDC {
    return readMdc(this)
}

fun ContextView.getMdc(contextKey: String): MDC {
    return readMdc(this, contextKey)
}

fun Context.getMdc(): MDC {
    return this.readOnly().getMdc()
}

fun Context.getMdc(contextKey: String): MDC {
    return this.readOnly().getMdc(contextKey)
}

fun ContextView.getOrDefaultMdc(): MDC {
    return readOrDefaultMdc(this)
}

fun ContextView.getOrDefaultMdc(contextKey: String): MDC {
    return readOrDefaultMdc(this, contextKey)
}

fun Context.getOrDefaultMdc(): MDC {
    return this.readOnly().getOrDefaultMdc()
}

fun Context.getOrDefaultMdc(contextKey: String): MDC {
    return this.readOnly().getOrDefaultMdc(contextKey)
}
//endregion

//region snapshotMdc
fun ContextView.snapshotMdc(): MDC {
    return snapshotMdc(this)
}

fun ContextView.snapshotMdc(contextKey: String): MDC {
    return snapshotMdc(this, contextKey)
}

fun Context.snapshotMdc(): MDC {
    return this.readOnly().snapshotMdc()
}

fun Context.snapshotMdc(contextKey: String): MDC {
    return this.readOnly().snapshotMdc(contextKey)
}
//endregion