/*
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
package fr.zcraft.zlib;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ZLib 
{
    static private JavaPlugin plugin;
    
    static public void init(JavaPlugin plugin)
    {
        ZLib.plugin = plugin;
    }
    
    static public JavaPlugin getPlugin() throws IllegalStateException
    {
        if(plugin == null)
            throw new IllegalStateException("Assertion failed : ZLib is not correctly inizialized");
        return plugin;
    }
    
    static public boolean isInitialized()
    {
        return plugin != null;
    }
}
