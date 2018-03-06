package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Flag;

public class FlagAlreadyDefinedException extends ArgumentException {
    private final Flag flag;

    public FlagAlreadyDefinedException(Flag flag, String argument, int position) {
        super(argument, position);
        this.flag = flag;
    }

    public Flag getFlag() {
        return flag;
    }
}
