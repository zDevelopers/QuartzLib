/*
 * Copyright or © or Copr. ZLib contributors (2015)
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
package fr.zcraft.zlib.components.gui;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.tools.PluginLogger;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class Gui extends ZLibComponent
{
    /**
     * A map of all the currently open GUIs, associated to the HumanEntity
     * that requested it.
     */
    static private HashMap<Player, GuiBase> openGuis = null;
    
    /**
     * A map of all the currently registered GUIs listeners.
     */
    static private HashMap<Class<? extends Listener>, Listener> guiListeners = null;
    
    /**
     * The plugin that uses the GUI API.
     */
    static private Plugin plugin = null;
    
    @Override
    protected void onEnable()
    {
        openGuis = new HashMap<>();
        guiListeners = new HashMap<>();
        Gui.plugin = ZLib.getPlugin();
        GuiUtils.init();
    }
    
    @Override
    protected void onDisable()
    {
        openGuis.clear();
        guiListeners.clear();
        openGuis = null;
        guiListeners = null;
    }
    
    static protected final void registerListener(Class<? extends Listener> listenerClass)
    {
        if(guiListeners == null || guiListeners.containsKey(listenerClass))
            return;
        
        try
        {
            Constructor<? extends Listener> constructor = listenerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Listener listener = constructor.newInstance();
            guiListeners.put(listenerClass, listener);
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        catch(Throwable ex)
        {
            PluginLogger.error("Could not register listener for GUI", ex);
        }
    }
    
    /**
     * Opens a GUI for a player.
     * @param <T> A GUI type.
     * @param owner The player the GUI will be shown to.
     * @param gui The GUI.
     * @param parent The parent of the newly created GUI. Can be null.
     * @return The opened GUI.
     */
    static public final <T extends GuiBase> T open(final Player owner, final T gui, final GuiBase parent)
    {
        GuiBase openGui = openGuis.get(owner);
        if(openGui != null) openGui.onClose();
        if(parent != null) ((GuiBase)gui).setParent(parent);
        
         Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run()
                {
                    ((GuiBase)gui).open(owner);/* JAVA GENERICS Y U NO WORK */
                }
            }, 0);
        
        return gui;
    }
    
    /**
     * Opens a GUI for a player.
     * @param <T> A GUI type.
     * @param owner The player the GUI will be shown to.
     * @param gui The GUI.
     * @return The opened GUI.
     */
    static public final <T extends GuiBase> T open(Player owner, T gui)
    {
        return open(owner, gui, null);
    }
    
    /**
     * Closes any open GUI for a given player.
     * @param owner The player.
     */
    static public final void close(Player owner)
    {
        GuiBase openGui = openGuis.get(owner);
        if(openGui != null) openGui.close();
    }
    
    static public final GuiBase getOpenGui(HumanEntity entity)
    {
        if(!(entity instanceof Player)) return null;
        return openGuis.get((Player) entity);
    }
    
    static public final <T extends GuiBase> T getOpenGui(HumanEntity entity, Class<T> guiClass)
    {
        GuiBase openGui = getOpenGui(entity);
        if(openGui == null) return null;
        if(!guiClass.isAssignableFrom(openGui.getClass())) return null;
        return (T) openGui;
    }
    
    static final void registerGuiOpen(Player player, GuiBase gui)
    {
        openGuis.put(player, gui);
    }
    
    static final void registerGuiClose(GuiBase gui)
    {
        openGuis.remove(gui.getPlayer());
    }
    
    
}
