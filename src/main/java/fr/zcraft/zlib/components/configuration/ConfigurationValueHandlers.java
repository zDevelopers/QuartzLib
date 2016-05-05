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

package fr.zcraft.zlib.components.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Map;

public abstract class ConfigurationValueHandlers 
{
    static private final Map<Class, Method> valueHandlers = new HashMap<>();
    
    static {
        registerHandlers(ConfigurationValueHandlers.class);
    }
    
    private ConfigurationValueHandlers() {}
    
    static public void registerHandlers(Class handlersClass)
    {
        for(Method method : handlersClass.getMethods())
        {
            if(!Modifier.isStatic(method.getModifiers())) continue;
            ConfigurationValueHandler annotation = method.getAnnotation(ConfigurationValueHandler.class);
            if(annotation == null) continue;
            if(annotation.value().length == 0)
            {
                valueHandlers.put(method.getReturnType(), method);
            }
            else
            {
                for(Class klass : annotation.value())
                {
                    valueHandlers.put(klass, method);
                }
            }
        }
    }
    
    static public <T> T handleValue(Object obj, Class<T> klass) throws ConfigurationParseException
    {
        if(obj == null) return null;
        if(klass == null) return (T) obj;//yolocast, strongly deprecated
        
        if(klass.isAssignableFrom(obj.getClass())) return (T) obj;
        
        Method handler = valueHandlers.get(klass);
        if(handler == null) 
            throw new UnsupportedOperationException("Unsupported configuration type : " + klass.getName());
        
        try
        {
            return (T) handler.invoke(null, obj);
        }
        catch (IllegalAccessException | IllegalArgumentException ex)
        {
            throw new RuntimeException("Unable to call handler for type " + klass.getName(), ex);
        }
        catch (InvocationTargetException ex)
        {
            if(ex.getCause() instanceof ConfigurationParseException)
                throw (ConfigurationParseException) ex.getCause();
            
            throw new RuntimeException("Error while calling handler for type " + klass.getName(), ex.getCause());
        }
    }
    
    @ConfigurationValueHandler({Boolean.class, boolean.class})
    static public boolean handleBoolValue(Object obj) throws ConfigurationParseException 
    {
        return Boolean.parseBoolean(obj.toString());
    }
    
    @ConfigurationValueHandler({Byte.class, byte.class})
    static public byte handleByteValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Byte.parseByte(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid byte value", obj);
        }
    }
    
    @ConfigurationValueHandler({Short.class, short.class})
    static public short handleShortValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Short.parseShort(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid short value", obj);
        }
    }
    
    
    @ConfigurationValueHandler({Integer.class, int.class})
    static public int handleIntValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Integer.parseInt(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid integer value", obj);
        }
    }
    
    @ConfigurationValueHandler({Long.class, long.class})
    static public long handleLongValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Long.parseLong(obj.toString(), 10);
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid long value", obj);
        }
    }
    
    @ConfigurationValueHandler({Float.class, float.class})
    static public float handleFloatValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Float.parseFloat(obj.toString());
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid float value", obj);
        }
    }
    
    @ConfigurationValueHandler({Double.class, double.class})
    static public double handleDoubleValue(Object obj) throws ConfigurationParseException 
    {
        try
        {
            return Double.parseDouble(obj.toString());
        }
        catch(NumberFormatException ex)
        {
            throw new ConfigurationParseException("Invalid double value", obj);
        }
    }
    
    @ConfigurationValueHandler({Character.class, char.class})
    static public char handleCharValue(Object obj) throws ConfigurationParseException 
    {
        String str = obj.toString();
        if(str.length() > 1)
            throw new ConfigurationParseException("String is too long to fit in a single character", obj);
        return str.charAt(0);
    }
    
    @ConfigurationValueHandler
    static public String handleStringValue(Object obj) throws ConfigurationParseException 
    {
        return obj.toString();
    }
    
    @ConfigurationValueHandler
    static public Locale handleLocaleValue(Object obj) throws ConfigurationParseException 
    {
        if(obj.toString().isEmpty()) 
            return null;
        
        try
        {
            return new Locale.Builder().setLanguageTag(obj.toString()).build();
        }
        catch(IllformedLocaleException ex)
        {
            throw new ConfigurationParseException("Illegal language tag : " + ex.getMessage(), obj);
        }
    }
}
