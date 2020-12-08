package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;

import java.lang.reflect.Parameter;

public class CommandMethodArgument {
    private final Parameter parameter;
    private final int position;
    private final ArgumentTypeWrapper<?> typeHandler;

    public CommandMethodArgument(Parameter parameter, int position, TypeCollection typeCollection) {
        this.parameter = parameter;
        this.position = position;
        this.typeHandler = typeCollection.findArgumentType(parameter.getType()).get(); // FIXME: handle unknown types
    }

    public Object parse(String raw) throws ArgumentParseException {
        return this.typeHandler.getTypeHandler().parse(raw);
    }

    public int getPosition() {
        return position;
    }
}
