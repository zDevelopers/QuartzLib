package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

class CommandEndpoint extends CommandNode {
    private final List<CommandMethod> methods = new ArrayList<>();

    CommandEndpoint(String name) {
        super(name, null);
    }

    @Override
    void run(Object parentInstance, CommandSender sender, String[] args) throws CommandException {
        CommandMethod method = findMatchingMethod(args);
        method.run(parentInstance, sender, args);
    }

    @Nullable CommandMethod findMatchingMethod(String[] args) {
        return this.methods.stream().filter(m -> m.getArguments().length == args.length).findFirst().orElse(null);
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }
}
