package fr.zcraft.quartzlib.components.commands;

class SenderTypeWrapper<T> {
    private final Class<T> resultType;
    private final SenderType<T> typeHandler;

    public SenderTypeWrapper(Class<T> resultType, SenderType<T> typeHandler) {
        this.resultType = resultType;
        this.typeHandler = typeHandler;
    }

    public SenderType<T> getTypeHandler() {
        return typeHandler;
    }

    public Class<T> getResultType() {
        return resultType;
    }
}
