package fr.zcraft.zlib.components.commands2.exceptions;

import java.util.Optional;

public class InvalidArgumentException extends ArgumentException {
    private final String parseErrorMessage;
    private final Optional<Throwable> parseErrorCause;

    public InvalidArgumentException(ParameterTypeConverterException sourceException, String argument, int position) {
        super(argument, position);
        this.parseErrorMessage = sourceException.getParseErrorMessage();
        this.parseErrorCause = sourceException.getParseErrorCause();
    }

    public String getParseErrorMessage() {
        return parseErrorMessage;
    }

    public Optional<Throwable> getParseErrorCause() {
        return parseErrorCause;
    }
}
