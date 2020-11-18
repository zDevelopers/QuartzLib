package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.arguments.primitive.IntegerTypeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ArgumentTypeHandlerCollection {
    private final Map<Class<?>, ArgumentTypeHandler<?>> argumentTypeHandlerMap = new HashMap<>();

    public ArgumentTypeHandlerCollection () {
        this.registerNativeTypes();
    }

    public <T> void register(ArgumentTypeHandler<T> typeHandler)
    {
        argumentTypeHandlerMap.put(typeHandler.getResultType(), typeHandler);
    }

    public Optional<ArgumentTypeHandler<?>> findTypeHandler(Class<?> resultType) {
        return Optional.ofNullable(argumentTypeHandlerMap.get(resultType));
    }

    private void registerNativeTypes () {
        register(new ArgumentTypeHandler<>(Integer.class, new IntegerTypeHandler()));

        register(new ArgumentTypeHandler<>(String.class, s -> s));
    }
}
