package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;

@FunctionalInterface
public interface ArgumentType<T> {
    T parse(String raw) throws ArgumentParseException;

    default boolean isValid(String raw) {
        try {
            parse(raw);
            return true;
        } catch (ArgumentParseException ignored) {
            return false;
        }
    }
}
