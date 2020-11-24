package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandManager {
    private final Map<String, CommandNode> rootCommands = new HashMap<>();
    private final ArgumentTypeHandlerCollection typeHandlerCollection = new ArgumentTypeHandlerCollection();

    public <T> void registerCommand(String name, Class<T> commandType, Supplier<T> commandClassSupplier) {
        CommandGroup group = new CommandGroup(commandType, commandClassSupplier, name, typeHandlerCollection);
        rootCommands.put(name, group);
    }

    public void run(String commandName, String... args) throws CommandException {
        ((CommandGroup) rootCommands.get(commandName)).run(args); // TODO
    }
}
