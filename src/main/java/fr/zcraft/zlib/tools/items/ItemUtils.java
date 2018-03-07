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
import fr.zcraft.zlib.tools.reflection.NMSException;
import fr.zcraft.zlib.tools.reflection.Reflection;
import fr.zcraft.zlib.tools.runners.RunTask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.bukkit.Sound;

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
            try
            {
                addItemFlagsMethod = ItemMeta.class.getMethod("addItemFlags", itemFlagClass);
            }
            catch(NoSuchMethodException ex)
            {
                addItemFlagsMethod = ItemMeta.class.getMethod("addItemFlags", itemFlagValues.getClass());
            }
            
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

    static private Object getItemFlagValue(String flagName)
    {
        if(itemFlagValues == null) return null;
        
        for(Object value : itemFlagValues)
        {
            if(value.toString().equalsIgnoreCase(flagName))
                return value;
        }
        
        return null;
    }
    
    static private Object[] getItemFlagsValues(String... flagsNames)
    {
        List flagsList = new ArrayList();
        for(String flagName : flagsNames)
        {
            Object flag = getItemFlagValue(flagName);
            if(flag != null)
                flagsList.add(flag);
        }
        
        
        Object array = Array.newInstance(itemFlagValues[0].getClass(), flagsList.size());
        return flagsList.toArray((Object[]) array);
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
        return hideItemAttributes(meta, itemFlagValues);
    }
    
    /**
     * Hides the specified item attributes of the given {@link ItemMeta}.
     *
     * @param meta The {@link ItemMeta} to hide attributes from.
     * @param itemFlagsNames The item flags to hide.
     * @return The same item meta. The modification is applied by reference, the
     * stack is returned for convenience reasons.
     */
    static public ItemMeta hideItemAttributes(ItemMeta meta, String... itemFlagsNames)
    {
        if (!addItemFlagLoaded) init();
        return hideItemAttributes(meta, getItemFlagsValues(itemFlagsNames));
    }
    
    static private ItemMeta hideItemAttributes(ItemMeta meta, Object[] itemFlags)
    {
        if (addItemFlagsMethod == null)
        {
            return meta;
        }

        try
        {
            addItemFlagsMethod.invoke(meta, new Object[]{itemFlags});
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
     * Checks if two item stacks are similar, by checking the type, data and display name (if set).
     *
     * @param first An item stack. Can be {@code null}.
     * @param other Another item stack. Can be {@code null}.
     *
     * @return {@code true} if similar (either both {@code null} or similar).
     */
    static public boolean areSimilar(ItemStack first, ItemStack other)
    {
        if (first == null || other == null)
            return first == other;

        return first.getType() == other.getType()
                && first.getData().equals(other.getData())
                && ((!first.hasItemMeta() && !other.hasItemMeta())
                    || (!first.getItemMeta().hasDisplayName() && !other.getItemMeta().hasDisplayName())
                    || (first.getItemMeta().getDisplayName().equals(other.getItemMeta().getDisplayName()))
                   );
    }

    /**
     * Checks if an item stack have the given display name.
     *
     * @param stack An item stack. Can be {@code null}.
     * @param displayName A display name.
     *
     * @return {@code true} if the item stack have the given display name.
     */
    static public boolean hasDisplayName(ItemStack stack, String displayName)
    {
        return stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equals(displayName);
    }

    /**
     * Emulates the item in the player loosing durability as if the given player was using it.
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

    /**
     * Breaks the item currently in the hand of the player.
     *
     * @param player The player.
     * @param hand The hand to retrieve the item from. This will always be the main hand if
     *             the Bukkit build don't support dual-wielding.
     */
    static public void breakItemInHand(Player player, DualWielding hand)
    {
        DualWielding.setItemInHand(player, hand, new ItemStack(Material.AIR));
        //player.playSound(player.getLocation(), Sound.ITEM_BREAK, 0.8f, 1);
    }

    /**
     * Breaks the given item if it is found in one of the player's hands.
     *
     * @param player The player.
     * @param item The item to break.
     */
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
    
    static private String getI18nNameMethodName = null;

    /**
     * Returns the name of the method used to retrieve the I18N key of the name
     * of an item from a NMS ItemStack.
     *
     * @param item An item stack.
     * @return The name of the method. This result is cached.
     *
     * @throws NMSException if the operation cannot be executed.
     */
    static private String getI18nNameMethod(ItemStack item) throws NMSException
    {
        if(getI18nNameMethodName != null) return getI18nNameMethodName;
        
        try
        {
            Class<?> MinecraftItem = Reflection.getMinecraftClassByName("Item");
            Class<?> MinecraftItemStack = Reflection.getMinecraftClassByName("ItemStack");
            Class<?> CraftItemStack = Reflection.getBukkitClassByName("inventory.CraftItemStack");
            Object itemStackHandle = CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object minecraftItem = Reflection.getFieldValue(itemStackHandle, "item");
            
            for(Method method : Reflection.findAllMethods(MinecraftItem, null, String.class, 0, MinecraftItemStack))
            {
                String result = (String) Reflection.call(minecraftItem, method.getName(), itemStackHandle);
                if(result == null) continue;
                if(!(result.startsWith("item.") || result.startsWith("tile.") || result.startsWith("potion."))) continue;
                
                getI18nNameMethodName = method.getName();
                return getI18nNameMethodName;
            }
            
            throw new NMSException("Unable to retrieve Minecraft I18n name: no method found");
        }
        catch (Exception ex)
        {
            throw new NMSException("Unable to retrieve Minecraft I18n name", ex);
        }
    }
    
    static private Method registryLookupMethod = null;

    /**
     * Retrieves the method used to lookup the Minecraft internal item registry,
     * used to get the internal names of the Minecraft items (like {@code minecraft.stone}.
     *
     * @return The method. This result is cached.
     *
     * @throws NMSException if the operation cannot be executed.
     */
    static private Method getRegistryLookupMethod() throws NMSException
    {
        if(registryLookupMethod != null) return registryLookupMethod;
        
        try
        {
            Class MinecraftItem = Reflection.getMinecraftClassByName("Item");
            Object MaterialsRegistry = Reflection.getFieldValue(MinecraftItem, null, "REGISTRY");
            
            Method getMethod = Reflection.findMethod(MaterialsRegistry.getClass(), "get", (Type) null);
            
            if(getMethod == null)
                throw new NMSException("Method RegistryMaterials.get() not found."); 
            
            registryLookupMethod = Reflection.findMethod(
                    MaterialsRegistry.getClass(), 
                    "!get", 
                    getMethod.getGenericParameterTypes()[0], 
                    0,
                    getMethod.getGenericReturnType());
            
            if(registryLookupMethod == null)
                throw new NMSException("Method RegistryMaterials.lookup() not found."); 
            
            return registryLookupMethod;
        }
        catch (Exception ex)
        {
            throw new NMSException("Unable to retreive Minecraft ID", ex); 
        }
    }

    /**
     * Returns the Minecraft internal ID of an ItemStack.
     * <p>
     *     As example, the ID of a {@link Material#STONE Material.STONE} item is {@code minecraft.stone}.
     *     This ID is needed to include items in JSON-formatted messages.
     * </p>
     *
     * @param item An item.
     * @return The Minecraft name of this item, or null if the item's material 
     * is invalid.
     *
     * @throws NMSException if the operation cannot be executed.
     */
    static public String getMinecraftId(ItemStack item) throws NMSException
    {
        try
        {
            Object craftItemStack = asNMSCopy(item);
            if(craftItemStack == null) return null;
            
            Object minecraftItem = Reflection.getFieldValue(craftItemStack, "item");
            Class<?> MinecraftItem = Reflection.getMinecraftClassByName("Item");
            Object ItemsRegistry = Reflection.getFieldValue(MinecraftItem, null, "REGISTRY");
            
            Object minecraftKey = getRegistryLookupMethod().invoke(ItemsRegistry, minecraftItem);
            
            return minecraftKey.toString();
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retrieve Minecraft ID for an ItemStack", ex);
        }
    }

    /**
     * Retrieves the key to use in the {@code translate} property of a JSON message to translate
     * the name of the given ItemStack.
     *
     * @param item An item.
     *
     * @return The I18N key for this item.
     * @throws NMSException if the operation cannot be executed.
     */
    static public String getI18nName(ItemStack item) throws NMSException
    {
        if(item.getItemMeta() instanceof PotionMeta) return getI18nPotionName(item);
        
        try
        {
            Object craftItemStack = asNMSCopy(item);
            Object minecraftItem = Reflection.getFieldValue(craftItemStack, "item");
            return Reflection.call(minecraftItem, getI18nNameMethod(item), craftItemStack) + ".name";
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retrieve Minecraft I18n name", ex);
        }
    }

    /**
     * Copies the ItemStack in a new net.minecraft.server.ItemStack.
     *
     * @param item An item.
     *
     * @return A copy of this item as a net.minecraft.server.ItemStack object.
     * @throws NMSException if the operation cannot be executed.
     */
    static public Object asNMSCopy(ItemStack item) throws NMSException
    {
        try
        {
            Class<?> CraftItemStack = Reflection.getBukkitClassByName("inventory.CraftItemStack");
            return CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retreive NMS copy", ex);
        }
    }

    /**
     * Copies the ItemStack in a new CraftItemStack.
     *
     * @param item An item.
     *
     * @return A copy of this item in a CraftItemStack object.
     * @throws NMSException if the operation cannot be executed.
     */
    static public Object asCraftCopy(ItemStack item) throws NMSException
    {
        try
        {
            Class<?> CraftItemStack = Reflection.getBukkitClassByName("inventory.CraftItemStack");
            return CraftItemStack.getMethod("asCraftCopy", ItemStack.class).invoke(null, item);
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retreive Craft copy", ex);
        }
    }

    /**
     * Returns a NMS ItemStack for the given item.
     *
     * @param item An item.
     *
     * @return A NMS ItemStack for this item. If the item was a CraftItemStack,
     * this will be the item's handle directly; in the other cases, a copy in a
     * NMS ItemStack object.
     * @throws NMSException if the operation cannot be executed.
     */
    static public Object getNMSItemStack(ItemStack item) throws NMSException
    {
        try
        {
            Class<?> CraftItemStack = Reflection.getBukkitClassByName("inventory.CraftItemStack");
            return CraftItemStack.isAssignableFrom(item.getClass())
                    ? Reflection.getFieldValue(CraftItemStack, item, "handle")
                    : CraftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retrieve NMS copy", ex);
        }
    }

    /**
     * Returns a CraftItemStack for the given item.
     *
     * @param item An item.
     *
     * @return A CraftItemStack for this item. If the item was initially a
     * CraftItemStack, it is returned directly. In the other cases, a copy in a
     * new CraftItemStack will be returned.
     * @throws NMSException if the operation cannot be executed.
     */
    static public Object getCraftItemStack(ItemStack item) throws NMSException
    {
        try
        {
            Class<?> CraftItemStack = Reflection.getBukkitClassByName("inventory.CraftItemStack");
            return CraftItemStack.isAssignableFrom(item.getClass()) ? CraftItemStack.cast(item) : asCraftCopy(item);
        }
        catch (Exception ex)
        {
            throw new NMSException("Unable to retrieve CraftItemStack copy", ex);
        }
    }
    
    static private String getI18nPotionName(ItemStack item) throws NMSException
    {
        String potionKey = getI18nPotionKey(item);
        
        try
        {
            Class<?> PotionUtil = Reflection.getMinecraftClassByName("PotionUtil");
            Class<?> PotionRegistry = Reflection.getMinecraftClassByName("PotionRegistry");
            Class<?> ItemStackClass = Reflection.getMinecraftClassByName("ItemStack");
            Object registry = Reflection.findMethod(PotionUtil, null, PotionRegistry, 0, ItemStackClass).invoke(null, asNMSCopy(item));
            
            return (String) Reflection.findMethod(PotionRegistry, null, String.class, 0, String.class).invoke(registry, potionKey);
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retrieve Minecraft I18n name", ex);
        }
    }
    
    static private String getI18nPotionKey(ItemStack item)
    {
        switch(item.getType().name())
        {
            case "SPLASH_POTION":
                return "splash_potion.effect.";
            case "LINGERING_POTION":
                return "lingering_potion.effect.";
            case "POTION":
                break;
            default: 
                return "potion.effect";
        }
        
        Potion potion = Potion.fromItemStack(item);
        
        if(potion.isSplash())
        {
            return "splash_potion.effect.";
        }
        return "potion.effect.";
    }
    
    /**
     * Drops the item at the given location.
     * @param location The location to drop the item at.
     * @param item The item to drop.
     */
    static public void drop(Location location, ItemStack item)
    {
        location.getWorld().dropItem(location, item);
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
        RunTask.nextTick(new DropLaterTask(location, item, true));
    }
    
    /**
     * Drops the item at the given location, at the next server tick.
     * @param location The location to drop the item at.
     * @param item The item to drop.
     */
    static public void dropLater(Location location, ItemStack item)
    {
        RunTask.nextTick(new DropLaterTask(location, item, false));
    }
    
    static private class DropLaterTask implements Runnable
    {
        private final Location location;
        private final ItemStack item;
        private final boolean naturally;
        
        public DropLaterTask(Location location, ItemStack item, boolean naturally)
        {
            this.location = location;
            this.item = item;
            this.naturally = naturally;
        }
        
        @Override
        public void run()
        {
            if(naturally)
            {
                dropNaturally(location, item);
            }
            else
            {
                drop(location, item);
            }
        }
    }
}
