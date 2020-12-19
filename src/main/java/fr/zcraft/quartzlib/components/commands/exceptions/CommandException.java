package fr.zcraft.quartzlib.components.commands.exceptions;

import fr.zcraft.quartzlib.components.rawtext.RawText;
import org.bukkit.command.CommandSender;

public abstract class CommandException extends Exception {
    public abstract RawText display(CommandSender sender);
}
