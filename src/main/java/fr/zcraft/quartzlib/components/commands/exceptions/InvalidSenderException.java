package fr.zcraft.quartzlib.components.commands.exceptions;

import fr.zcraft.quartzlib.components.rawtext.RawText;
import org.bukkit.command.CommandSender;

public class InvalidSenderException extends CommandException {
    @Override
    public RawText display(CommandSender sender) {
        return null;
    }
}