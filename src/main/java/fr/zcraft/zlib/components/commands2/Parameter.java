package fr.zcraft.zlib.components.commands2;

public class Parameter extends Field {
    Parameter(Class<?> parameterType, java.lang.reflect.Field runnableField, ParameterTypeConverter<?> typeConverter, String name, String about, boolean isRequired) {
        super(parameterType, runnableField, typeConverter, name, about, isRequired);
    }
}
