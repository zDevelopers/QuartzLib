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

import fr.zcraft.zlib.components.gui.GuiUtils;
import fr.zcraft.zlib.components.nbt.NBT;
import fr.zcraft.zlib.components.nbt.NBTCompound;
import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.components.rawtext.RawTextPart;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.NMSException;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class contains helpers to create or edit very customized ItemStacks
 * using the builder pattern.
 *
 * <p>Example of use:</p>
 * <pre>
 *     ItemStack item = new ItemStackBuilder(Material.TOTEM)
 *                  .title(ChatColor.GOLD + "The best totem of all totems")
 *
 *                  .lore(ChatColor.GRAY + "I'm better than the others.")
 *                  .emptyLore()
 *
 *                  // This one will be automatically wrapped to 28-characters lines so
 *                  // the rendered tooltip is not too wide.
 *                  .longLore(ChatColor.GRAY + "Lorem ipsum dolor sit amnet [&hellip;] elit")
 *
 *                  .glow()
 *                  .hideAttributes()
 *
 *                  // Builds the effective ItemStack
 *                  .item()
 * </pre>
 *
 * <p>Another example, if you want to update an existing ItemStack:</p>
 * <pre>
 *     ItemStack item = ...;
 *
 *     new ItemStackBuilder(item)
 *          .title("A new title")
 *
 *          // Resets the existing lore.
 *          // Without this, lore-related methods
 *          // adds new lines to the existent ones.
 *          .resetLore()
 *
 *          .lore("&hellip;")
 *          .glow()
 *
 *          // This returns the ItemStack, but also updates it by reference
 *          // if the builder was constructed with an ItemStack, so we don't
 *          // really need to retrieve the returned value in such cases.
 *          .item();
 * </pre>
 *
 * <p>You want a {@code CraftItemStack} for some reasons (like NBT handling)? We got you covered:</p>
 * <pre>
 *     ItemStack craftItemStack = new ItemStackBuilder()
 *
 *                  // Any method of the builder, of course.
 *                  .title("A Craft ItemStack")
 *
 *                  // Returns a CraftItemStack object.
 *                  // Please note that, as specified in {@link #craftItem()}'s javadoc,
 *                  // the returned item is here a clone of the original ItemStack in all
 *                  // cases. The original ItemStack will be updated if it exists, by reference,
 *                  // but it will not be transformed into a CraftItemStack magically.
 *                  .craftItem();
 * </pre>
 */
public class ItemStackBuilder
{
    private final ItemStack itemStack;
    private Material material = null;
    private int amount = 1;
    private short data = 0;

    private RawTextPart title = null;
    private final List<String> loreLines = new ArrayList<>();
    private boolean resetLore = false;

    private boolean glowing = false;
    private boolean hideAttributes = false;
    private String[] hiddenAttributes = null;

    private Map<String, Object> nbt = null;
    private boolean replaceNBT = false;

    private final HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    private DyeColor dye = null;

    /**
     * Creates a new ItemStackBuilder.
     */
    public ItemStackBuilder()
    {
        this.material = null;
        this.itemStack = null;
    }

    /**
     * Creates a new ItemStackBuilder, and sets the material of the ItemStack.
     *
     * @param material This ItemStack's material.
     */
    public ItemStackBuilder(Material material)
    {
        this(material, 1);
    }

    /**
     * Creates a new ItemStackBuilder, and sets the material and amount of the
     * ItemStack.
     *
     * @param material This ItemStack's material.
     * @param amount   This ItemStack's amount.
     */
    public ItemStackBuilder(Material material, int amount)
    {
        this.material = material;
        this.amount = amount;
        this.itemStack = null;
    }

