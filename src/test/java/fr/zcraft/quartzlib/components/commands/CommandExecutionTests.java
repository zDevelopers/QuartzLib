package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.MockedToasterTest;
import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandExecutionTests extends MockedToasterTest {
    private CommandManager commands;

    @Before
    public void beforeEach() {
        commands = new CommandManager();
    }

    @Test
    public void canRegisterAndRunCommand() throws CommandException {

        final boolean[] ran = {false};

        class FooCommand {
            public void get() {
                ran[0] = true;
            }
        }

        commands.registerCommand("toaster", FooCommand.class, () -> new FooCommand());
        boolean success = server.dispatchCommand(server.addPlayer(), "toaster get");
        Assert.assertTrue(success);
        Assert.assertArrayEquals(new boolean[] {true}, ran);
    }
}
