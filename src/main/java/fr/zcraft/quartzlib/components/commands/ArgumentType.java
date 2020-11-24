package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;

@FunctionalInterface
public interface ArgumentType<T> {
    T parse(String raw) throws ArgumentParseException;
}
