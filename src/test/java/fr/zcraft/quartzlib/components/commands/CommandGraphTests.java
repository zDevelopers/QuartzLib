package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.MockedBukkitTest;
import fr.zcraft.quartzlib.components.commands.attributes.Sender;
import fr.zcraft.quartzlib.components.commands.attributes.SubCommand;
import fr.zcraft.quartzlib.components.commands.exceptions.CommandException;
import java.util.stream.StreamSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommandGraphTests extends MockedBukkitTest {
    private CommandManager commands;

    @Before
    public void beforeEach() {
        commands = new CommandManager();
    }

    @Test
    public void canDiscoverBasicSubcommands() {
        class FooCommand {
            public void add() {
            }

            public void get() {
            }

            public void list() {
            }
        }

        CommandGroup commandGroup =
                new CommandGroup(FooCommand.class, () -> new FooCommand(), "foo", new TypeCollection());
        String[] commandNames =
                StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName)
                        .toArray(String[]::new);
        Assert.assertArrayEquals(new String[] {"add", "get", "list"}, commandNames);
    }

    @Test
    public void onlyDiscoversPublicMethods() {
        CommandGroup commandGroup =
                new CommandGroup(CommandWithStatics.class, () -> new CommandWithStatics(), "foo", new TypeCollection());
        String[] commandNames =
                StreamSupport.stream(commandGroup.getSubCommands().spliterator(), false).map(CommandNode::getName)
                        .toArray(String[]::new);
        Assert.assertArrayEquals(new String[] {"add", "delete"}, commandNames);
    }

    @Test
    public void canRunBasicSubcommands() throws CommandException {
        final boolean[] ran = {false, false, false};

        class FooCommand {
            public void add() {
                ran[0] = true;
            }

            public void get() {
                ran[1] = true;
            }

            public void list() {
                ran[2] = true;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(server.addPlayer(), "foo", "get");
        Assert.assertArrayEquals(new boolean[] {false, true, false}, ran);
    }

    @Test
    public void canReceiveStringArguments() throws CommandException {
        final String[] argValue = {""};

        class FooCommand {
            public void add(String arg) {
                argValue[0] = arg;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(server.addPlayer(), "foo", "add", "pomf");
        Assert.assertArrayEquals(new String[] {"pomf"}, argValue);
    }

    @Test
    public void canReceiveParsedArguments() throws CommandException {
        final int[] argValue = {0};

        class FooCommand {
            public void add(Integer arg) {
                argValue[0] = arg;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(server.addPlayer(), "foo", "add", "42");
        Assert.assertArrayEquals(new int[] {42}, argValue);
    }

    @Test
    public void canReceiveEnumArguments() throws CommandException {
        final FooEnum[] argValue = {null};

        class FooCommand {
            public void add(FooEnum arg) {
                argValue[0] = arg;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(server.addPlayer(), "foo", "add", "foo");
        Assert.assertArrayEquals(new FooEnum[] {FooEnum.FOO}, argValue);
        commands.run(server.addPlayer(), "foo", "add", "bar");
        Assert.assertArrayEquals(new FooEnum[] {FooEnum.BAR}, argValue);
    }

    @Test
    public void canReceiveCommandSender() throws CommandException {
        final CommandSender[] senders = {null};
        Player player = server.addPlayer();

        class FooCommand {
            public void add(@Sender CommandSender sender) {
                senders[0] = sender;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(player, "foo", "add");
        Assert.assertArrayEquals(new CommandSender[] {player}, senders);
    }

    @Test
    public void canCallSubcommand() throws CommandException {
        final boolean[] ran = {false};

        class SubFooCommand {
            public void add() {
                ran[0] = true;
            }
        }

        class FooCommand {
            @SubCommand
            public final SubFooCommand sub = new SubFooCommand();

            public void add() {
                throw new RuntimeException("This shouldn't run!");
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());
        commands.run(server.addPlayer(), "foo", "sub", "add");
        Assert.assertArrayEquals(new boolean[] {true}, ran);
    }

    @Test
    public void canHandleOverrides() throws CommandException {
        final String[] argValue = {""};

        class FooCommand {
            public void add(String arg) {
                argValue[0] = arg;
            }

            public void add() {
                argValue[0] = "bar";
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());

        commands.run(server.addPlayer(), "foo", "add", "pomf");
        Assert.assertArrayEquals(new String[] {"pomf"}, argValue);

        commands.run(server.addPlayer(), "foo", "add");
        Assert.assertArrayEquals(new String[] {"bar"}, argValue);
    }

    @Test
    public void canHandleOverridesWithSameArgumentCount() throws CommandException {
        final Object[] argValue = {null};

        class FooCommand {
            public void add(Integer arg) {
                argValue[0] = arg;
            }

            public void add(String arg) {
                argValue[0] = arg;
            }
        }

        commands.addCommand("foo", FooCommand.class, () -> new FooCommand());

        commands.run(server.addPlayer(), "foo", "add", "pomf");
        Assert.assertArrayEquals(new Object[] {"pomf"}, argValue);

        commands.run(server.addPlayer(), "foo", "add", "42");
        Assert.assertArrayEquals(new Object[] {42}, argValue);
    }

    enum FooEnum {
        FOO, BAR
    }
}
