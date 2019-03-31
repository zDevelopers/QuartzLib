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
package fr.zcraft.zlib.core;

import fr.zcraft.zlib.tools.PluginLogger;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.jar.JarFile;

/**
 * The base class of any plugin using the ZLib.
 *
 * To use the ZLib, you have to use this class instead of {@link JavaPlugin},
 * and to add calls to the {@code super} methods of
 * {@link JavaPlugin#onEnable()} and {@link JavaPlugin#onDisable()} (if you use
 * them).
 */
public abstract class ZPlugin extends JavaPlugin
{
	
	public final String minimumSupportedMinecraftVersion = "1_13";
	
	private boolean checkIsVersionSupported() {
		
		String packagename = this.getServer().getClass().getPackage().getName();
		String versionRaw = packagename.substring(packagename.lastIndexOf('.') + 1).replace("v", "");
		
		String[] versionBits = versionRaw.substring(0, versionRaw.length()-3).split("_");
		String[] minimumVerBits = minimumSupportedMinecraftVersion.split("_");
		
		int length = versionBits.length>minimumVerBits.length ? versionBits.length: minimumVerBits.length ;
		
		for(int i = 0; i<length ;i++) {
			int minimum = i < minimumVerBits.length ? Integer.parseInt(minimumVerBits[i]) : 0;
			int current = i < versionBits.length? Integer.parseInt(versionBits[i]) : 0;
			if(current<minimum) {
				return false;
			} 
			if(current>minimum) {
				return true;
			}
			
			
		}
		return true;
	}
	
    @Override
    public void onLoad()
    {
    	if(!checkIsVersionSupported()) {
    		getLogger().severe("This Plugin is powered by zLib-1.13 which requires the Server to be at least on 1.13!");
    		getLogger().severe("Plugin is now shutting down! If you ARE running 1.13 or newer please contact the");
    		getLogger().severe("Developers of zLib and file a Bug report. ");
    		getLogger().severe("!!WARNING!! If you ignore this message you will not get any support whatsoever from us !!WARNING!!");
    		this.getPluginLoader().disablePlugin(this);
    		return;
    	}
        ZLib.init(this);
    }

    /**
     * Load the given ZLib's components.
     *
     * @param components The base classes of the components to load.
     */
    @SafeVarargs
    public final void loadComponents(Class<? extends ZLibComponent>... components)
    {
        for (Class<? extends ZLibComponent> componentClass : components)
        {
            ZLib.loadComponent(componentClass);
        }
    }
    
    /**
     * Tries to load a given component.
     * @param <T> The type of the component.
     * @param componentClass The component's class.
     * @return The component instance, or null if instanciation failed.
     */
    public <T extends ZLibComponent> T loadComponent(Class<T> componentClass)
    {
        return ZLib.loadComponent(componentClass);
    }

    public JarFile getJarFile()
    {
        try
        {
            return new JarFile(getFile());
        }
        catch (IOException e)
        {
            PluginLogger.error("Unable to load JAR file {0}", e, getFile().getAbsolutePath());
            return null;
        }
    }
}
