package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class CommandMethod {
    private final Method method;
    private final String name;
    private final CommandMethodArgument[] arguments;

    CommandMethod(Method method, ArgumentTypeHandlerCollection typeHandlerCollection) {
        this.method = method;
        this.name = method.getName();

        arguments = Arrays.stream(method.getParameters())
                .map(p -> new CommandMethodArgument(p, typeHandlerCollection))
                .toArray(CommandMethodArgument[]::new);
    }

    public String getName() {
        return name;
    }

    public void run(Object target, String[] args) {
        Object[] parsedArgs = parseArguments(args);
        try {
            this.method.invoke(target, parsedArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // TODO
        }
    }

    private Object[] parseArguments(String[] args) {
        Object[] parsed = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            parsed[i] = arguments[i].parse(args[i]);
        }

        return parsed;
    }

    public CommandMethodArgument[] getArguments() {
        return arguments;
    }
}
