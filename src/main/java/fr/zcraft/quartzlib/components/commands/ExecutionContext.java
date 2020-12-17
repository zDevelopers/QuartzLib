package fr.zcraft.quartzlib.components.commands;

import org.bukkit.command.CommandSender;

public class ExecutionContext {
    private final CommandSender sender;
    private final String[] fullArgs;

    public ExecutionContext(CommandSender sender, String[] fullArgs) {
        this.sender = sender;
        this.fullArgs = fullArgs;
    }
}
