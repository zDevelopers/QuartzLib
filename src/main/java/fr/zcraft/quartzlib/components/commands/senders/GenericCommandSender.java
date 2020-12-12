package fr.zcraft.quartzlib.components.commands.senders;

import fr.zcraft.quartzlib.components.commands.GenericSenderType;
import fr.zcraft.quartzlib.components.commands.SenderType;
import fr.zcraft.quartzlib.components.commands.exceptions.InvalidSenderException;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class GenericCommandSender implements GenericSenderType<CommandSender> {
    @Override
    public Optional<SenderType<CommandSender>> getMatchingSenderType(Class<?> type) {
        if (CommandSender.class.isAssignableFrom(type)) {
            return Optional.of(new CommandSenderSubType(type));
        }
        return Optional.empty();
    }

    private static class InvalidSenderTypeException extends InvalidSenderException {

    }

    private static class CommandSenderSubType implements SenderType<CommandSender> {
        private final Class<?> subtype;

        private CommandSenderSubType(Class<?> subtype) {
            this.subtype = subtype;
        }

        @Override
        public CommandSender parse(CommandSender raw) throws InvalidSenderException {
            if (subtype.isAssignableFrom(raw.getClass())) {
                return raw;
            }

            throw new InvalidSenderTypeException();
        }
    }
}
