package fr.zcraft.zlib.components.commands2.iom;

import fr.zcraft.zlib.components.commands2.CommandRunnable;
import fr.zcraft.zlib.components.commands2.Context;
import fr.zcraft.zlib.components.commands2.annotations.About;
import fr.zcraft.zlib.components.commands2.annotations.Flag;

import java.net.URI;
import java.util.Optional;

public class CreateCommand implements CommandRunnable {

    @About("The URI")
    public URI imageURI;

    @About("The width")
    @Flag(shortName = "w")
    public Optional<Integer> width;

    @About("The height")
    @Flag(shortName = "h")
    public Optional<Integer> height;

    @Flag
    public boolean stretch;

    @Flag
    public boolean cover;

    @Override
    public void run(Context context) {
        System.out.println("Hello world !");
    }

}
