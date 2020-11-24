package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.arguments.generic.EnumArgumentType;
import fr.zcraft.quartzlib.components.commands.arguments.primitive.IntegerArgumentType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class ArgumentTypeHandlerCollection {
    private final Map<Class<?>, ArgumentTypeHandler<?>> argumentTypeHandlerMap = new HashMap<>();
    private final List<GenericArgumentType<?>> genericArgumentTypes = new ArrayList<>();

    public ArgumentTypeHandlerCollection () {
        this.registerNativeTypes();
    }

    public <T> void register(ArgumentTypeHandler<T> typeHandler)
    {
        argumentTypeHandlerMap.put(typeHandler.getResultType(), typeHandler);
    }

    public <T> void register(GenericArgumentType<T> genericArgumentType) {
        genericArgumentTypes.add(genericArgumentType);
    }

    public Optional<ArgumentTypeHandler<?>> findTypeHandler(Class<?> resultType) {
        ArgumentTypeHandler<?> typeHandler = argumentTypeHandlerMap.get(resultType);
        if (typeHandler != null) return Optional.of(typeHandler);
        return this.findGenericTypeHandler(resultType);
    }

    private <T> Optional<ArgumentTypeHandler<?>> findGenericTypeHandler(Class<T> resultType) {
        for (GenericArgumentType<?> t : genericArgumentTypes) {
            Optional<? extends ArgumentType<?>> matchingArgumentType = t.getMatchingArgumentType(resultType);

            if (matchingArgumentType.isPresent()) {
                ArgumentTypeHandler<?> typeHandler = new ArgumentTypeHandler<>(resultType, (ArgumentType<T>) matchingArgumentType.get());
                return Optional.of(typeHandler);
            }
        }
        return Optional.empty();
    }

    private void registerNativeTypes () {
        // Primitive types
        register(new ArgumentTypeHandler<>(Integer.class, new IntegerArgumentType()));
        register(new ArgumentTypeHandler<>(String.class, s -> s));

        // Generic types
        register(new EnumArgumentType());
    }
}
