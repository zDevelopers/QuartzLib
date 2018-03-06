package fr.zcraft.zlib.components.commands2.converters;

import fr.zcraft.zlib.components.commands2.ParameterTypeConverter;
import fr.zcraft.zlib.components.commands2.exceptions.ParameterTypeConverterException;

public class IntegerTypeConverter implements ParameterTypeConverter<Integer> {
    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer fromArgument(String argument) throws ParameterTypeConverterException {
        try {
            return Integer.parseInt(argument);
        } catch(NumberFormatException e) {
            throw new ParameterTypeConverterException("Invalid integer", e);
        }
    }
}
