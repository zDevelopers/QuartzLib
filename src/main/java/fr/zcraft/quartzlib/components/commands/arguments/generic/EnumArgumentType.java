package fr.zcraft.quartzlib.components.commands.arguments.generic;

import fr.zcraft.quartzlib.components.commands.ArgumentType;
import fr.zcraft.quartzlib.components.commands.GenericArgumentType;
import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnumArgumentType implements GenericArgumentType<Enum<?>> {
    private static Map<String, Enum<?>> getEnumValues(Class<?> enumClass) {
        Map<String, Enum<?>> enumValues = new HashMap<>();

        Arrays.stream(enumClass.getDeclaredFields())
                .filter(f -> Modifier.isPublic(f.getModifiers())
                        && Modifier.isStatic(f.getModifiers())
                        && enumClass.isAssignableFrom(f.getType()))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        enumValues.put(f.getName().toLowerCase(), (Enum<?>) f.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        return enumValues;
    }

    @Override
    public Optional<ArgumentType<Enum<?>>> getMatchingArgumentType(Class<?> type) {
        if (type.isEnum()) {
            return Optional.of(new DiscreteEnumArgumentType(type));
        }
        return Optional.empty();
    }

    private static class DiscreteEnumArgumentType implements ArgumentType<Enum<?>> {
        private final Map<String, Enum<?>> enumValues;

        public DiscreteEnumArgumentType(Class<?> enumClass) {
            enumValues = getEnumValues(enumClass);
        }

        @Override
        public Enum<?> parse(String raw) throws ArgumentParseException {
            Enum<?> value = enumValues.get(raw);
            if (value == null) {
                throw new EnumParseException();
            }
            return value;
        }
    }

    private static class EnumParseException extends ArgumentParseException {

    }
}
