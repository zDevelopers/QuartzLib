package fr.zcraft.zlib.components.commands2.exceptions;

import fr.zcraft.zlib.components.commands2.Parameter;

public class MissingParameterException extends ArgumentException {
    private final Parameter flag;

    public MissingParameterException(Parameter flag) {
        this.flag = flag;
    }

    public Parameter getFlag() {
        return flag;
    }
}
