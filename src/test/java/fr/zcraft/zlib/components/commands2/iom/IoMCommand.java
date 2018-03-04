package fr.zcraft.zlib.components.commands2.iom;

import fr.zcraft.zlib.components.commands2.exceptions.CommandException;
import fr.zcraft.zlib.components.commands2.CommandRunnable;
import fr.zcraft.zlib.components.commands2.Context;
import fr.zcraft.zlib.components.commands2.annotations.Subcommand;

public enum IoMCommand implements CommandRunnable {
    @Subcommand(CreateCommand.class)
    CREATE,
    @Subcommand(ListCommand.class)
    LIST,
    ;

    static public class ListCommand implements CommandRunnable {

        @Override
        public void run(Context context) throws CommandException {

        }
    }
}
