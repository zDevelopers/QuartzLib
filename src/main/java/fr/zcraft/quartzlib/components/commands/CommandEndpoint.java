package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;

import java.util.ArrayList;
import java.util.List;

class CommandEndpoint extends CommandNode {
    private final List<CommandMethod> methods = new ArrayList<>();

    CommandEndpoint(String name) {
        super(name, null);
    }

    @Override
    void run(Object instance, String[] args) throws CommandException {
        this.methods.get(0).run(instance, args);
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }
}
