package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Flag;

import java.util.Optional;

public class FlagMissingValueException extends ArgumentException {
    private final Flag flag;

    public FlagMissingValueException(Flag flag, int position) {
        super(Optional.empty(), Optional.of(position));
        this.flag = flag;
    }

    public Flag getFlag() {
        return flag;
    }
}
