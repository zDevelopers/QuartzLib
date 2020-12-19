package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public abstract class CommandNode {
    private final String name;
    @Nullable private final CommandGroup parent;

    protected CommandNode(String name, @Nullable CommandGroup parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    @Nullable public CommandGroup getParent() {
        return parent;
    }

    abstract void run(Object parentInstance, CommandSender sender, String[] args) throws CommandException;
}
