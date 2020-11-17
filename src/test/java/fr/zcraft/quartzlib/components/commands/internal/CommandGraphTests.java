package fr.zcraft.quartzlib.components.commands.internal;

import org.junit.jupiter.api.Assertions;
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
    @Test public void canDiscoverBasicSubcommands() {
        class FooCommand {
            public void add () {}
            public void get () {}
            public void list () {}
        }

        CommandGroup commandGroup = new CommandGroup(FooCommand.class, () -> new FooCommand(), "foo");
        String[] commandNames = StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[] {"add", "get", "list"}, commandNames);
    }

    @Test public void onlyDiscoversPublicMethods() {
        CommandGroup commandGroup = new CommandGroup(CommandWithStatics.class, () -> new CommandWithStatics(), "foo");
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

        FooCommand f = new FooCommand();
        CommandGroup commandGroup = new CommandGroup(FooCommand.class, () -> new FooCommand(),"foo");
        commandGroup.run("get");
        Assertions.assertArrayEquals(new boolean[] { false, true, false }, ran);
    }
}
