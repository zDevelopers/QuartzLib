package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import fr.zcraft.quartzlib.tools.text.RawMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class QuartzCommandExecutor implements CommandExecutor {
    private final CommandGroup group;

    public QuartzCommandExecutor(CommandGroup group) {
        this.group = group;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        try {
            group.run(sender, args);
        } catch (CommandException e) {
            RawMessage.send(sender, e.display(sender).build());
        }
        return true;
    }
}
