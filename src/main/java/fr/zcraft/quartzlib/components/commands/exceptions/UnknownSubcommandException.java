package fr.zcraft.quartzlib.components.commands.exceptions;

import fr.zcraft.quartzlib.components.commands.CommandGroup;
import fr.zcraft.quartzlib.components.commands.CommandNode;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.rawtext.RawText;
import fr.zcraft.quartzlib.components.rawtext.RawTextPart;
import fr.zcraft.quartzlib.tools.text.StringUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class UnknownSubcommandException extends CommandException {
    public static final String CHAT_PREFIX = "┃ ";
    private final CommandGroup commandGroup;
    private final String attemptedSubcommand;

    public UnknownSubcommandException(CommandGroup commandGroup, String attemptedSubcommand) {
        this.commandGroup = commandGroup;
        this.attemptedSubcommand = attemptedSubcommand;
    }

    @Override
    public RawText display(CommandSender sender) {
        RawTextPart<?> text = new RawText(CHAT_PREFIX).color(ChatColor.DARK_RED)
                .then(I.t("Unknown subcommand: ")).color(ChatColor.RED)
                .then("/").color(ChatColor.WHITE)
                .then(getParents() + " ").color(ChatColor.AQUA)
                .then(attemptedSubcommand).style(ChatColor.RED, ChatColor.UNDERLINE, ChatColor.BOLD)
                .hover(appendSubCommandList(new RawText()));

        String nearest = getNearestCommand();

        if (nearest != null) {
            text = text.then("\n" + CHAT_PREFIX).color(ChatColor.AQUA)
                    .then("  " + I.t("Did you mean: ")).color(ChatColor.GRAY)
                    .then("/").color(ChatColor.WHITE)
                    .then(getParents() + " ").style(ChatColor.AQUA, ChatColor.UNDERLINE)
                        .hover(I.t("Click to insert this command"))
                        .suggest("/" + getParents() + " " + nearest)
                    .then(nearest).style(ChatColor.DARK_AQUA, ChatColor.UNDERLINE)
                        .hover(I.t("Click to insert this command"))
                        .suggest("/" + getParents() + " " + nearest);
        }

        return text.build();
    }

    private static final int MAX_DISTANCE = 10;

    @Nullable
    private String getNearestCommand() {
        List<String> names = commandGroup.getSubCommands()
                .stream()
                .map(CommandNode::getName)
                .collect(Collectors.toList());

        return StringUtils.levenshteinNearest(attemptedSubcommand, names, MAX_DISTANCE);
    }

    private String getParents() {
        StringBuilder builder = new StringBuilder();

        CommandGroup group = commandGroup;

        do {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(group.getName());
            group = group.getParent();
        } while (group != null);

        return builder.toString();
    }

    private RawTextPart<?> appendSubCommandList(RawTextPart<?> text) {
        boolean first = true;
        text = text.then(I.t("Should be one of the following:\n  "));
        for (CommandNode subCommand : commandGroup.getSubCommands()) {
            if (!first) {
                text = text.then(", ").color(ChatColor.GRAY);
            }
            first = false;

            text = text.then(subCommand.getName()).color(ChatColor.AQUA);
        }

        return text;
    }
}
