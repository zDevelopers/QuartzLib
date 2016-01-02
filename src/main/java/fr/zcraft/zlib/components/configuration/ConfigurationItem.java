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

import fr.zcraft.zlib.core.ZLib;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;


/**
 * Represents a configuration item stored in the config.yml file.
 *
 * @param <T> The type of the stored value.
 */
public class ConfigurationItem<T>
{
    private final String fieldName;
    private final T defaultValue;
    private final String[] deprecatedNames;

    /**
     * @param fieldName The path of the field in the {@code config.yml} file.
     * @param defaultValue The default value if this is not defined.
     * @param deprecatedNames A list of deprecated names to migrate the old values automatically.
     */
    public ConfigurationItem(String fieldName, T defaultValue, String ... deprecatedNames)
    {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.deprecatedNames = deprecatedNames;
    }

    /**
     * @return the defined value for this configuration item, or the default value if missing.
     */
    public T get()
    {
        return get(fieldName, defaultValue);
    }

    /**
     * @return the default value for this configuration item.
     */
    public T getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * @return the path in the configuration file.
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * @return {@code true} if a value is explicitly set in the configuration file.
     */
    public boolean isDefined()
    {
        return getConfig().contains(fieldName);
    }

    /**
     * Updates the value of this configuration item. Saves the change in the configuration file.
     *
     * If you don't want to save the update, use {@link #set(Object,Boolean) set(value, false)}.
     *
     * @param value the new value.
     * @return The previously stored value.
     *
     * @see #set(Object, Boolean)
     */
    public T set(T value)
    {
        return set(value, true);
    }

    /**
     * Updates the value of this configuration item.
     *
     * @param value the new value.
     * @param save {@code true} to save this change in the config file. Be aware that it will save all unsaved changes,
     *             including previous values changed with this argument set to {@code false}.
     *
     * @return The previously stored value.
     */
    public T set(T value, Boolean save)
    {
        T oldValue = get();
        getConfig().set(fieldName, value);

        Configuration.triggerCallback(this);

        if (save)
            Configuration.save();

        return oldValue;
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

    @SuppressWarnings("unchecked")
    static private <T> T get(String path, T defaultValue)
    {
        if (defaultValue instanceof String)
            return (T) getConfig().getString(path, (String) defaultValue);

        else if (defaultValue instanceof Boolean)
            return (T) (Boolean) getConfig().getBoolean(path, (Boolean) defaultValue);

        else if (defaultValue instanceof Integer)
            return (T) (Integer) getConfig().getInt(path, (Integer) defaultValue);

        else if (defaultValue instanceof Double)
            return (T) (Double) getConfig().getDouble(path, (Double) defaultValue);

        else if (defaultValue instanceof Long)
            return (T) (Long) getConfig().getLong(path, (Long) defaultValue);

        else if (defaultValue instanceof List)
            return (T) getConfig().getList(path, (List<?>) defaultValue);
        
        else
            return (T) getConfig().get(path, defaultValue);
    }


    /**
     * Utility method to construct a configuration item.
     *
     * @param fieldName The path of the field in the {@code config.yml} file.
     * @param defaultValue The default value if this is not defined.
     * @param deprecatedNames A list of deprecated names to migrate the old values automatically.
     * @param <T> The type of the stored value.
     *
     * @return A ready-to-use configuration item.
     */
    static public <T> ConfigurationItem<T> item(String fieldName, T defaultValue, String... deprecatedNames)
    {
        return new ConfigurationItem<>(fieldName, defaultValue, deprecatedNames);
    }
}
