package io.github.numichi.reactive.logger.exception;

public class InvalidContextDataException extends RuntimeException {
    public InvalidContextDataException(Throwable throwable) {
        super("Reached data is not Map<String, String>", throwable);
    }
}
