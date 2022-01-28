package io.github.numichi.reactive.logger.exception;

public class AlreadyConfigurationException extends RuntimeException {
    public AlreadyConfigurationException() {
        super("DefaultValues have already configurated!");
    }
}
