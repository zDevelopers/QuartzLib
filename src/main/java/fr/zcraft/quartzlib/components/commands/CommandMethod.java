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

class CommandMethod {
    private final Method method;
    private final String name;
    private final CommandMethodArgument[] arguments;
    private final int parameterCount;
    private CommandMethodSenderArgument senderArgument = null;

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
                arguments.add(new CommandMethodArgument(parameter, i, typeCollection));
            }
        }

        this.arguments = arguments.toArray(new CommandMethodArgument[] {});
        this.parameterCount = parameters.length;
    }

    public String getName() {
        return name;
    }

    public void run(Object target, CommandSender sender, String[] args) throws CommandException {
        Object[] parsedArgs = parseArguments(sender, args);
        try {
            this.method.invoke(target, parsedArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // TODO
        }
    }

    private Object[] parseArguments(CommandSender sender, String[] args)
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

    public CommandMethodArgument[] getArguments() {
        return arguments;
    }
}
