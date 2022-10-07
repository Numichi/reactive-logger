package io.github.numichi.reactive.logger.exceptions

class ReadException(override val message: String, override val cause: Throwable) : RuntimeException()