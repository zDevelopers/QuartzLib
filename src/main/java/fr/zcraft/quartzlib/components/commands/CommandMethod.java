package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.attributes.Sender;
import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;
import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import fr.zcraft.quartzlib.components.commands.exceptions.InvalidSenderException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CommandMethod {
    @NotNull private final Method method;
    @NotNull private final String name;
    @NotNull private final CommandMethodArgument[] arguments;
    private final int parameterCount;
    @Nullable private CommandMethodSenderArgument senderArgument = null;

    CommandMethod(Method method, TypeCollection typeCollection) {
        this.method = method;
        this.name = method.getName();

        Parameter[] parameters = method.getParameters();
        List<CommandMethodArgument> arguments = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Sender.class)) { // TODO: check for multiple sender arguments
                senderArgument = new CommandMethodSenderArgument(parameter, i, typeCollection);
            } else {
                arguments.add(new CommandMethodArgument(this, parameter, i, typeCollection));
            }
        }

        this.arguments = arguments.toArray(new CommandMethodArgument[] {});
        this.parameterCount = parameters.length;
    }

    public @NotNull String getName() {
        return name;
    }

    public void run(Object target, Object[] parsedArgs) throws CommandException {
        try {
            this.method.invoke(target, parsedArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    public Object[] parseArguments(CommandSender sender, String[] args)
            throws ArgumentParseException, InvalidSenderException {
        Object[] parsed = new Object[parameterCount];

        for (int i = 0; i < arguments.length; i++) {
            CommandMethodArgument argument = arguments[i];
            parsed[argument.getPosition()] = argument.parse(args[i]);
        }

        if (this.senderArgument != null) {
            parsed[this.senderArgument.getPosition()] = this.senderArgument.parse(sender);
        }

        return parsed;
    }

    public @NotNull CommandMethodArgument[] getArguments() {
        return arguments;
    }

    public @NotNull Method getMethod() {
        return method;
    }
}