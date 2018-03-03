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
package fr.zcraft.zlib.tools.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * A set of tools to simplify reflective operations on Bukkit and the Native Minecraft Server.
 *
 * @author ProkopyL
 * @author Amaury Carrade
 */
public final class Reflection
{
    private Reflection() {}

    /**
     * Returns the Bukkit's current version, as read in the Bukkit's package name.
     *
     * @return The Bukkit's version in the package name.
     */
    static public String getBukkitPackageVersion()
    {
        return getBukkitPackageName().substring("org.bukkit.craftbukkit.".length());
    }
    
    /**
     * Returns the full name of the root Bukkit package: something like
     * "org.bukkit.craftbukkit.v1_8_R3".
     *
     * @return the full name of the root Bukkit package.
     */
    static public String getBukkitPackageName()
    {
        return Bukkit.getServer().getClass().getPackage().getName();
    }
    
    /**
     * Returns the full name of the root NMS package: something like "net.minecraft.server.v1_8_R3".
     *
     * @return the full name of the root NMS package.
     */
    static public String getMinecraftPackageName()
    {
        return "net.minecraft.server." + getBukkitPackageVersion();
    }
    
    /**
     * Returns the {@link Class} of a Bukkit class from it's name (without the main Bukkit
     * package).
     *
     * As example, with "Server", this method returns the {@code org.bukkit.craftbukkit.v1_X_RX.Server}
     * class.
     *
     * @param name The Bukkit's class name (without the main Bukkit package).
     *
     * @return The class.
     * @throws ClassNotFoundException if no class exists with this name in the Bukkit package.
     */
    static public Class getBukkitClassByName(String name) throws ClassNotFoundException
    {
        return Class.forName(getBukkitPackageName() + "." + name);
    }
    
    /**
     * Returns the {@link Class} of a NMS class from it's name (without the main NMS package).
     *
     * As example, with "Server", this method returns the {@code net.minecraft.server.v1_X_RX.Server}
     * class.
     *
     * @param name The NMS' class name (without the main Bukkit package).
     *
     * @return The class.
     * @throws ClassNotFoundException if no class exists with this name in the NMS package.
     */
    static public Class getMinecraftClassByName(String name) throws ClassNotFoundException
    {
        return Class.forName(getMinecraftPackageName() + "." + name);
    }
    

