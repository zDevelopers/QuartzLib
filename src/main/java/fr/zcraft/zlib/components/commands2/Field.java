package fr.zcraft.zlib.components.commands2;

class Field {
    private final Class<?> parameterType;
    private final java.lang.reflect.Field runnableField;
    private final ParameterTypeConverter<?> typeConverter;

    private final String name;
    private final String about;
    private final boolean isRequired;

    Field(Class<?> parameterType, java.lang.reflect.Field runnableField, ParameterTypeConverter<?> typeConverter, String name, String about, boolean isRequired) {
        this.parameterType = parameterType;
        this.runnableField = runnableField;
        this.typeConverter = typeConverter;

        this.name = name;
        this.about = about;
        this.isRequired = isRequired;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public ParameterTypeConverter<?> getTypeConverter() {
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