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

import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.runners.RunTask;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
//import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for dealing with items and inventories.
 */
abstract public class ItemUtils
{
    private ItemUtils() {}
    
    static private boolean addItemFlagLoaded = false;
    static private Method addItemFlagsMethod = null;
    static private Object[] itemFlagValues = null;

    /**
     * Initializes the Item utilities.
     */
    static private void init()
    {
        if (addItemFlagLoaded) return;
        
        try
        {
            Class<?> itemFlagClass = Class.forName("org.bukkit.inventory.ItemFlag");
            Method valuesMethod = itemFlagClass.getDeclaredMethod("values");
            itemFlagValues = (Object[]) valuesMethod.invoke(null);
            addItemFlagsMethod = ItemMeta.class.getMethod("addItemFlags", itemFlagClass);
            addItemFlagsMethod.setAccessible(true);
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e)
        {
            // Not supported :c
        }
        catch (InvocationTargetException e)
        {
            PluginLogger.error("Exception occurred while looking for the ItemFlag API.", e.getCause());
        }
        
        addItemFlagLoaded = true;
    }

    /**
     * Hides all the item attributes of the given {@link ItemMeta}.
     *
     * @param meta The {@link ItemMeta} to hide attributes from.
     * @return The same item meta. The modification is applied by reference, the
     * stack is returned for convenience reasons.
     */
    static public ItemMeta hideItemAttributes(ItemMeta meta)
    {
        if (!addItemFlagLoaded) init();
        
        if (addItemFlagsMethod == null)
        {
            return meta;
        }

        try
        {
            addItemFlagsMethod.invoke(meta, itemFlagValues);
        }
        catch (IllegalAccessException ex)
        {
            PluginLogger.error("IllegalAccessException caught while invoking the ItemMeta.addItemFlags method.", ex);
        }
        catch (InvocationTargetException ex)
        {
            PluginLogger.error("Exception occurred while invoking the ItemMeta.addItemFlags method.", ex.getCause());
        }

        return meta;
    }

    /**
     * Hides all the item attributes of the given {@link ItemStack}.
     *
     * <p>
     * Warning: this will update the ItemMeta, clearing the effects of, as
     * example, {@link fr.zcraft.zlib.tools.items.GlowEffect}.</p>
     *
     * @param stack The {@link ItemStack} to hide attributes from.
     * @return The same item stack. The modification is applied by reference,
     * the stack is returned for convenience reasons.
     */
    static public ItemStack hideItemAttributes(ItemStack stack)
    {
        ItemMeta meta = stack.getItemMeta();
        hideItemAttributes(meta);
        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Simulates the player consuming the itemstack in its hand, depending on
     * his game mode. This decreases the ItemStack's size by one, and replaces
     * it with air if nothing is left.
     *
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
     * Simulates the player consuming the given itemstack, depending on his game
     * mode. This decreases the ItemStack's size by one, and replaces it with
     * air if nothing is left.
     *
     * @param player The player that will consume the stack.
     * @param item The stack to be consumed.
     * @return The updated stack.
     */
    static public ItemStack consumeItem(Player player, ItemStack item)
    {
        if(player.getGameMode() == GameMode.CREATIVE)
            return item;

        int amount = item.getAmount();
        if (amount == 1)
        {
            item.setType(Material.AIR);
        }
        else
        {
            item.setAmount(amount - 1);
        }
        return item;
    }

    /**
     * Emulates the behaviour of the /give command
     * @param player The player to give the item to.
     * @param item The item to give to the player
     * @return true if the player received the item in its inventory, false if it had to be dropped on the ground.
     */
    static public boolean give(Player player, ItemStack item)
    {
        if (!player.getInventory().addItem(item).isEmpty())
        {
            // Inventory was full
            player.getWorld().dropItem(player.getLocation(), item);
            return false;
        }
        else
        {
            //Sounds will be added later
            //player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.2f, 1.8f);
            return true;
        }
    }

    /**
     * Shortcut method to set the display name of an item
     * @param item The item 
     * @param displayName The new display name of the item
     * @return The same item.
     */
    static public ItemStack setDisplayName(ItemStack item, String displayName)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Emulates the item inthe player loosing durability as if the given player was using it.
     * If the player is in creative mode, the item won't be damaged at all.
     * The Unbreaking enchantments are taken into account.
     * The player's inventory is also updated, if needed.
     * @param player The player that is using the item.
     * @param item The item in the player's hand.
     * @param factor The amount of damage taken.
     */
    static public void damageItemInHand(Player player, ItemStack item, int factor)
    {
        if(player == null) throw new IllegalArgumentException("Player can't be null.");
        if (player.getGameMode() == GameMode.CREATIVE) return;
        
        short newDurability = item.getDurability();
        newDurability += newDurability(item.getEnchantmentLevel(Enchantment.DURABILITY)) * factor;

        if (newDurability >= item.getType().getMaxDurability())
        {
            breakItemInHand(player, item);
        }
        else
        {
            item.setDurability(newDurability);
            //player.getInventory().setItemInHand(item);
        }

        player.updateInventory();
    }
    
    static public void breakItemInHand(Player player, DualWielding hand)
    {
        DualWielding.setItemInHand(player, hand, new ItemStack(Material.AIR));
        //player.playSound(player.getLocation(), Sound.ITEM_BREAK, 0.8f, 1);
    }
    
    static public void breakItemInHand(Player player, ItemStack item)
    {
        breakItemInHand(player, DualWielding.getHoldingHand(player, item));
    }
    
    /**
     * Calculates the new durability, taking into account the unbreaking
     * enchantment.
     *
     * @param unbreakingLevel The Unbreaking level (0 if not enchanted with
     * that).
     * @return The durability to add.
     */
    static private short newDurability(int unbreakingLevel)
    {
        if (new Random().nextInt(100) <= (100 / (unbreakingLevel + 1)))
        {
            return 1;
        }

        return 0;
    }
    
    /**
     * Naturally "drops" the item at the given location.
     * @param location The location to drop the item at.
     * @param item The item to drop.
     */
    static public void dropNaturally(Location location, ItemStack item)
    {
        location.getWorld().dropItemNaturally(location, item);
    }
    
    /**
     * Naturally "drops" the item at the given location, at the next server tick.
     * @param location The location to drop the item at.
     * @param item The item to drop.
     */
    static public void dropNaturallyLater(Location location, ItemStack item)
    {
        RunTask.nextTick(new DropLaterTask(location, item));
    }
    
    static private class DropLaterTask implements Runnable
    {
        private final Location location;
        private final ItemStack item;
        
        public DropLaterTask(Location location, ItemStack item)
        {
            this.location = location;
            this.item = item;
        }
        
        @Override
        public void run()
        {
            dropNaturally(location, item);
        }
    }
}
