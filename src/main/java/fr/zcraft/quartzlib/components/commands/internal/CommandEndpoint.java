package fr.zcraft.quartzlib.components.commands.internal;

import java.util.ArrayList;
import java.util.List;

class CommandEndpoint extends CommandNode {
    private final List<CommandMethod> methods = new ArrayList<>();

    CommandEndpoint(String name) {
        super(name);
    }

    void addMethod(CommandMethod method) {
        methods.add(method);
    }
}
