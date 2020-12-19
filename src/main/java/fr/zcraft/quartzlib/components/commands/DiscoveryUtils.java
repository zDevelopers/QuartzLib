package fr.zcraft.quartzlib.components.commands;

import fr.zcraft.quartzlib.components.commands.annotations.SubCommand;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class DiscoveryUtils {
    public static Stream<CommandMethod> getCommandMethods(Class<?> commandGroupClass, TypeCollection typeCollection) {
        // Yay java "lambdas"
        AtomicInteger declarationIndex = new AtomicInteger(0);

        return Arrays.stream(commandGroupClass.getDeclaredMethods())
                .filter(m -> hasRunnableModifiers(m.getModifiers()))
                .map((Method method) -> new CommandMethod(method, typeCollection, declarationIndex.getAndIncrement()));
    }

    public static Stream<CommandGroup> getSubCommands(CommandGroup commandGroup, TypeCollection typeCollection) {
        return Arrays.stream(commandGroup.getCommandGroupClass().getDeclaredFields())
                .filter(m -> hasRunnableModifiers(m.getModifiers()))
                .filter(DiscoveryUtils::isSubcommand)
                .map(field -> new CommandGroup(commandGroup, field, typeCollection));
    }

    private static boolean isSubcommand(AccessibleObject field) {
        return field.isAnnotationPresent(SubCommand.class);
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

    private static boolean hasRunnableModifiers(int modifiers) {
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
    }

    public static String generateArgumentName(Method declaringMethod, Parameter parameter) {
        String parameterTypeName = generateArgumentName(parameter.getType());
        Parameter[] parametersWithSameTypeName = Arrays.stream(declaringMethod.getParameters())
                .filter(p -> parameterTypeName.equals(generateArgumentName(p.getType())))
                .toArray(Parameter[]::new);

        if (parametersWithSameTypeName.length <= 1) {
            return parameterTypeName;
        }

        int index = IntStream.range(0, parametersWithSameTypeName.length)
                .filter(i -> parameter == parametersWithSameTypeName[i])
                .findFirst() // first occurrence
                .orElse(-1) + 1;

        return parameterTypeName + index;
    }

    private static String generateArgumentName(Class<?> type) {
        String parameterTypeName = type.getSimpleName();
        if ("".equals(parameterTypeName)) {
            return "arg";
        }
        return Character.toLowerCase(parameterTypeName.charAt(0)) + parameterTypeName.substring(1);
    }
}
