package fr.zcraft.zlib.components.commands2.exceptions;

import java.lang.reflect.Field;

public class UnhandledParameterType extends CommandConfigurationException {
    private final Class<?> type;
    private final Field field;

    public UnhandledParameterType(Class<?> type, Field field) {
        super(field.getDeclaringClass(), "");
        this.type = type;
        this.field = field;
    }

    public Class<?> getType() {
        return type;
    }

    private Field getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return "Type error in " + field.getDeclaringClass().getName() + "." + field.getName() + " : no parameter type converters found for type " + type.getName();
    }
}
