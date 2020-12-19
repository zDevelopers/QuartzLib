package fr.zcraft.quartzlib.components.commands.arguments.primitive;

import fr.zcraft.quartzlib.components.commands.ArgumentType;
import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;

public class IntegerArgumentType implements ArgumentType<Integer> {
    @Override
    public Integer parse(String raw) throws ArgumentParseException {
        try {
            return Integer.parseInt(raw, 10);
        } catch (NumberFormatException ignored) {
            throw new IntegerParseException();
        }
    }

    private static class IntegerParseException extends ArgumentParseException {

    }
}
