package fr.zcraft.quartzlib.components.commands;

import java.util.Optional;

public interface GenericSenderType<T> {
    Optional<SenderType<T>> getMatchingSenderType(Class<?> type);
}
