package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import fr.zcraft.quartzlib.core.QuartzLib;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class CommandManager {
    private final Map<String, CommandNode> rootCommands = new HashMap<>();
    private final TypeCollection typeCollection = new TypeCollection();

    public <T> void addCommand(String name, Class<T> commandType, Supplier<T> commandClassSupplier) {
        CommandGroup group = new CommandGroup(commandType, commandClassSupplier, name, typeCollection);
        rootCommands.put(name, group);
    }

    public <T> void registerCommand(String name, Class<T> commandType, Supplier<T> commandClassSupplier) {
        CommandGroup group = new CommandGroup(commandType, commandClassSupplier, name, typeCollection);
        rootCommands.put(name, group);
        registerCommand(group);
    }

    private void registerCommand(CommandGroup group) {
        PluginCommand command = QuartzLib.getPlugin().getCommand(group.getName());
        // TODO: handle null here
        Objects.requireNonNull(command).setExecutor(new QuartzCommandExecutor(group));
    }

    public void run(CommandSender sender, String commandName, String... args) throws CommandException {
        ((CommandGroup) rootCommands.get(commandName)).run(sender, args); // TODO
    }
}
