package io.github.numichi.reactive.logger.coroutine

import reactor.util.context.ContextView
import kotlin.coroutines.CoroutineContext

typealias CCElement = CoroutineContext.Element
typealias CCKey<E> = CoroutineContext.Key<E>

/**
 * You can also define your custom context. Since this is not standard, so must be a defined mechanism to resolve it.
 *
 * Example custom context:
 *
 * @see <a href="https://github.com/Kotlin/kotlinx.coroutines/blob/master/integration/kotlinx-coroutines-slf4j/src/MDCContext.kt">MDCContext.kt</a>
 */
typealias CCResolveFn<E> = suspend (CCKey<out E>) -> ContextView?
