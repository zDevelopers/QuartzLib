package fr.zcraft.zlib.components.commands2;

class Parameter<T> extends Field<T> {
    Parameter(Class<T> parameterType, java.lang.reflect.Field runnableField, ParameterTypeConverter<T> typeConverter, String name, String about, boolean isRequired) {
        super(parameterType, runnableField, typeConverter, name, about, isRequired);
    }
}
