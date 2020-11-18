package fr.zcraft.quartzlib.components.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.StreamSupport;

// This is outside because inner classes cannot have statics
class CommandWithStatics {
    public void add () {}
    private void get () {}
    protected void list () {}
    public void delete () {}
    void update () {}
    static public void staticMethod () {}
}

public class CommandGraphTests {
    private CommandManager commands;

    @BeforeEach
    public void beforeEach () {
        commands = new CommandManager();
    }

    @Test public void canDiscoverBasicSubcommands() {
        class FooCommand {
            public void add () {}
            public void get () {}
            public void list () {}
        }

        CommandGroup commandGroup = new CommandGroup(FooCommand.class, () -> new FooCommand(), "foo", new ArgumentTypeHandlerCollection());
        String[] commandNames = StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[] {"add", "get", "list"}, commandNames);
    }

    @Test public void onlyDiscoversPublicMethods() {
        CommandGroup commandGroup = new CommandGroup(CommandWithStatics.class, () -> new CommandWithStatics(), "foo", new ArgumentTypeHandlerCollection());
        String[] commandNames = StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[] {"add", "delete"}, commandNames);
    }

    @Test public void canRunBasicSubcommands() {
        final boolean[] ran = {false, false, false};

        class FooCommand {
            public void add () { ran[0] = true; }
            public void get () { ran[1] = true; }
            public void list () { ran[2] = true; }
        }

        commands.registerCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run("foo", "get");
        Assertions.assertArrayEquals(new boolean[] { false, true, false }, ran);
    }

    @Test public void canReceiveStringArguments() {
        final String[] argValue = {""};

        class FooCommand {
            public void add (String arg) { argValue[0] = arg; }
        }

        commands.registerCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run("foo", "add", "pomf");
        Assertions.assertArrayEquals(new String[] { "pomf" }, argValue);
    }

    @Test public void canReceiveParsedArguments() {
        final int[] argValue = {0};

        class FooCommand {
            public void add (Integer arg) { argValue[0] = arg; }
        }

        commands.registerCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run("foo", "add", "42");
        Assertions.assertArrayEquals(new int[] { 42 }, argValue);
    }
}
