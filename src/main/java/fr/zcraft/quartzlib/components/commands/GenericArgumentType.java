package fr.zcraft.quartzlib.components.commands;

import java.util.Optional;

public interface GenericArgumentType<T> {
    Optional<ArgumentType<T>> getMatchingArgumentType(Class<?> type);
}
