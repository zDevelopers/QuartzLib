package fr.zcraft.quartzlib.components.commands;

@FunctionalInterface
public interface ArgumentType<T> {
    T parse(String raw);
}
