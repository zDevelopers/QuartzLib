package fr.zcraft.zlib.components.commands2.converters;

import fr.zcraft.zlib.components.commands2.ParameterTypeConverter;
import fr.zcraft.zlib.components.commands2.exceptions.InvalidArgumentException;

import java.net.URI;
import java.net.URISyntaxException;

public class URITypeConverter implements ParameterTypeConverter<URI> {
    @Override
    public Class<URI> getType() {
        return URI.class;
    }

    @Override
    public URI fromArgument(String argument) throws InvalidArgumentException {
        try {
            return new URI(argument);
        } catch (URISyntaxException e) {
            throw new InvalidArgumentException("Invalid URI");
        }
    }
}
