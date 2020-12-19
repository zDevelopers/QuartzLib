package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.InvalidSenderException;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface SenderType<T> {
    T parse(CommandSender raw) throws InvalidSenderException;
}
