package fr.zcraft.zlib.components.commands2.exceptions;

public class CommandConfigurationException extends RuntimeException {
    private final Class<?> runnableClass;
    private final String errorMessage;

    public CommandConfigurationException(Class<?> runnableClass, String errorMessage) {
        this.runnableClass = runnableClass;
        this.errorMessage = errorMessage;
    }

    public Class<?> getRunnableClass() {
        return runnableClass;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
