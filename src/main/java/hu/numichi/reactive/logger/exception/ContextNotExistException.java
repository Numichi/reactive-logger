package hu.numichi.reactive.logger.exception;

public class ContextNotExistException extends RuntimeException {
    public ContextNotExistException(String message) {
        super(message);
    }
}
