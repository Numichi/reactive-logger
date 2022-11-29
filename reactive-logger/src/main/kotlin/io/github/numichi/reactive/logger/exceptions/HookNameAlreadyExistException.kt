package io.github.numichi.reactive.logger.exceptions

import io.github.numichi.reactive.logger.hook.MDCHook

class HookNameAlreadyExistException(hook: MDCHook<*>) : RuntimeException(
    "The name \"${hook.name}\" already exists. Order=${hook.order} ContextKey=${hook.contextKey}"
)

class ContextHookNameAlreadyExistException(string: String) : RuntimeException(string)