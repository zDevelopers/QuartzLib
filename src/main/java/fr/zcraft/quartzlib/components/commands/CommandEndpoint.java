package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

class CommandEndpoint extends CommandNode {
    private final SortedSet<CommandMethod> methods = new TreeSet<>();

    CommandEndpoint(String name) {
        super(name, null);
    }

    @Override
    void run(Object parentInstance, CommandSender sender, String[] args) throws CommandException {
        for (CommandMethod method : this.methods) {
            if (method.getParameters().length != args.length) {
                continue; // TODO
            }

            try {
                Object[] parsedArgs;
                parsedArgs = method.parseArguments(sender, args);
                method.run(parentInstance, parsedArgs);
                return;
            } catch (CommandException ignored) { // TODO

            }
        }

        throw new RuntimeException("No matching command found"); // TODO
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }

    @NotNull public Iterable<CommandMethod> getMethods() {
        return methods;
    }
}