    /**
     * Creates a new ItemStackBuilder using an already existing ItemStack.
     *
     * @param itemStack A base ItemStack to modify. This ItemStack will NOT be
     *                  cloned!
     */
    public ItemStackBuilder(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        this.material = null;
        
        if (itemStack != null)
        {
            this.amount = itemStack.getAmount();
            this.data = itemStack.getDurability();

            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null && meta.getLore() != null)
            {
                loreLines.addAll(meta.getLore());
            }
        }
    }

    /**
     * Creates (or updates) the ItemStack, with all the previously provided
     * parameters.
     *
     * @return The new ItemStack. It is NOT a copy of the previously provided
     * ItemStack (if any).
     */
    public ItemStack item()
    {
        ItemStack newItemStack = itemStack;
        
        if (newItemStack == null)
        {
            newItemStack = new ItemStack(material, amount);
        }
        else
        {
            if (material != null)
                newItemStack.setType(material);

            newItemStack.setAmount(amount);
        }
        
        newItemStack.setDurability(data);

        if (dye != null)
        {
            MaterialData materialData = newItemStack.getData();
            if (!(materialData instanceof Colorable))
                throw new IllegalStateException("Unable to apply dye : item is not colorable.");

            ((Colorable) materialData).setColor(dye);
        }

        ItemMeta meta = newItemStack.getItemMeta();

        if (title != null)
            meta.setDisplayName(ChatColor.RESET + title.build().toFormattedText());

        if (!loreLines.isEmpty() || resetLore)
            meta.setLore(loreLines);

        if (hideAttributes)
        {
            if(hiddenAttributes == null)
            {
                ItemUtils.hideItemAttributes(meta);
            }
            else
            {
                ItemUtils.hideItemAttributes(meta, hiddenAttributes);
            }
        }

        newItemStack.setItemMeta(meta);

        if (glowing)
            GlowEffect.addGlow(newItemStack);

        newItemStack.addUnsafeEnchantments(enchantments);

        return newItemStack;
    }
    
    /**
     * Creates (or updates) the ItemStack, with all the previously provided
     * parameters, and returns it as a CraftItemStack (the Bukkit internal 
     * ItemStack representation).
     * 
     * Unlike ItemStacks, CraftItemStacks are registered into NMS, and therefore
     * will be able to hold NBT data (such as attributes and such).
     * 
     * Note that the returned ItemStack WILL be a copy of the previously 
     * provided one (if any).
     * 
     * @return The new CraftItemStack.
     */
    public ItemStack craftItem()
    {
        ItemStack bukkitItem = item();
        try
        {
            ItemStack craftCopy = (ItemStack) ItemUtils.asCraftCopy(bukkitItem);

            if (nbt != null && !nbt.isEmpty())
            {
                craftCopy = NBT.addToItemStack(craftCopy, nbt, replaceNBT);
            }

            return craftCopy;
        }
        catch(NMSException ex)
        {
            PluginLogger.warning("CraftItem failed", ex);
            return bukkitItem;
        }
    }

    /**
     * Defines the material of the ItemStack.
     *
     * @param material The material
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder material(Material material)
    {
        this.material = material;
        return this;
    }

    /**
     * Defines the amount of the ItemStack.
     *
     * @param amount The amount.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder amount(int amount)
    {
        this.amount = amount;
        return this;
    }

    /**
     * Defines the title of the ItemStack. This can only be defined once,
     * otherwise an IllegalStateException will be thrown.
     *
     * @param text The text of the title.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder title(RawTextPart text)
    {
        if (this.title != null)
            throw new IllegalStateException("Title has already been defined.");

        this.title = text;
        return this;
    }

    /**
     * Sets the title of the ItemStack. If a text has already been defined, it
     * will be appended to the already existing one.
     *
     * @param color The color for this piece of text.
     * @param texts The text. If several are provided, they will be all
     *              concatenated.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder title(ChatColor color, String... texts)
    {
        String text = arrayToString(texts);

        if (this.title == null)
            this.title = new RawText(text);
        else
            this.title = this.title.then(text);

        if (color != null)
            this.title.color(color);

        return this;
    }

    /**
     * Sets the title of the ItemStack. If a text has already been defined, it
     * will be appended to the already existing one.
     *
     * @param texts The text. If several strings are provided, they will be all
     *              concatenated.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder title(String... texts)
    {
        return title(null, texts);
    }

    /**
     * Adds one or more lines of lore to the ItemStack.
     *
     * @param lines The lines of lore.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder lore(String... lines)
    {
        loreLines.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * Adds one or more lines of lore to the ItemStack.
     *
     * @param lines The lines of lore.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder lore(List<String> lines)
    {
        loreLines.addAll(lines);
        return this;
    }

    /**
     * Adds one line of lore to the ItemStack.
     *
     * @param text The line of lore. If several are provided, they will be all
     *             concatenated.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder loreLine(String... text)
    {
        return loreLine(null, text);
    }

    /**
     * Adds one line of lore to the ItemStack.
     *
     * @param color The color for this line of lore.
     * @param text  The line of lore. If several are provided, they will be all
     *              concatenated.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder loreLine(ChatColor color, String... text)
    {
        if (color != null)
            loreLines.add(color + arrayToString(text));
        else
            loreLines.add(arrayToString(text));

        return this;
    }

    /**
     * Adds one line of lore to the ItemStack.
     *
     * @param rawText The line of lore.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder loreLine(RawTextPart rawText)
    {
        return loreLine(rawText.toFormattedText());
    }

    /**
     * Adds a line of lore and wraps it to lines of 28 characters, so the
     * tooltip is not too large.
     *
     * @param text The text.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see GuiUtils#generateLore(String)
     */
    public ItemStackBuilder longLore(String text)
    {
        return lore(GuiUtils.generateLore(text));
    }

    /**
     * Adds a line of lore and wraps it to lines of {@code lineLength}
     * characters, so the tooltip is not too large.
     *
     * @param text       The text.
     * @param lineLength The max length of a line.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see GuiUtils#generateLore(String, int)
     */
    public ItemStackBuilder longLore(String text, int lineLength)
    {
        return lore(GuiUtils.generateLore(text, lineLength));
    }

    /**
     * Adds a line of lore and wraps it to lines of 28 characters, so the
     * tooltip is not too large.
     *
     * @param color The color for this line of lore.
     * @param text  The text.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see GuiUtils#generateLore(String)
     */
    public ItemStackBuilder longLore(ChatColor color, String text)
    {
        return longLore(color + text);
    }

    /**
     * Adds a line of lore and wraps it to lines of {@code lineLength}
     * characters, so the tooltip is not too large.
     *
     * @param color      The color for this line of lore.
     * @param text       The text.
     * @param lineLength The max length of a line.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see GuiUtils#generateLore(String, int)
     */
    public ItemStackBuilder longLore(ChatColor color, String text, int lineLength)
    {
        return longLore(color + text, lineLength);
    }

    /**
     * Adds an empty line in the lore to act as a separator.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder loreSeparator()
    {
        loreLines.add("");
        return this;
    }

    /**
     * Clears the previously added lore lines.
     *
     * <p>This is mainly useful for {@link ItemStackBuilder builders} used to update an existing
     * {@link ItemStack}.</p>
     *
     * <ul>
     *     <li>If this method is not called, the original lore will be kept as-is, and calls
     *     to other lore-related methods will append lore lines to the existing lore.</li>
     *     <li>If the method is indeed called, then the original lore will be erased, leaving
     *     a lore-less {@link ItemStack} if no other lore-related method is called, or only
     *     the new lore lines, else.</li>
     * </ul>
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder resetLore()
    {
        loreLines.clear();
        resetLore = true;
        return this;
    }

    /**
     * Adds a glow effect (a fake enchantment) to the ItemStack. This can only
     * be called once, otherwise an IllegalStateException will be thrown.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder glow()
    {
        if (this.glowing)
            throw new IllegalStateException("Glowing has already been set.");

        this.glowing = true;
        return this;
    }

    /**
     * Adds a glow effect (a fake enchantment) to the ItemStack. This can only
     * be called once, otherwise an IllegalStateException will be thrown.
     *
     * @param glow If the glow effect has to be applied. If false, nothing will
     *             be done.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder glow(boolean glow)
    {
        if (glow) glow();
        return this;
    }

    /**
     * Enchants the ItemStack.
     *
     * @param enchantment The enchantment
     * @param level       The enchantment level. The enchant is added with the
     *                    unsafe method, so you can put any number here.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder enchant(Enchantment enchantment, int level)
    {
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Enchants the ItemStack with all the enchantments in the given map.
     *
     * @param enchantments A map enchant → enchant level.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see #enchant(Enchantment, int)
     */
    public ItemStackBuilder enchant(Map<Enchantment, Integer> enchantments)
    {
        for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
        {
            enchant(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Hides all of the attribute lines of the ItemStack. This can only be
     * called once, otherwise an IllegalStateException will be thrown.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder hideAttributes()
    {
        if (this.hideAttributes)
            throw new IllegalStateException("'Hidden attributes' has already been set.");

        this.hideAttributes = true;
        return this;
    }
    
    public ItemStackBuilder hideAttributes(String... flagsNames)
    {
        hideAttributes();
        this.hiddenAttributes = flagsNames;
        
        return this;
    }

    /**
     * Sets the data (= damage) value of the ItemStack.
     *
     * @param data The data value.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder data(short data)
    {
        this.data = data;
        return this;
    }

    /**
     * Sets the dye color of the ItemStack. If the item is not colorable, an
     * IllegalStateException will be thrown when making the item. Setting this
     * value will override damage/data values.
     *
     * @param dye The dye color for the item.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     */
    public ItemStackBuilder dye(DyeColor dye)
    {
        this.dye = dye;
        return this;
    }

    /**
     * Sets the NBT tags to be set in the stack.
     * It is set last and will override some properties like
     * display names.
     *
     * <strong>WARNING</strong>—As NBT data cannot be added to an ItemStack if it's not a CraftItemStack,
     * NBT data will <strong>only be added if you retrieve the item using {@link #craftItem()}</strong>.
     *
     * @param compound A map containing the NBT tags to apply to the item.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see NBTCompound#toHashMap() A method to export the data inside a compound as an independent HashMap.
     * @see #replaceNBT() Option to replace the whole NBT data
     */
    public ItemStackBuilder nbt(Map<String, Object> compound)
    {
        this.nbt = compound;
        return this;
    }

    /**
     * Sets the NBT data to replace the whole data in the item stack. If not called,
     * NBT data will be appended to the existing data, overriding concurrent keys only.
     * If this is called, the whole NBT data will be the data set in {@link #nbt(Map)},
     * clearing any existing data.
     *
     * @return The current ItemStackBuilder instance, for methods chaining.
     * @see #nbt(Map) The method to set NBT data.
     */
    public ItemStackBuilder replaceNBT()
    {
        this.replaceNBT = true;
        return this;
    }

    /**
     * Concatenates a String[] to a single one.
     *
     * @param texts The Strings array.
     *
     * @return The concatenated string.
     */
    private String arrayToString(String... texts)
    {
        StringBuilder builder = new StringBuilder();

        for (String text : texts)
        {
            builder.append(text);
        }

        return builder.toString();
    }
}
