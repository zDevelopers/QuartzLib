package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.Parameter;

public class CommandMethodArgument {
    private final Parameter parameter;
    private final ArgumentTypeHandler<?> typeHandler;

    public CommandMethodArgument(Parameter parameter, ArgumentTypeHandlerCollection typeHandlerCollection) {
        this.parameter = parameter;
        this.typeHandler = typeHandlerCollection.findTypeHandler(parameter.getType()).get(); // FIXME: handle unknown types
    }

    public Object parse(String raw) {
        return this.typeHandler.getTypeHandler().parse(raw);
    }
}
