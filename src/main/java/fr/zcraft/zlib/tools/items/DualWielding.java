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

package fr.zcraft.zlib.tools.items;

import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;

/**
 * This class provides various utilities for handling dual-wielding.
 */
public enum DualWielding 
{
    /**
     * Represents the main hand of the player.
     */
    MAIN_HAND,
    /**
     * Represents the off hand of the player.
     */
    OFF_HAND;
    
    static private Boolean available = null;
    
    // Dual-wielding isn't available in all builds of Bukkit/Spigot
    static private void init()
    {
        available = Reflection.hasMethod(PlayerInventory.class, "getItemInMainHand")
                && Reflection.hasMethod(PlayerInventory.class, "getItemInOffHand")
                && Reflection.hasMethod(PlayerInventory.class, "setItemInMainHand", ItemStack.class)
                && Reflection.hasMethod(PlayerInventory.class, "setItemInOffHand", ItemStack.class);
    }
    
    static private boolean checkAvailable()
    {
        if (available == null) init();
        return available;
    }
    
    /**
     * Retrieves the item in the given player's hand.
     * If dual-wielding is not available, it is retrieved from the players' only hand.
     * @param player The player to get the item from
     * @param hand The hand 
     * @return The retrieved item.
     */
    static public ItemStack getItemInHand(Player player, DualWielding hand)
    {
        try
        {
            if(!checkAvailable()) return player.getItemInHand();
            
            switch(hand)
            {
                case MAIN_HAND:
                    return (ItemStack) Reflection.call(player.getInventory(), "getItemInMainHand");
                case OFF_HAND:
                    return (ItemStack) Reflection.call(player.getInventory(), "getItemInOffHand");
                default:
                    return player.getItemInHand();
            }
        }
        catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            return player.getItemInHand();
        }
    }
    
    /**
     * Sets the item in the given player's hand.
     * If dual-wielding is not available, it is put in the players' only hand.
     * @param player The player
     * @param hand The player's hand
     * @param item The item to put in the specified hand.
     */
    static public void setItemInHand(Player player, DualWielding hand, ItemStack item)
    {
        if(hand == null) return;
        
        if(!checkAvailable())
        {
            player.setItemInHand(item);
            return;
        }
            
        try
        {
            switch(hand)
            {
                case MAIN_HAND:
                    Reflection.call(player.getInventory(), "setItemInMainHand", item);
                    break;
                case OFF_HAND:
                    Reflection.call(player.getInventory(), "setItemInOffHand", item);
                    break;
                default:
                    player.setItemInHand(item);
            }
        }
        catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            player.setItemInHand(item);
        }
    }
    
    /**
     * Returns which player's hand is holding the specific item.
     * If dual-wielding is not available, the item is tested against the 
     * player's only hand.
     * @param player The player
     * @param item The item
     * @return The hand holding the given item, or null if neither of them is holding it.
     */
    static public DualWielding getHoldingHand(Player player, ItemStack item)
    {
        if(!checkAvailable())
            return player.getItemInHand().equals(item) ? MAIN_HAND : null;
        
        if(getItemInHand(player, OFF_HAND).equals(item))
            return OFF_HAND;
        
        if(getItemInHand(player, MAIN_HAND).equals(item))
            return MAIN_HAND;
        
        return null;
    }
}