    /**
     * Returns the value of a field (regardless of its visibility) for the given instance.
     *
     * @param hClass   The instance's class.
     * @param instance The instance.
     * @param name     The field's name.
     *
     * @return The field's value for the given instance.
     * @throws NoSuchFieldException     if the field does not exists.
     * @throws IllegalArgumentException if {@code instance} is not an instance of {@code hClass}.
     * @throws IllegalAccessException   if the field cannot be accessed due to a Java language
     *                                  access control.
     */
    static public Object getFieldValue(Class<?> hClass, Object instance, String name)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        return getField(hClass, name).get(instance);
    }
    
    /**
     * Returns the value of a field (regardless of its visibility) for the given instance.
     *
     * @param instance The instance.
     * @param name     The field's name.
     *
     * @return The field's value for the given instance.
     * @throws NoSuchFieldException     if the field does not exists.
     * @throws IllegalArgumentException if {@code instance} is not an instance of itself (should
     *                                  never happens).
     * @throws IllegalAccessException   if the field cannot be accessed due to a Java language
     *                                  access control.
     */
    static public Object getFieldValue(Object instance, String name) 
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        if(instance == null)
            throw new IllegalArgumentException("Cannot infer object type : instance is null.");
        return getFieldValue(instance.getClass(), instance, name);
    }
    

    /**
     * Makes the {@link Field} with the given name in the given class accessible, and returns it.
     *
     * @param klass The field's parent class.
     * @param name  The field's name.
     *
     * @return The {@link Field}.
     * @throws NoSuchFieldException if the class does not contains any field with this name.
     */
    static public Field getField(Class<?> klass, String name) throws NoSuchFieldException
    {
        Field field = klass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
    
    /**
     * Finds the first {@link Field} in the given class with the given type, makes it accessible,
     * and returns it.
     *
     * @param klass The field's parent class.
     * @param type  The field's class.
     *
     * @return The {@link Field}.
     * @throws NoSuchFieldException if the class does not contains any field with this name.
     */
    static public Field getField(Class<?> klass, Class<?> type) throws NoSuchFieldException
    {
        for (Field field : klass.getDeclaredFields())
        {
            if (typeIsAssignableFrom(field.getType(), type))
            {
                field.setAccessible(true);
                return field;
            }
        }

        throw new NoSuchFieldException("Class " + klass.getName() + " does not define any field of type " + type.getName());
    }
    

    /**
     * Update the field with the given name in the given instance using the given value.
     *
     * @param instance The instance to update.
     * @param name     The name of the field to be updated.
     * @param value    The new value of the field.
     *
     * @throws NoSuchFieldException     if no field with the given name was found.
     * @throws IllegalArgumentException if {@code instance} is not an instance of itself (should
     *                                  never happens).
     * @throws IllegalAccessException   if the field cannot be accessed due to a Java language
     *                                  access control.
     */
    static public void setFieldValue(Object instance, String name, Object value) 
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        if(instance == null)
            throw new IllegalArgumentException("Cannot infer object type : instance is null.");
        setFieldValue(instance.getClass(), instance, name, value);
    }
    
    /**
     * Update the field with the given name in the given instance using the given value.
     *
     * @param hClass   The field's parent class.
     * @param instance The instance to update.
     * @param name     The name of the field to be updated.
     * @param value    The new value of the field.
     *
     * @throws NoSuchFieldException     if no field with the given name was found.
     * @throws IllegalArgumentException if {@code instance} is not an instance of {@code hClass}.
     * @throws IllegalAccessException   if the field cannot be accessed due to a Java language
     *                                  access control.
     */
    static public void setFieldValue(Class<?> hClass, Object instance, String name, Object value)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        getField(hClass, name).set(instance, value);
    }
    

    /**
     * Calls the given static method of the given class, passing the given parameters to it.
     *
     * @param hClass     The method's parent class.
     * @param name       The method's name.
     * @param parameters The parameters to be passed to the method.
     *
     * @return the object the called method returned.
     * @throws NoSuchMethodException     if no method with this name is defined in the class.
     * @throws IllegalAccessException    if the method cannot be accessed due to a Java language
     *                                   access control.
     * @throws IllegalArgumentException  if the method is an instance method; if the number of
     *                                   actual and formal parameters differ; if an unwrapping
     *                                   conversion for primitive arguments fails; or if, after
     *                                   possible unwrapping, a parameter value cannot be converted
     *                                   to the corresponding formal parameter type by a method
     *                                   invocation conversion.
     * @throws InvocationTargetException if an exception is thrown by the called method.
     */
    static public Object call(Class<?> hClass, String name, Object... parameters)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return call(hClass, null, name, parameters);
    }
    
    /**
     * Calls the given method on the given instance, passing the given parameters to it.
     *
     * @param instance   The object the method is invoked from.
     * @param name       The method's name.
     * @param parameters The parameters to be passed to the method.
     *
     * @return the object the called method returned.
     * @throws NoSuchMethodException     if no method with this name is defined in the class.
     * @throws IllegalAccessException    if the method cannot be accessed due to a Java language
     *                                   access control.
     * @throws IllegalArgumentException  if the method is an instance method and the specified
     *                                   object argument is not an instance of the class or
     *                                   interface declaring the underlying method (or of a subclass
     *                                   or implementor thereof); if the number of actual and formal
     *                                   parameters differ; if an unwrapping conversion for
     *                                   primitive arguments fails; or if, after possible
     *                                   unwrapping, a parameter value cannot be converted to the
     *                                   corresponding formal parameter type by a method invocation
     *                                   conversion.
     * @throws InvocationTargetException if an exception is thrown by the called method.
     */
    static public Object call(Object instance, String name, Object... parameters)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        if(instance == null)
            throw new IllegalArgumentException("Cannot infer object type : instance is null.");
        return call(instance.getClass(), instance, name, parameters);
    }
    
    /**
     * Calls the given method on the given instance, passing the given parameters to it.
     *
     * @param hClass     The method's parent class.
     * @param instance   The object the method is invoked from.
     * @param name       The method's name.
     * @param parameters The parameters to be passed to the method.
     *
     * @return the object the called method returned.
     * @throws NoSuchMethodException     if no method with this name is defined in the class.
     * @throws IllegalAccessException    if the method cannot be accessed due to a Java language
     *                                   access control.
     * @throws IllegalArgumentException  if the method is an instance method and the specified
     *                                   object argument is not an instance of the class or
     *                                   interface declaring the underlying method (or of a subclass
     *                                   or implementor thereof); if the number of actual and formal
     *                                   parameters differ; if an unwrapping conversion for
     *                                   primitive arguments fails; or if, after possible
     *                                   unwrapping, a parameter value cannot be converted to the
     *                                   corresponding formal parameter type by a method invocation
     *                                   conversion.
     * @throws InvocationTargetException if an exception is thrown by the called method.
     */
    static public Object call(Class<?> hClass, Object instance, String name, Object... parameters)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Method method;
        final Class[] types = getTypes(parameters);

        try
        {
            method = hClass.getMethod(name, types);
        }
        catch (NoSuchMethodException ex)
        {
            method = hClass.getDeclaredMethod(name, types);
        }

        method.setAccessible(true);
        return method.invoke(instance, parameters);
    }
    
    /**
     * Returns if a given class has a method matching the given parameters.
     * @param hClass The class.
     * @param name The name of the method to look for
     * @param parameterTypes The parameter types to look for
     * @return If the method exists in the given class, or not
     */
    static public boolean hasMethod(Class<?> hClass, String name, Class... parameterTypes)
    {
        try
        {
            try
            {
                hClass.getMethod(name, parameterTypes);
            }
            catch (NoSuchMethodException ex)
            {
                hClass.getDeclaredMethod(name, parameterTypes);
            }
        }
        catch (NoSuchMethodException | SecurityException ex)
        {
            return false;
        }
        return true;
    }
    
    static public Method findMethod(Class<?> hClass, String name, Type... parameterTypes)
    {
        return findMethod(hClass, name, null, 0, parameterTypes);
    }
    
    static public Method findMethod(Class<?> hClass, String name, int modifiers, Type... parameterTypes)
    {
        return findMethod(hClass, name, null, modifiers, parameterTypes);
    }
    
    static public Method findMethod(Class<?> hClass, String name, Type returnType, int modifiers, Type... parameterTypes)
    {
        List<Method> methods = findAllMethods(hClass, name, returnType, modifiers, parameterTypes);
        
        if(methods.isEmpty()) return null;
        
        return methods.get(0);
    }
    
    static public List<Method> findAllMethods(Class<?> hClass, String name, Type returnType, int modifiers, Type... parameterTypes)
    {
        List<Method> methods = new ArrayList<>();
        
        methods: for(Method method : hClass.getMethods())
        {
            if(!nameMatches(method.getName(), name)) continue;
            if(!hasModifiers(method.getModifiers(), modifiers)) continue;
            if(returnType != null && !typeIsAssignableFrom(method.getGenericReturnType(), returnType)) continue;
            
            Type[] methodTypes = method.getGenericParameterTypes();
            if(parameterTypes.length != methodTypes.length) continue;
            
            for(int i = 0; i < methodTypes.length; ++i)
            {
                if(parameterTypes[i] == null) continue;
                if(!typeIsAssignableFrom(parameterTypes[i], methodTypes[i]))
                    continue methods;
            }
            
            methods.add(method);
        }
        
        return methods;
    }
    
    static private boolean nameMatches(String methodName, String pattern)
    {
        if(pattern == null) return true;
        
        if(pattern.startsWith("!"))
        {
            return !pattern.equals(methodName);
        }
        else
        {
            return pattern.equals(methodName);
        }
    }
    
    static public boolean typeIsAssignableFrom(Type source, Type destination)
    {
        if(source instanceof ParameterizedType) source = ((ParameterizedType) source).getRawType();
        if(destination instanceof ParameterizedType) source = ((ParameterizedType) destination).getRawType();

        if(source instanceof Class && destination instanceof Class)
        {
            return ((Class<?>)destination).isAssignableFrom((Class) source);
        }
        
        return source.equals(destination);
    }
    
    static public boolean hasModifiers(int modifiers, int requiredModifiers)
    {
        for(int bit = Integer.SIZE; bit --> 0;)
        {
            int modifier = 1 << bit;
            boolean modifierRequired = (requiredModifiers & modifier) != 0;
            boolean modifierPresent = (modifiers & modifier) != 0;
            
            if(modifierRequired && !modifierPresent) return false;
        }
        
        return true;
    }
    
    /**
     * Creates and returns an instance of the given class, passing the given parameters to the
     * appropriate constructor.
     *
     * @param <T>        The type of the object to be instanciated.
     * @param hClass     The class to be instantiated.
     * @param parameters The parameters to be passed to the constructor. This also determines which
     *                   constructor will be called.
     *
     * @return the created instance.
     * @throws NoSuchMethodException     if no constructor with these parameters types exists.
     * @throws InstantiationException    if the class cannot be instantiated, due to a
     *                                   non-accessible or non-existent constructor, the class being
     *                                   an abstract one or an interface, a primitive type, or
     *                                   {@code void}.
     * @throws IllegalAccessException    if the constructor cannot be accessed due to a Java
     *                                   language access control.
     * @throws IllegalArgumentException  if the number of actual and formal parameters differ; if an
     *                                   unwrapping conversion for primitive arguments fails; or if,
     *                                   after possible unwrapping, a parameter value cannot be
     *                                   converted to the corresponding formal parameter type by a
     *                                   method invocation conversion; if this constructor pertains
     *                                   to an enum type.
     * @throws InvocationTargetException if an exception is thrown in the constructor.
     */
    static public <T> T instantiate(Class<T> hClass, Object... parameters)
            throws NoSuchMethodException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Constructor<T> constructor = hClass.getDeclaredConstructor(getTypes(parameters));
        constructor.setAccessible(true);
        return constructor.newInstance(parameters);
    }
    
    static public <T> Constructor findConstructor(Class<T> hClass, int parameterCount)
    {
        for(Constructor constructor: hClass.getDeclaredConstructors())
        {
            if(constructor.getParameterTypes().length == parameterCount)
                return constructor;
        }
        return null;
    }


    /**
     * Returns an array of the same size of the given array, containing the types of the objects in
     * the given array, in the same order.
     *
     * @param objects The original array.
     *
     * @return an array with the types of the items in the original array.
     */
    static public Class[] getTypes(Object[] objects)
    {
        Class[] types = new Class[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            types[i] = objects[i].getClass();
        }
        return types;
    }
    
    static public <T> Class<? extends T> getCallerClass(Class<T> baseType)
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++)
        {
            if (stackTrace[i].getClassName().equals(baseType.getName()))
                continue;

            Class caller;
            try
            {
                caller = Class.forName(stackTrace[i].getClassName());
            }
            catch (ClassNotFoundException ex)
            {
                continue;
            }

            if (baseType.isAssignableFrom(caller))
                return caller;
        }

        return null;
    }
    
    static public <T> Class<T> getDeclaringClass(T value)
    {
        if(value == null) return null;
        if(value instanceof Enum) return ((Enum)value).getDeclaringClass();
        return (Class<T>) value.getClass();
    }
    
    static public Class getClosestType(Class reference, Collection<Class> candidates)
    {
        ArrayList<Class> remainingCandidates = new ArrayList(candidates);
        
        
        //Remove incompatible candidates
        for(Class klass : candidates)
        {
            if(!klass.isAssignableFrom(reference))
                remainingCandidates.remove(klass);
        }
        
        
        if(remainingCandidates.isEmpty()) return null;
        if(remainingCandidates.size() == 1) return remainingCandidates.get(0);
        
        
        Class currentBest = remainingCandidates.get(0);
        //Find best candidate
        for(Class klass : remainingCandidates)
        {
            if(klass == currentBest) continue;
            if(currentBest.isAssignableFrom(klass))
            {
                currentBest = klass;
            }
        }
        
        return currentBest;
    }
    
    static public Class getClosestType(Class reference, Class... candidates)
    {
        return getClosestType(reference, Arrays.asList(candidates));
    }
}
