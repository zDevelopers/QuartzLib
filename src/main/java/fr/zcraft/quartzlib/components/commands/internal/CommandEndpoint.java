package fr.zcraft.quartzlib.components.commands.internal;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

class CommandEndpoint extends CommandNode {
    private final List<CommandMethod> methods = new ArrayList<>();

    CommandEndpoint(String name) {
        super(name, null);
    }

    @Override
    void run(Object instance, String[] args) {
        this.methods.get(0).run(instance, args);
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }
}
