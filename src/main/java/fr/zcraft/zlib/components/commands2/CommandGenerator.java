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

import fr.zcraft.zlib.components.commands2.annotations.Subcommand;
import fr.zcraft.zlib.components.commands2.exceptions.CommandException;
import fr.zcraft.zlib.components.commands2.exceptions.UnhandledParameterType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class contains various utilities to generate Command objects from their bound runnable class.
 */
class CommandGenerator {
    static public <T extends CommandRunnable> Command<T> fromClass(Class<T> runnableClass, String name) throws CommandException {
        if(isCommandGroup(runnableClass)) {
            return fromEnumClass(runnableClass, name);
        } else {
            return fromPlainClass(runnableClass, name);
        }
    }

    static private boolean isCommandGroup(Class<? extends CommandRunnable> runnableClass) {
        return Enum.class.isAssignableFrom(runnableClass);
    }

    static private <T extends CommandRunnable> Command<T> fromEnumClass(Class<T> runnableClass, String name) throws CommandException {
        List<SubCommand<?, ?>> subcommands = new ArrayList<>();

        for(Field f : runnableClass.getDeclaredFields()) {
            if(!f.isEnumConstant()) continue;
            subcommands.add(subcommandFromField(f));
        }

        return new Command(runnableClass, name, true, subcommands, new ArrayList<>(), new ArrayList<>());
    }

    static private List<Parameter<?>> getCommandParameters(Class<? extends CommandRunnable> runnableClass) throws UnhandledParameterType {
        List<Parameter<?>> parameters = new ArrayList<>();

        for(Field f : runnableClass.getDeclaredFields()) {
            if(isFlagField(f)) continue;
            parameters.add(parameterFromField(f));
        }

        return parameters;
    }

    static private List<Flag<?>> getCommandFlags(Class<? extends CommandRunnable> runnableClass) throws UnhandledParameterType {
        List<Flag<?>> parameters = new ArrayList<>();

        for(Field f : runnableClass.getDeclaredFields()) {
            if(!isFlagField(f)) continue;
            parameters.add(flagFromField(f));
        }

        return parameters;
    }

    static private boolean isFlagField(Field field) {
        return field.getAnnotation(fr.zcraft.zlib.components.commands2.annotations.Flag.class) != null;
    }

    static private Parameter<?> parameterFromField(Field field) throws UnhandledParameterType {
        Class<?> type = field.getType();
        ParameterTypeConverter typeConverter = Commands.findTypeConverter(type).orElseThrow(() -> new UnhandledParameterType(type, field));
        return new Parameter<>(type, field, typeConverter, field.getName(), null, true);
    }

    static private <T> Flag<?> flagFromField(Field field) throws UnhandledParameterType {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) field.getType();
        fr.zcraft.zlib.components.commands2.annotations.Flag flagAnnotation = field.getAnnotation(fr.zcraft.zlib.components.commands2.annotations.Flag.class);
        String shortName = flagAnnotation.shortName();

        if(boolean.class.isAssignableFrom(type)) {
            return new Flag<>(type, field, null, field.getName(), shortName,null, false, false);
        }

        boolean isRequired = true;
        if(Optional.class.isAssignableFrom(type)) {
            ParameterizedType ptype = (ParameterizedType) field.getGenericType();
            type = (Class<T>) ptype.getActualTypeArguments()[0];
            isRequired = false;
        }

        Optional<ParameterTypeConverter<T>> typeConverter = Commands.findTypeConverter(type);
        if(!typeConverter.isPresent()) throw new UnhandledParameterType(type, field);
        return new Flag<>(type, field, typeConverter.get(), field.getName(), shortName, null, isRequired, true);
    }

    static private <T extends CommandRunnable> Command<T> fromPlainClass(Class<T> runnableClass, String name) throws CommandException {
        return new Command<>(runnableClass, name, false, new ArrayList<>(), getCommandParameters(runnableClass), getCommandFlags(runnableClass));
    }

    static private <T extends CommandRunnable> SubCommand<?, ?> subcommandFromField(Field field) throws CommandException {
        Subcommand subcommand = field.getAnnotation(Subcommand.class);
        if(subcommand == null) throw new RuntimeException("No subcommand annotation"); //TODO: Better exception

        String commandName = subcommand.name();
        if(commandName.isEmpty()) commandName = field.getName().toLowerCase();
        Command<?> innerCommand = fromClass(subcommand.value(), commandName);
        T parentValue;
        try {
            field.setAccessible(true);
            parentValue = (T) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);  //TODO: Better exception
        }

        return new SubCommand(innerCommand, parentValue, field);
    }
}
