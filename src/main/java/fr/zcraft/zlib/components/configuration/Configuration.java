/*
 * Copyright (C) 2013 Moribus
 * Copyright (C) 2015 ProkopyL <prokopylmc@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.zcraft.zlib.components.configuration;

import fr.zcraft.zlib.ZLib;
import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Configuration
{
    /* ===== Static API ===== */
    static private ConfigurationItem[] items;
    
    static public void init(Class configurationClass)
    {
        ArrayList<ConfigurationItem> itemsList = new ArrayList<>();
        
        for(Field field : configurationClass.getFields())
        {
            if(field.getType().equals(ConfigurationItem.class))
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
    }
    
    static public void save()
    {
        ZLib.getPlugin().saveConfig();
    }
    
    static private void loadDefaultValues()
    {
        boolean affected = false;
        
        for(ConfigurationItem configField : items)
        {
            if(configField.init()) affected = true;
        }
        
        if(affected) save();
    }
    
}
