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

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for dealing with items and inventories.
 */
abstract public class ItemUtils 
{
    private ItemUtils() {}
    
    
    /**
     * Simulates the player consuming the itemstack in its hand, depending on his game mode.
     * This decreases the ItemStack's size by one, and replaces it with air if nothing is left.
     * @param player The player that will consume the stack.
     * @return The updated stack.
     */
    static public ItemStack consumeItem(Player player)
    {
        ItemStack newStack = consumeItem(player, player.getItemInHand());
        player.setItemInHand(newStack);
        return newStack;
    }
    
    /**
     * Simulates the player consuming the given itemstack, depending on his game mode.
     * This decreases the ItemStack's size by one, and replaces it with air if nothing is left.
     * @param player The player that will consume the stack.
     * @param item The stack to be consumed.
     * @return The updated stack.
     */
    static public ItemStack consumeItem(Player player, ItemStack item)
    {
        if(player.getGameMode() == GameMode.CREATIVE)
            return item;
        
        int amount = item.getAmount();
        if(amount == 1)
        {
            item.setType(Material.AIR);
        }
        else
        {
            item.setAmount(amount - 1);
        }
        return item;
    }
    
    static public void give(Player player, ItemStack item)
    {
        if (!player.getInventory().addItem(item).isEmpty())
        {
            // Inventory was full
            player.getWorld().dropItem(player.getLocation(), item);
        }
        else
        {
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.2f, 1.8f);
        }
    }
    
    static public ItemStack setDisplayName(ItemStack item, String displayName)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
}
