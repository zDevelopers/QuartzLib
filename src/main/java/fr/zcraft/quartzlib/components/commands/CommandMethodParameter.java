package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.annotations.Param;
import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;
import fr.zcraft.quartzlib.components.commands.exceptions.UnknownArgumentTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.jetbrains.annotations.NotNull;

public class CommandMethodParameter {
    private final Parameter parameter;
    private final int position;
    private final ArgumentTypeWrapper<?> typeHandler;
    private final String name;

    public CommandMethodParameter(
            CommandMethod parent,
            Parameter parameter,
            int position,
            TypeCollection typeCollection
    ) {
        this.parameter = parameter;
        this.position = position;
        this.typeHandler = typeCollection.findArgumentType(parameter.getType())
            .orElseThrow(() -> new UnknownArgumentTypeException(parent.getMethod(), parameter.getType()));
        this.name = findName(parent.getMethod(), parameter);
    }

    public Object parse(String raw) throws ArgumentParseException {
        return this.typeHandler.getTypeHandler().parse(raw);
    }

    public int getPosition() {
        return position;
    }

    @NotNull public String getName() {
        return name;
    }

    private static String findName(Method declaringMethod, Parameter param) {
        Param annotation = param.getAnnotation(Param.class);
        if (annotation != null) {
            return annotation.value();
        }

        if (param.isNamePresent()) {
            return param.getName();
        }

        return DiscoveryUtils.generateArgumentName(declaringMethod, param);
    }
}
