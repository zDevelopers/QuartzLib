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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains various utilities to generate Command objects from their bound runnable class.
 */
class CommandGenerator {
    static public <T extends CommandRunnable> Command<T> fromClass(Class<T> runnableClass, String name) {
        if(isCommandGroup(runnableClass)) {
            return fromEnumClass(runnableClass, name);
        } else {
            return fromPlainClass(runnableClass, name);
        }
    }

    static private boolean isCommandGroup(Class<? extends CommandRunnable> runnableClass) {
        return Enum.class.isAssignableFrom(runnableClass);
    }

    static private <T extends CommandRunnable> Command<T> fromEnumClass(Class<T> runnableClass, String name) {
        List<SubCommand<?, ?>> subcommands = Arrays.stream(runnableClass.getDeclaredFields())
                .filter(Field::isEnumConstant)
                .map(CommandGenerator::fromField)
                .collect(Collectors.toList());

        return new Command(runnableClass, name, true, subcommands);
    }

    static private <T extends CommandRunnable> Command<T> fromPlainClass(Class<T> runnableClass, String name) {
        return new Command(runnableClass, name, false, new ArrayList<>());
    }

    static private <T extends CommandRunnable> SubCommand<?, ?> fromField(Field field) {
        Subcommand subcommand = field.getAnnotation(Subcommand.class);
        if(subcommand == null) throw new RuntimeException("No subcommand annotation"); //TODO: Better exception

        String commandName = subcommand.name();
        if(commandName.isEmpty()) commandName = field.getName().toLowerCase();
        Command<?> innerCommand = fromClass(subcommand.value(), commandName);
        T parentValue;
        try {
            parentValue = (T) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);  //TODO: Better exception
        }

        return new SubCommand(innerCommand, parentValue, field);
    }
}
