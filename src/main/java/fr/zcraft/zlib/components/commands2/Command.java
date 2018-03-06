/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.zcraft.zlib.components.commands2;

import fr.zcraft.zlib.components.commands2.exceptions.CommandException;

import java.util.List;
import java.util.Optional;

/**
 * This class represents a registered command.
 * @param <T> The command runnable type this command is bound to.
 */
public class Command<T extends CommandRunnable> {

    private final String name;
    private final Class<T> runnableClass;
    private final boolean isCommandGroup;
    private final List<SubCommand<?, T>> subCommands;
    private final List<Parameter> parameters;
    private final List<Flag> flags;

    Command(Class<T> runnableClass, String name, boolean isCommandGroup, List<SubCommand<?, T>> subCommands, List<Parameter> parameters, List<Flag> flags) {
        this.runnableClass = runnableClass;
        this.name = name;
        this.isCommandGroup = isCommandGroup;
        this.subCommands = subCommands;
        this.parameters = parameters;
        this.flags = flags;
    }

    public Context<? extends CommandRunnable> makeContext(CommandSender sender, String[] arguments) throws CommandException {
        return ContextGenerator.makeContext(this, sender, arguments, Optional.empty());
    }

    public String getName() {
        return name;
    }

    public boolean nameMatches(String string) {
        return string.equalsIgnoreCase(name);
    }

    public Class<T> getRunnableClass() {
        return runnableClass;
    }

    public boolean isCommandGroup() {
        return isCommandGroup;
    }

    public List<SubCommand<?, T>> getSubCommands() {
        return subCommands;
    }

    public Optional<SubCommand<?, T>> getSubCommand(String name) {
        for(SubCommand<?, T> subCommand : subCommands) {
            if(subCommand.getCommand().nameMatches(name)) return Optional.of(subCommand);
        }
        return Optional.empty();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public Optional<Flag> getFlag(String shortName) {
        for(Flag flag: flags) {
            if(flag.getName().equals(shortName)) return Optional.of(flag);
        }
        return Optional.empty();
    }

    public Optional<Flag> getShortFlag(String shortName) {
        for(Flag flag: flags) {
            if(flag.getShortName().equals(shortName)) return Optional.of(flag);
        }
        return Optional.empty();
    }
}
