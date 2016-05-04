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

import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.components.rawtext.RawTextPart;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains helpers to create or edit very customized ItemStacks using the builder pattern.
 */
public class ItemStackBuilder 
{
    private final ItemStack itemStack;
    private Material material = null;
    private int amount = 1;
    private short data = 0;
    
    private RawTextPart title = null;
    private final List<String> loreLines = new ArrayList<>();
    
    private boolean glowing = false;
    private boolean hideAttributes = false;
    
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
     * @param material 
     */
    public ItemStackBuilder(Material material)
    {
        this(material, 1);
    }
    
    /**
     * Creates a new ItemStackBuilder, and sets the material and amount of the ItemStack.
     * @param material
     * @param amount 
     */
    public ItemStackBuilder(Material material, int amount)
    {
        this.material = material;
        this.amount = amount;
        this.itemStack = null;
    }
    
    /**
     * Creates a new ItemStackBuilder using an already existing ItemStack.
     * @param itemStack 
     */
    public ItemStackBuilder(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        this.material = null;
        this.amount = 0;
    }
    
    /**
     * Creates (or updates) the ItemStack, with all the previously provided parameters.
     * @return The new ItemStack. It is NOT a copy of the previously provided ItemStack (if any).
     */
    public ItemStack item()
    {
        ItemStack newItemStack = itemStack;
        
        if(newItemStack == null)
           newItemStack = new ItemStack(material, amount);
        
        newItemStack.setDurability(data);
        
        if(dye != null)
        {
            MaterialData materialData = newItemStack.getData();
            if(!(materialData instanceof Colorable))
                throw new IllegalStateException("Unable to apply dye : item is not colorable.");
            
            ((Colorable)materialData).setColor(dye);
        }
        
        ItemMeta meta = newItemStack.getItemMeta();
        
        if(title != null)
            meta.setDisplayName(ChatColor.RESET + title.build().toFormattedText());
        
        if(!loreLines.isEmpty())
            meta.setLore(loreLines);
        
        if(hideAttributes)
            ItemUtils.hideItemAttributes(meta);
        
        newItemStack.setItemMeta(meta);
        
        if(glowing)
            GlowEffect.addGlow(newItemStack);
        
        return newItemStack;
    }
    
    /**
     * Defines the material of the ItemStack.
     * This can only be defined once, otherwise an IllegalStateException will be thrown.
     * @param material 
     * @return 
     */
    public ItemStackBuilder material(Material material)
    {
        if(this.material != null)
            throw new IllegalStateException("Material has already been defined.");
        
        this.material = material;
        return this;
    }
    
    /**
     * Defines the amount of the ItemStack.
     * @param amount the amount.
     * @return 
     */
    public ItemStackBuilder amount(int amount)
    {
        this.amount = amount;
        return this;
    }
    
    /**
     * Defines the title of the ItemStack.
     * This can only be defined once, otherwise an IllegalStateException will be thrown.
     * @param text
     * @return 
     */
    public ItemStackBuilder title(RawTextPart text)
    {
        if(this.title != null)
            throw new IllegalStateException("Title has already been defined.");
        
        this.title = text;
        return this;
    }
    
    /**
     * Sets the title of the ItemStack.
     * If a text has already been defined, it will be appended to the already existing one.
     * @param color The color for this piece of text.
     * @param texts The text. If several are provided, they will be all concatenated.
     * @return 
     */
    public ItemStackBuilder title(ChatColor color, String... texts)
    {
        String text = arrayToString(texts);
        
        if(this.title == null)
        {
            this.title = new RawText(text);
        }
        else
        {
            this.title = this.title.then(text);
        }
        
        if(color != null)
            this.title.color(color);
        
        return this;
    }
    
    /**
     * Sets the title of the ItemStack.
     * If a text has already been defined, it will be appended to the already existing one.
     * @param texts The text. If several strings are provided, they will be all concatenated.
     * @return 
     */
    public ItemStackBuilder title(String... texts)
    {
        return title(null, texts);
    }
    
    /**
     * Adds one or more lines of lore to the ItemStack.
     * @param lines The lines of lore.
     * @return 
     */
    public ItemStackBuilder lore(String... lines)
    {
        loreLines.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * Adds one or more lines of lore to the ItemStack.
     * @param lines The lines of lore.
     * @return
     */
    public ItemStackBuilder lore(List<String> lines)
    {
        loreLines.addAll(lines);
        return this;
    }
    
    /**
     * Adds one line of lore to the ItemStack.
     * @param text The line of lore. If several are provided, they will be all concatenated.
     * @return 
     */
    public ItemStackBuilder loreLine(String...text)
    {
        return loreLine(null, text);
    }
    
    /**
     * Adds one line of lore to the ItemStack.
     * @param color The color for this line of lore.
     * @param text The line of lore. If several are provided, they will be all concatenated.
     * @return 
     */
    public ItemStackBuilder loreLine(ChatColor color, String...text)
    {
        if(color != null)
        {
            loreLines.add(color + arrayToString(text));
        }
        else
        {
            loreLines.add(arrayToString(text));
        }
        return this;
    }
    
    /**
     * Adds one line of lore to the ItemStack.
     * @param rawText The line of lore.
     * @return 
     */
    public ItemStackBuilder loreLine(RawTextPart rawText)
    {
        return loreLine(rawText.toFormattedText());
    }
    
    /**
     * Adds a glow effect (a factice enchantment) to the ItemStack.
     * This can only be called once, otherwise an IllegalStateException will be thrown.
     * @return 
     */
    public ItemStackBuilder glow()
    {
        if(this.glowing)
            throw new IllegalStateException("Glowing has already been set.");
        
        this.glowing = true;
        return this;
    }
    
    /**
     * Adds a glow effect (a factice enchantment) to the ItemStack.
     * This can only be called once, otherwise an IllegalStateException will be thrown.
     * @param glow If the glow effect has to be applied. If false, nothing will be done.
     * @return 
     */
    public ItemStackBuilder glow(boolean glow)
    {
        if(glow) glow();
        return this;
    }
    
    /**
     * Hides all of the attribute lines of the ItemStack.
     * This can only be called once, otherwise an IllegalStateException will be thrown.
     * @return 
     */
    public ItemStackBuilder hideAttributes()
    {
        if(this.hideAttributes)
            throw new IllegalStateException("'Hidden attributes' has already been set.");
        
        this.hideAttributes = true;
        return this;
    }
    
    /**
     * Sets the data (= damage) value of the ItemStack.
     * @param data
     * @return 
     */
    public ItemStackBuilder data(short data)
    {
        this.data = data;
        return this;
    }
    
    /**
     * Sets the dye color of the ItemStack.
     * If the item is not colorable, an IllegalStateException will be thrown 
     * when making the item.
     * Setting this value will override damage/data values.
     * @param dye The dye color for the item.
     * @return 
     */
    public ItemStackBuilder dye(DyeColor dye)
    {
        this.dye = dye;
        return this;
    }
    
    private String arrayToString(String... texts)
    {
        StringBuilder builder = new StringBuilder();
        
        for(String text: texts)
        {
            builder.append(text);
        }
        
        return builder.toString();
    }
}
