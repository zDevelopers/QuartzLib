package fr.zcraft.quartzlib.components.commands.arguments.generic;

import fr.zcraft.quartzlib.components.commands.ArgumentType;
import fr.zcraft.quartzlib.components.commands.exceptions.ArgumentParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumArgumentTypeTests {
    private final EnumArgumentType enumArgumentType = new EnumArgumentType();

    private enum SimpleEnum {
        FOO, BAR
    }

    @Test
    public void worksOnSimpleEnum() throws ArgumentParseException {
        ArgumentType<?> argumentType = enumArgumentType.getMatchingArgumentType(SimpleEnum.class).get();

        Assertions.assertEquals(SimpleEnum.FOO, argumentType.parse("foo"));
        Assertions.assertEquals(SimpleEnum.BAR, argumentType.parse("bar"));

        Assertions.assertThrows(ArgumentParseException.class, () -> argumentType.parse("blah"));
    }
}
