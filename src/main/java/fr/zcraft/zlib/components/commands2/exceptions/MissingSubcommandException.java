package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Command;

public class MissingSubcommandException extends ArgumentException {
    private final Command<?> parentCommand;

    public MissingSubcommandException(Command<?> parentCommand) {
        this.parentCommand = parentCommand;
    }

    public Command<?> getParentCommand() {
        return parentCommand;
    }
}
