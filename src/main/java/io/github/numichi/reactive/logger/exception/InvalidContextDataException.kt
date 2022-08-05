package io.github.numichi.reactive.logger.exception

class InvalidContextDataException(throwable: Throwable) : RuntimeException("Reached data is not Map<String, String>", throwable)
