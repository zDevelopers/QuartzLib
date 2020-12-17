package fr.zcraft.quartzlib.components.commands.exceptions;

import java.lang.reflect.Method;

public class UnknownArgumentTypeException extends RuntimeException {
    public UnknownArgumentTypeException(Method method, Class<?> foundType) {
        super(getErrorMessage(method, foundType));
    }

    private static String getErrorMessage(Method method, Class<?> foundType) {
        return "Found unknown command argument type: '" + foundType
                + "' (found in '" + method.toString() + "'). "
                + "Did you forget to register it to the CommandManager?";
    }
}
