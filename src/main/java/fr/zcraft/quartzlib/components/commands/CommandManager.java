package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandManager {
    private final Map<String, CommandNode> rootCommands = new HashMap<>();
    private final TypeCollection typeCollection = new TypeCollection();

    public <T> void registerCommand(String name, Class<T> commandType, Supplier<T> commandClassSupplier) {
        CommandGroup group = new CommandGroup(commandType, commandClassSupplier, name, typeCollection);
        rootCommands.put(name, group);
    }

    public void run(CommandSender sender, String commandName, String... args) throws CommandException {
        ((CommandGroup) rootCommands.get(commandName)).run(sender, args); // TODO
    }
}
