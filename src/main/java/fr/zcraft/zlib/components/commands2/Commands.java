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
import fr.zcraft.zlib.components.commands2.exceptions.CommandNotFoundException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The entry point for the zLib Commands component.
 */
public abstract class Commands
{
    private Commands() {}

    static private Map<String, Command<? extends CommandRunnable>> commandRegistry = new HashMap<>();
    static private Map<Class<?>, ParameterTypeConverter<?>> typeConverterRegistry = new HashMap<>();

    /**
     * Registers a new command.
     * @param command The command runnable to be registered
     * @param name The name of the command
     */
    static public void register(Class<? extends CommandRunnable> command, String name) {
        name = name.toLowerCase();
        if(commandRegistry.containsKey(name)) throw new IllegalArgumentException("Command already registered : " + name);

        commandRegistry.put(name, CommandGenerator.fromClass(command, name));
    }

    /**
     * Registers a new command parameter type converter.
     * You must register a new converter before using any types in your commands that zLib doesn't support out of the box.
     * Additional type converters must be registered before any commands using the type are.
     * @param parameterTypeConverter The type converter to register
     */
    static public void registerParameterTypeConverter(ParameterTypeConverter<?> parameterTypeConverter) {
        //TODO: Raise exception if already registered
        typeConverterRegistry.put(parameterTypeConverter.getType(), parameterTypeConverter);
    }

    /**
     * Finds the registered command matching the given name.
     * @param commandName The name of the command
     * @return The matching command
     * @throws CommandNotFoundException If no matching command is found
     */
    static public Command findCommand(String commandName) throws CommandNotFoundException {
        Command command = commandRegistry.get(commandName.toLowerCase());
        if(command == null) throw new CommandNotFoundException(commandName);
        return command;
    }

    /**
     * Finds the registered command parameter type converter handling the given type.
     * @param type The type used to look up the matching type converter
     * @param <T> The type the command parameter type converter handles
     * @return The registered command parameter type converter, if found
     */
    static public <T> Optional<ParameterTypeConverter<T>> findTypeConverter(Class<T> type) {
        @SuppressWarnings("unchecked")
        ParameterTypeConverter<T> typeConverter = (ParameterTypeConverter<T>) typeConverterRegistry.get(type);
        return Optional.ofNullable(typeConverter);
    }

    /**
     * Creates a new Command execution context for the given command.
     * @param commandName The name of the command
     * @param sender The command sender the command originated from
     * @param arguments The command arguments
     * @return The newly created context
     * @throws CommandException If the command is not found, or if an error occured when parsing the arguments
     */
    static public Context<?> makeContext(String commandName, CommandSender sender, String[] arguments) throws CommandException {
        return findCommand(commandName).makeContext(sender, arguments);
    }
}
