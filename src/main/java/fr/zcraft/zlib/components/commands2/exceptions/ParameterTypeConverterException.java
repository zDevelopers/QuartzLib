package fr.zcraft.zlib.components.commands2.exceptions;

import java.util.Optional;

public class ParameterTypeConverterException extends Exception {
    private final String parseErrorMessage;
    private final Optional<Throwable> cause;

    public ParameterTypeConverterException(String parseErrorMessage) {
        this(parseErrorMessage, Optional.empty());
    }

    public ParameterTypeConverterException(String parseErrorMessage, Throwable cause) {
        this(parseErrorMessage, Optional.of(cause));
    }

    public ParameterTypeConverterException(String parseErrorMessage, Optional<Throwable> cause) {
        this.parseErrorMessage = parseErrorMessage;
        this.cause = cause;
    }

    public String getParseErrorMessage() {
        return parseErrorMessage;
    }

    public Optional<Throwable> getParseErrorCause() {
        return cause;
    }
}
