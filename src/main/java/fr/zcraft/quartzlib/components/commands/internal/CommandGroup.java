package fr.zcraft.quartzlib.components.commands.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class CommandGroup extends CommandNode {
    private final Class<?> commandGroupClass;
    private final CommandGroup parent;

    private final Map<String, CommandNode> subCommands = new HashMap<>();

    public CommandGroup(Class<?> commandGroupClass, String name) {
        super(name);
        this.commandGroupClass = commandGroupClass;
        this.parent = null;
        getCommandMethods(commandGroupClass).forEach(this::addMethod);
    }

    public CommandGroup(Class<?> commandGroupClass, String name, CommandGroup parent) {
        super(name);
        this.commandGroupClass = commandGroupClass;
        this.parent = parent;
    }

    public Iterable<CommandNode> getSubCommands () {
        return this.subCommands.values();
    }


    private void addMethod(CommandMethod method) {
        // TODO: handle adding to non-endpoints
        CommandEndpoint endpoint = (CommandEndpoint) subCommands.get(method.getName());
        if (endpoint == null) {
            endpoint = new CommandEndpoint(method.getName());
            subCommands.put(endpoint.getName(), endpoint);
        }
        endpoint.addMethod(method);
    }

    // Private utils TODO: move to DiscoveryUtils?

    private static Stream<CommandMethod> getCommandMethods(Class<?> commandGroupClass) {
        return Arrays.stream(commandGroupClass.getDeclaredMethods()).map(CommandMethod::new);
    }
}
