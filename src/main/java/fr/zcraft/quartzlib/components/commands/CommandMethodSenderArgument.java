package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.exceptions.InvalidSenderException;
import java.lang.reflect.Parameter;
import org.bukkit.command.CommandSender;

public class CommandMethodSenderArgument {
    private final Parameter parameter;
    private final int position;
    private final SenderTypeWrapper<?> typeHandler;

    public CommandMethodSenderArgument(Parameter parameter, int position, TypeCollection typeCollection) {
        this.parameter = parameter;
        this.position = position;
        this.typeHandler = typeCollection.findSenderType(parameter.getType()).get(); // FIXME: handle unknown types
    }

    public Object parse(CommandSender raw) throws InvalidSenderException {
        return this.typeHandler.getTypeHandler().parse(raw);
    }

    public int getPosition() {
        return position;
    }
}
