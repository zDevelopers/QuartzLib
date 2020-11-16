package fr.zcraft.quartzlib.components.commands.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.StreamSupport;

public class CommandGraphTests {
    @Test public void canDiscoverBasicSubcommands() {
        class FooCommand {
            public void add () {}
            public void get () {}
            public void list () {}
        }

        CommandGroup commandGroup = new CommandGroup(FooCommand.class, "foo");
        String[] commandNames = StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName).toArray(String[]::new);
        Assertions.assertArrayEquals(new String[] {"add", "get", "list"}, commandNames);
    }
}
