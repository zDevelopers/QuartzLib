package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Command;

public class UnknownSubcommandException extends ArgumentException {
    private final Command<?> parentCommand;

    public UnknownSubcommandException(Command<?> parentCommand, String argument, int position) {
        super(argument, position);
        this.parentCommand = parentCommand;
    }

    public Command<?> getParentCommand() {
        return parentCommand;
    }
}
