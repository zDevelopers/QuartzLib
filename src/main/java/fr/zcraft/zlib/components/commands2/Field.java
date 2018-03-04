package fr.zcraft.zlib.components.commands2;

public class Field<T> {
    private final Class<T> parameterType;
    private final java.lang.reflect.Field runnableField;
    private final ParameterTypeConverter<T> typeConverter;

    private final String name;
    private final String about;
    private final boolean isRequired;

    Field(Class<T> parameterType, java.lang.reflect.Field runnableField, ParameterTypeConverter<T> typeConverter, String name, String about, boolean isRequired) {
        this.parameterType = parameterType;
        this.runnableField = runnableField;
        this.typeConverter = typeConverter;

        this.name = name;
        this.about = about;
        this.isRequired = isRequired;
    }

    public Class<T> getParameterType() {
        return parameterType;
    }

    public ParameterTypeConverter<T> getTypeConverter() {
        return typeConverter;
    }

    public java.lang.reflect.Field getRunnableField() {
        return runnableField;
    }

    public String getName() {
        return name;
    }

    public String getAbout() {
        return about;
    }

    public boolean isRequired() {
        return isRequired;
    }
}