package fr.zcraft.zlib.components.commands2.exceptions;

public class UnknownFlagException extends ArgumentException {
    public UnknownFlagException(String argument, int position) {
        super(argument, position);
    }
}
