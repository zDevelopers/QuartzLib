package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import fr.zcraft.quartzlib.components.commands.exceptions.MissingSubcommandException;
import fr.zcraft.quartzlib.components.commands.exceptions.UnknownSubcommandException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class CommandGroup extends CommandNode {
    private final Class<?> commandGroupClass;

    @Nullable
    private final Supplier<?> classInstanceSupplier;
    @Nullable
    private final GroupClassInstanceSupplier groupClassInstanceSupplier;

    private final Map<String, CommandNode> subCommands = new HashMap<>();

    CommandGroup(Class<?> commandGroupClass, Supplier<?> classInstanceSupplier, String name,
                 TypeCollection typeCollection) {
        this(commandGroupClass, classInstanceSupplier, null, name, typeCollection, null);
    }

    CommandGroup(Class<?> commandGroupClass, GroupClassInstanceSupplier classInstanceSupplier, String name,
                 CommandGroup parent, TypeCollection typeCollection) {
        this(commandGroupClass, null, classInstanceSupplier, name, typeCollection, parent);
    }

    CommandGroup(CommandGroup parent, Field backingField, TypeCollection typeCollection) {
        this(
                backingField.getType(),
                GroupClassInstanceSupplier.backingField(backingField),
                backingField.getName(),
                parent,
                typeCollection
        );
    }

    private CommandGroup(
            Class<?> commandGroupClass,
            @Nullable Supplier<?> classInstanceSupplier,
            @Nullable GroupClassInstanceSupplier groupClassInstanceSupplier, String name,
            TypeCollection typeCollection, CommandGroup parent) {
        super(name, parent);
        this.commandGroupClass = commandGroupClass;
        this.classInstanceSupplier = classInstanceSupplier;
        this.groupClassInstanceSupplier = groupClassInstanceSupplier;
        DiscoveryUtils.getCommandMethods(commandGroupClass, typeCollection).forEach(this::addMethod);
        DiscoveryUtils.getSubCommands(this, typeCollection).forEach(this::addSubCommand);
    }

    public Collection<CommandNode> getSubCommands() {
        return this.subCommands.values();
    }

    @Nullable public CommandNode getSubCommand(String subCommandName) {
        return this.subCommands.get(subCommandName);
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

    private void addSubCommand(CommandGroup commandGroup) {
        subCommands.put(commandGroup.getName(), commandGroup);
    }

    void run(CommandSender sender, String... args) throws CommandException {
        if (classInstanceSupplier == null) {
            throw new IllegalStateException("This command group comes from a parent and cannot instanciate itself.");
        }

        Object commandObject = classInstanceSupplier.get();
        runSelf(commandObject, sender, args);
    }

    @Override
    void run(Object parentInstance, CommandSender sender, String[] args) throws CommandException {
        if (this.groupClassInstanceSupplier == null) {
            throw new IllegalStateException("This command group cannot be ran from a parent");
        }

        Object instance = this.groupClassInstanceSupplier.supply(parentInstance);
        runSelf(instance, sender, args);
    }

    private void runSelf(Object instance, CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new MissingSubcommandException(this);
        }

        CommandNode subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            throw new UnknownSubcommandException(this, args[0]);
        }

        subCommand.run(instance, sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public Class<?> getCommandGroupClass() {
        return commandGroupClass;
    }
}
