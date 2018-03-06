package fr.zcraft.zlib.components.commands2.exceptions;

public class ExtraArgumentException extends ArgumentException {
    public ExtraArgumentException(String argument, int position) {
        super(argument, position);
    }
}
