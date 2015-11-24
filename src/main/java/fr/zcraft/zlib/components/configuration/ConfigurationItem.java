/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.configuration;

import fr.zcraft.zlib.core.ZLib;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationItem<T>
{
    private final String fieldName;
    private final T defaultValue;
    private final String[] deprecatedNames;
    
    public ConfigurationItem(String fieldName, T defaultValue, String ... deprecatedNames)
    {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.deprecatedNames = deprecatedNames;
    }
    
    public T get()
    {
        return get(fieldName, defaultValue);
    }
    
    public T getDefaultValue()
    {
        return defaultValue;
    }
    
    public boolean isDefined()
    {
        return getConfig().contains(fieldName);
    }
    
    @Override
    public String toString()
    {
        return get().toString();
    }
    
    boolean init()
    {
        boolean affected = false;
        
        if(!isDefined())
        {
            getConfig().set(fieldName, defaultValue);
            affected = true;
        }
        
        for(String deprecatedName : deprecatedNames)
        {
            if(getConfig().contains(deprecatedName))
            {
                getConfig().set(fieldName, getConfig().get(deprecatedName));
                getConfig().set(deprecatedName, null);
                affected = true;
            }
        }
        return affected;
    }
    
    static private FileConfiguration getConfig()
    {
        return ZLib.getPlugin().getConfig();
    }
    
    static private <T> T get(String path, T defaultValue)
    {
        if(defaultValue instanceof String)
            return (T) getConfig().getString(path, (String) defaultValue);
        if(defaultValue instanceof Boolean)
            return (T) (Boolean) getConfig().getBoolean(path, (Boolean) defaultValue);
        if(defaultValue instanceof Integer)
            return (T) (Integer) getConfig().getInt(path, (Integer) defaultValue);
        if(defaultValue instanceof Double)
            return (T) (Double) getConfig().getDouble(path, (Double) defaultValue);
        if(defaultValue instanceof Long)
            return (T) (Long) getConfig().getLong(path, (Long) defaultValue);
        
        return (T) getConfig().get(path, defaultValue);
    }
    
    static public <T> ConfigurationItem<T> item(String fieldName, T defaultValue, String... depreceatedNames)
    {
        return new ConfigurationItem(fieldName, defaultValue, depreceatedNames);
    }
    
}
