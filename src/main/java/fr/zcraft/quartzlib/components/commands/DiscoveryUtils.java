package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

abstract class DiscoveryUtils {
    public static Stream<CommandMethod> getCommandMethods(Class<?> commandGroupClass, TypeCollection typeCollection) {
        return Arrays.stream(commandGroupClass.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
                .map((Method method) -> new CommandMethod(method, typeCollection));
    }

    public static Supplier<?> getClassConstructorSupplier(Class<?> commandGroupClass) {
        Constructor<?> constructor = commandGroupClass.getDeclaredConstructors()[0];
        return () -> {
            try {
                return constructor.newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e); // TODO
            }
        };
    }
}
