package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;
import fr.zcraft.quartzlib.components.commands.exceptions.UnknownArgumentTypeException;
import java.lang.reflect.Parameter;

public class CommandMethodArgument {
    private final Parameter parameter;
    private final int position;
    private final ArgumentTypeWrapper<?> typeHandler;

    public CommandMethodArgument(
            CommandMethod parent,
            Parameter parameter,
            int position,
            TypeCollection typeCollection
    ) {
        this.parameter = parameter;
        this.position = position;
        this.typeHandler = typeCollection.findArgumentType(parameter.getType())
            .orElseThrow(() -> new UnknownArgumentTypeException(parent.getMethod(), parameter.getType()));
    }

    public Object parse(String raw) throws ArgumentParseException {
        return this.typeHandler.getTypeHandler().parse(raw);
    }

    public int getPosition() {
        return position;
    }
}
