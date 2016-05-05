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
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.tools.Callback;
import fr.zcraft.zlib.tools.PluginLogger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Configuration extends ZLibComponent
{
    /* ===== Static API ===== */
    static private ConfigurationItem[] items;
    static private Callback<ConfigurationItem<?>> updateCallback;
    
    @Override
    protected void onEnable() 
    {
        init(this.getClass());
    }
    
    static public void init(Class configurationClass)
    {
        ArrayList<ConfigurationItem> itemsList = new ArrayList<>();
        
        for(Field field : configurationClass.getFields())
        {
            if(ConfigurationItem.class.isAssignableFrom(field.getType()))
            {
                try 
                {
                    itemsList.add((ConfigurationItem) field.get(null));
                }
                catch(Exception ex){}
            }
        }
        
        items = itemsList.toArray(new ConfigurationItem[itemsList.size()]);
        loadDefaultValues();
        
        if(!validate())
        {
            PluginLogger.warning("Some configuration values are invalid. Please check your configuration file.");
        }
    }
    
    static public void save()
    {
        ZLib.getPlugin().saveConfig();
    }

    static public void registerConfigurationUpdateCallback(Callback<ConfigurationItem<?>> callback)
    {
        updateCallback = callback;
    }
    
    static private void loadDefaultValues()
    {
        for(ConfigurationItem configField : items)
        {
            configField.init();
        }
    }
    
    static private boolean validate()
    {
        boolean isValid = true;
        
        for(ConfigurationItem configField : items)
        {
            if(!configField.validate())
                isValid = false;
        }
        
        return isValid;
    }

    static void triggerCallback(ConfigurationItem<?> configurationItem)
    {
        if (updateCallback != null) updateCallback.call(configurationItem);
    }
}
