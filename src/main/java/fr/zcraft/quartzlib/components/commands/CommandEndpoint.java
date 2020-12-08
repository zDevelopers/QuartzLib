package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

class CommandEndpoint extends CommandNode {
    private final List<CommandMethod> methods = new ArrayList<>();

    CommandEndpoint(String name) {
        super(name, null);
    }

    @Override
    void run(Object instance, CommandSender sender, String[] args) throws CommandException {
        this.methods.get(0).run(instance, sender, args);
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }
}
