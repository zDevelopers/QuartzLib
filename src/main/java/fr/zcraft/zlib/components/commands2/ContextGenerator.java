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

import fr.zcraft.zlib.components.commands2.exceptions.*;
import fr.zcraft.zlib.tools.reflection.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class contains various utilities to generate Context objects from arguments matched against their bound Command class.
 */
abstract class ContextGenerator {
    private ContextGenerator() {}

    private static <T extends CommandRunnable> Context<T> makeEnumContext(Command<T> command, CommandSender sender, String[] arguments, Optional<Context> parentContext) throws ArgumentException {
        if(arguments.length < 1) throw new MissingSubcommandException(command);
        SubCommand<?, T> subCommand = command.getSubCommand(arguments[0]).orElseThrow(() -> new UnknownSubcommandException(command, arguments[0], 0));
        return new Context<>(subCommand.getParentEnumValue(), sender, arguments, command, parentContext, Optional.of(subCommand));
    }

    static Context<? extends CommandRunnable> makeContext(Command<? extends CommandRunnable> command, CommandSender sender, String[] arguments, Optional<Context> parentContext) throws ArgumentException {
        if(!command.isCommandGroup()) {
            return makePlainClassContext(command, sender, arguments, parentContext);
        }

        Context<?> context = makeEnumContext(command, sender, arguments, parentContext);
        SubCommand<?, ? extends CommandRunnable> subCommand = context.getMatchedSubCommand().orElseThrow(NullPointerException::new);
        try {
            return makeContext(subCommand.getCommand(), sender, Arrays.copyOfRange(arguments, 1, arguments.length), Optional.of(context));
        } catch(ArgumentException e) {
            e.setArgumentPosition(e.getArgumentPosition().map(i -> i + 1));
            throw e;
        }
    }

    private static <T extends CommandRunnable> Context<T> makePlainClassContext(Command<T> command, CommandSender sender, String[] arguments, Optional<Context> parentContext) throws ArgumentException {
        T runnable = instanciateCommandRunnable(command);
        List<Parameter> parameters = command.getParameters();
        List<Flag> flags = command.getFlags();
        List<Flag> remainingFlags = new ArrayList<>(flags);

        int argumentsI = 0;
        int parametersI = 0;

        for(; argumentsI < arguments.length; ++argumentsI) {
            String argument = arguments[argumentsI];
            Flag flag = null;
            if(argument.startsWith("--")) {
                argument = argument.substring(2);
                Optional<Flag> oflag = command.getFlag(argument);
                if (!oflag.isPresent()) throw new UnknownFlagException(argument, argumentsI);
                flag = oflag.get();
            } else if(argument.startsWith("-")) {
                argument = argument.substring(1);
                Optional<Flag> oflag = command.getShortFlag(argument);
                if (!oflag.isPresent()) throw new UnknownFlagException(argument, argumentsI);
                flag = oflag.get();
            }

            if(flag != null) {
                if (!remainingFlags.remove(flag)) throw new FlagAlreadyDefinedException(flag, argument, argumentsI);
                if (!flag.hasValue()) {
                    setRunnableField(runnable, flag, true);
                } else {
                    if (argumentsI + 1 >= arguments.length) throw new FlagMissingValueException(flag, argumentsI);
                    ++argumentsI;
                    Object runnableValue = convertFieldType(flag, arguments[argumentsI], argumentsI);
                    if (flag.isRequired()) {
                        setRunnableField(runnable, flag, runnableValue);
                    } else {
                        setRunnableField(runnable, flag,  Optional.of(runnableValue));
                    }
                }
                continue;
            }

            if(parametersI >= parameters.size()) throw new ExtraArgumentException(arguments[argumentsI], argumentsI);
            Parameter parameter = parameters.get(argumentsI);
            setRunnableField(runnable, parameter, convertFieldType(parameter, arguments[argumentsI], argumentsI));
            ++parametersI;
        }

        //Look for missing required parameters
        for(; parametersI < parameters.size(); ++parametersI) {
            Parameter parameter = parameters.get(parametersI);
            if(parameter.isRequired()) {
                throw new MissingParameterException(parameter);
            }
            setRunnableField(runnable, parameter, Optional.empty());
        }

        //Look for missing required flags
        for(Flag f: remainingFlags) {
            if(f.isRequired()) throw new MissingRequiredFlagException(f);
            if(f.hasValue()) {
                setRunnableField(runnable, f, Optional.empty());
            } else {
                setRunnableField(runnable, f, false);
            }
        }


        return new Context<>(runnable, sender, arguments, command, parentContext, Optional.empty());
    }

    static private void setRunnableField(CommandRunnable runnable, Field field, Object value) {
        try {
            field.getRunnableField().set(runnable, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set runnable class field : " + runnable.getClass() + "." + field.getRunnableField().getName(), e);
        }
    }

    static private <T extends CommandRunnable> T instanciateCommandRunnable(Command<T> command) {
        try {
            return Reflection.instantiate(command.getRunnableClass());
        } catch(Exception e) {
            throw new RuntimeException("Unable to instanciate Command runnable class : " + command.getRunnableClass().getName(), e);
        }
    }

    static private Object convertFieldType(Field parameter, String argument, int argumentPosition) throws InvalidArgumentException {
        try {
            return parameter.getTypeConverter().fromArgument(argument);
        } catch (ParameterTypeConverterException e) {
            throw new InvalidArgumentException(e, argument, argumentPosition);
        }
    }
}
