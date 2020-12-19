package fr.zcraft.quartzlib.components.commands;

class ArgumentTypeWrapper<T> {
    private final Class<T> resultType;
    private final ArgumentType<T> typeHandler;

    public ArgumentTypeWrapper(Class<T> resultType, ArgumentType<T> typeHandler) {
        this.resultType = resultType;
        this.typeHandler = typeHandler;
    }

    public ArgumentType<T> getTypeHandler() {
        return typeHandler;
    }

    public Class<T> getResultType() {
        return resultType;
    }
}
