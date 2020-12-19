package fr.zcraft.quartzlib.components.commands.exceptions;

import fr.zcraft.quartzlib.components.commands.CommandGroup;
import fr.zcraft.quartzlib.components.commands.CommandNode;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.rawtext.RawText;
import fr.zcraft.quartzlib.components.rawtext.RawTextPart;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MissingSubcommandException extends CommandException {
    private final CommandGroup commandGroup;

    public MissingSubcommandException(CommandGroup commandGroup) {
        this.commandGroup = commandGroup;
    }

    @Override
    public RawText display(CommandSender sender) {
        RawTextPart<?> text = new RawText(I.t("Missing subcommand: "))
                .color(ChatColor.RED)
                .then("/").color(ChatColor.WHITE)
                .then(getParents()).color(ChatColor.AQUA)
                .then(" <").style(ChatColor.GRAY)
                .then(I.t("sub-command"))
                    .style(ChatColor.GRAY, ChatColor.UNDERLINE)
                    .hover(appendSubCommandList(new RawText()))
                .then(">").style(ChatColor.GRAY);

        return text.build();
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
        text = text.then(I.t("One of the following:\n  "));
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
