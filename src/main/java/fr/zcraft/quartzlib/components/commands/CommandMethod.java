package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.annotations.Sender;
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

public class CommandMethod implements Comparable<CommandMethod> {
    @NotNull private final Method method;
    @NotNull private final String name;
    @NotNull private final CommandMethodParameter[] parameters;
    private final int parameterCount;
    @Nullable private CommandMethodSenderArgument senderParameter = null;

    private final int declarationIndex;
    private final int priority;

    CommandMethod(Method method, TypeCollection typeCollection, int declarationIndex) {
        this.method = method;
        this.name = method.getName();
        this.declarationIndex = declarationIndex;

        Parameter[] javaParameters = method.getParameters();
        List<CommandMethodParameter> parameters = new ArrayList<>();
        for (int i = 0; i < javaParameters.length; i++) {
            Parameter parameter = javaParameters[i];
            if (parameter.isAnnotationPresent(Sender.class)) { // TODO: check for multiple sender parameters
                senderParameter = new CommandMethodSenderArgument(parameter, i, typeCollection);
            } else {
                parameters.add(new CommandMethodParameter(this, parameter, i, typeCollection));
            }
        }

        this.parameters = parameters.toArray(new CommandMethodParameter[] {});
        this.parameterCount = javaParameters.length;

        fr.zcraft.quartzlib.components.commands.annotations.CommandMethod annotation =
                method.getAnnotation(fr.zcraft.quartzlib.components.commands.annotations.CommandMethod.class);

        if (annotation != null) {
            priority = annotation.priority();
        } else {
            priority = 0;
        }
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

        for (int i = 0; i < parameters.length; i++) {
            CommandMethodParameter argument = parameters[i];
            parsed[argument.getPosition()] = argument.parse(args[i]);
        }

        if (this.senderParameter != null) {
            parsed[this.senderParameter.getPosition()] = this.senderParameter.parse(sender);
        }

        return parsed;
    }

    public @NotNull CommandMethodParameter[] getParameters() {
        return parameters;
    }

    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public int compareTo(@NotNull CommandMethod other) {
        if (priority == other.priority) {
            if (declarationIndex == other.declarationIndex) {
                // This is needed to differentiate between methods with the same declaration index and priority
                return method.toString().compareTo(other.method.toString());
            }

            return Integer.compare(declarationIndex, other.declarationIndex);
        }

        // Higher priority = first in natural order
        return Integer.compare(priority, other.priority) * -1;
    }
}
