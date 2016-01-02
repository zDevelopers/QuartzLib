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

import com.google.common.collect.ImmutableSet;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public abstract class ZLib
{
    static private JavaPlugin plugin;
    static private final Set<ZLibComponent> loadedComponents = new CopyOnWriteArraySet<>();


    /**
     * Initializes the ZLibrary.
     *
     * This needs to be called before anything else; otherwise, you will encounter
     * {@link IllegalStateException}s.
     *
     * This method also initializes the {@link PluginLogger}, used by the zLib.
     *
     * @param plugin The plugin currently using this instance of the ZLib.
     */
    static public void init(JavaPlugin plugin)
    {
        ZLib.plugin = plugin;

        PluginLogger.init();
    }

    /**
     * Loads a ZLib component, and store it as loaded to automatically unload it when needed.
     *
     * @param component The component to load.
     * @throws IllegalStateException if the zLib was not initialized.
     */
    static void loadComponent(ZLibComponent component) throws IllegalStateException
    {
        checkInitialized();

        if(loadedComponents.add(component))
        {
            component.setEnabled(true);
        }
    }

    /**
     * Unloads all the registered components.
     *
     * This method is automatically called when the plugin is unloaded.
     *
     * @throws IllegalStateException if the ZLib was not initialized.
     */
    static void unloadComponents()
    {
        checkInitialized();

        for(ZLibComponent component : loadedComponents)
        {
            component.setEnabled(false);
        }

        loadedComponents.clear();
    }

    /**
     * Returns the plugin currently using the library.
     *
     * @return The plugin currently using the library.
     * @throws IllegalStateException if the ZLib was not initialized.
     */
    static public JavaPlugin getPlugin() throws IllegalStateException
    {
        checkInitialized();
        return plugin;
    }

    /**
     * Returns the currently loaded ZLib components.
     *
     * This returns a copy of the components list,
     *
     * @return the loaded components.
     * @throws IllegalStateException
     */
    static public Set<ZLibComponent> getLoadedComponents() throws IllegalStateException
    {
        checkInitialized();
        return ImmutableSet.copyOf(loadedComponents);
    }

    /**
     * Check wherever the ZLib is correctly initialized.
     *
     * @return {@code true} if initialized.
     */
    static public boolean isInitialized()
    {
        return plugin != null;
    }

    /**
     * Check wherever the ZLib is correctly initialized.
     *
     * @throws IllegalStateException if the ZLib is not initialized.
     */
    static private void checkInitialized() throws IllegalStateException
    {
        if(plugin == null)
            throw new IllegalStateException("Assertion failed: ZLib is not correctly inizialized");
    }
}
