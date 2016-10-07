/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.configuration;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.tools.Callback;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class ConfigurationInstance extends ZLibComponent
{
    private ConfigurationItem[] items;
    private Callback<ConfigurationItem<?>> updateCallback;
    private final FileConfiguration bukkitConfiguration;
    private final String fileName;
    private final File file;
    
    public ConfigurationInstance(File file)
    {
        this.file = file;
        this.fileName = null;
        this.bukkitConfiguration = new YamlConfiguration();
    }
    
    public ConfigurationInstance(String fileName)
    {
        this.file = null;
        this.fileName = fileName;
        this.bukkitConfiguration = new YamlConfiguration();
    }
    
    ConfigurationInstance(FileConfiguration config)
    {
        this.bukkitConfiguration = config;
        this.file = null;
        this.fileName = null;
    }
    
    public void load() throws IOException, InvalidConfigurationException
    {
        init(this.getClass());

        if (file != null)
        {
            this.bukkitConfiguration.load(file);
        }
        else if (fileName != null)
        {
            this.bukkitConfiguration.load(ZLib.getPlugin().getDataFolder().getAbsolutePath() + "/" + fileName);
        }

        if(!validate())
        {
            // The file path is null when the instance was constructed from a FileConfiguration object directly.
            final String filePath = getFilePath();

            if (filePath != null)
                PluginLogger.warning("Some configuration values are invalid. Please check the ''{0}'' file.", filePath);
            else
                PluginLogger.warning("Some configuration values are invalid. Please check the configuration file.");
        }
    }
    
    public String getFilePath()
    {
        if(file != null) return file.getAbsolutePath();
        return fileName;
    }
    
    @Override
    protected void onEnable() 
    {
        try
        {
            load();
        }
        catch(Exception ex)
        {
            PluginLogger.error("Couldn't read configuration file {0} : {1}", ex, getFilePath(), ex.getMessage());
        }
    }
    
    final void init(Class configurationClass)
    {
        if(items != null) return;
        
        ArrayList<ConfigurationItem> itemsList = new ArrayList<>();
        
        for(Field field : configurationClass.getFields())
        {
            if(ConfigurationItem.class.isAssignableFrom(field.getType()))
            {
                try 
                {
                    ConfigurationItem item = (ConfigurationItem) field.get(this);
                    item.setInstance(this);
                    itemsList.add(item);
                }
                catch(Exception ex){}
            }
        }
        
        items = itemsList.toArray(new ConfigurationItem[itemsList.size()]);
        initFields();
    }
    
    public void save()
    {
        ZLib.getPlugin().saveConfig();
    }
    
    public FileConfiguration getConfig()
    {
        return bukkitConfiguration;
    }

    public void registerConfigurationUpdateCallback(Callback<ConfigurationItem<?>> callback)
    {
        updateCallback = callback;
    }
    
    private void initFields()
    {
        for(ConfigurationItem configField : items)
        {
            configField.init();
        }
    }
    
    private boolean validate()
    {
        boolean isValid = true;
        
        for(ConfigurationItem configField : items)
        {
            if(!configField.validate())
                isValid = false;
        }
        
        return isValid;
    }

    void triggerCallback(ConfigurationItem<?> configurationItem)
    {
        if (updateCallback != null) updateCallback.call(configurationItem);
    }
}
