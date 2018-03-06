package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Flag;

public class MissingRequiredFlagException extends ArgumentException {
    private final Flag flag;

    public MissingRequiredFlagException(Flag flag) {
        this.flag = flag;
    }

    public Flag getFlag() {
        return flag;
    }
}
