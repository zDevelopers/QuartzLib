package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.arguments.generic.EnumArgumentType;
import fr.zcraft.quartzlib.components.commands.arguments.primitive.IntegerArgumentType;
import fr.zcraft.quartzlib.components.commands.senders.GenericCommandSender;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class TypeCollection {
    private final Map<Class<?>, ArgumentTypeWrapper<?>> argumentTypeMap = new HashMap<>();
    private final List<GenericArgumentType<?>> genericArgumentTypes = new ArrayList<>();

    private final Map<Class<?>, SenderTypeWrapper<?>> senderTypeMap = new HashMap<>();
    private final List<GenericSenderType<?>> genericSenderTypes = new ArrayList<>();

    public TypeCollection() {
        this.registerNativeTypes();
    }

    public <T> void register(ArgumentTypeWrapper<T> typeHandler) {
        argumentTypeMap.put(typeHandler.getResultType(), typeHandler);
    }

    public <T> void register(GenericArgumentType<T> genericArgumentType) {
        genericArgumentTypes.add(genericArgumentType);
    }

    public <T> void register(SenderTypeWrapper<T> typeHandler) {
        senderTypeMap.put(typeHandler.getResultType(), typeHandler);
    }

    public <T> void register(GenericSenderType<T> genericSenderType) {
        genericSenderTypes.add(genericSenderType);
    }

    public Optional<ArgumentTypeWrapper<?>> findArgumentType(Class<?> resultType) {
        ArgumentTypeWrapper<?> typeHandler = argumentTypeMap.get(resultType);
        if (typeHandler != null) {
            return Optional.of(typeHandler);
        }
        return this.findGenericArgumentType(resultType);
    }

    private <T> Optional<ArgumentTypeWrapper<?>> findGenericArgumentType(Class<T> resultType) {
        for (GenericArgumentType<?> t : genericArgumentTypes) {
            Optional<? extends ArgumentType<?>> matchingArgumentType = t.getMatchingArgumentType(resultType);

            if (matchingArgumentType.isPresent()) {
                ArgumentTypeWrapper<?> typeHandler =
                        new ArgumentTypeWrapper<>(resultType, (ArgumentType<T>) matchingArgumentType.get());
                return Optional.of(typeHandler);
            }
        }
        return Optional.empty();
    }

    private void registerNativeTypes() {
        // Primitive types
        register(new ArgumentTypeWrapper<>(Integer.class, new IntegerArgumentType()));
        register(new ArgumentTypeWrapper<>(String.class, s -> s));

        // Generic types
        register(new EnumArgumentType());

        // Generic sender types
        register(new GenericCommandSender());
    }


    public Optional<SenderTypeWrapper<?>> findSenderType(Class<?> resultType) {
        SenderTypeWrapper<?> typeHandler = senderTypeMap.get(resultType);
        if (typeHandler != null) {
            return Optional.of(typeHandler);
        }
        return this.findGenericSenderType(resultType);
    }

    private <T> Optional<SenderTypeWrapper<?>> findGenericSenderType(Class<T> resultType) {
        for (GenericSenderType<?> t : genericSenderTypes) {
            Optional<? extends SenderType<?>> matchingSenderType = t.getMatchingSenderType(resultType);

            if (matchingSenderType.isPresent()) {
                SenderTypeWrapper<?> typeHandler =
                        new SenderTypeWrapper<>(resultType, (SenderType<T>) matchingSenderType.get());
                return Optional.of(typeHandler);
            }
        }
        return Optional.empty();
    }
}
