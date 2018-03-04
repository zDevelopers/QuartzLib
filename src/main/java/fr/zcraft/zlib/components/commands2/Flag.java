package fr.zcraft.zlib.components.commands2;

class Flag<T> extends Field<T> {
    private final boolean hasValue;
    private final String shortName;

    Flag(Class<T> parameterType, java.lang.reflect.Field runnableField, ParameterTypeConverter<T> typeConverter, String name, String shortName, String about, boolean isRequired, boolean hasValue) {
        super(parameterType, runnableField, typeConverter, name, about, isRequired);
        this.hasValue = hasValue;
        this.shortName = shortName;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public String getShortName() {
        return shortName;
    }
}

