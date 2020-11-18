package fr.zcraft.quartzlib.components.commands.arguments.primitive;

import fr.zcraft.quartzlib.components.commands.ArgumentType;

public class IntegerTypeHandler implements ArgumentType<Integer> {
    @Override
    public Integer parse(String raw) {
        return Integer.parseInt(raw, 10); // TODO: handle exceptions
    }
}
