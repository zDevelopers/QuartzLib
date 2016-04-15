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
import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.components.rawtext.RawTextPart;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder 
{
    private Material material = null;
    private int amount = 1;
    private short data = 0;
    
    private RawTextPart title = null;
    private final ArrayList<String> loreLines = new ArrayList<>();
    
    private boolean glowing = false;
    private boolean hideAttributes = false;
    
    public ItemStackBuilder()
    {
        this(null);
    }
    
    public ItemStackBuilder(Material material)
    {
        this(material, 1);
    }
    
    public ItemStackBuilder(Material material, int amount)
    {
        this.material = material;
        this.amount = amount;
    }
    
    public ItemStack item()
    {
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.setDurability(data);
        
        ItemMeta meta = itemStack.getItemMeta();
        
        if(title != null)
            meta.setDisplayName(ChatColor.RESET + title.build().toFormattedText());
        
        if(!loreLines.isEmpty())
            meta.setLore(loreLines);
        
        if(hideAttributes)
            GuiUtils.hideItemAttributes(meta);
        
        itemStack.setItemMeta(meta);
        
        if(glowing)
            GlowEffect.addGlow(itemStack);
        
        return itemStack;
    }
    
    
    public ItemStackBuilder material(Material material)
    {
        if(this.material != null)
            throw new IllegalStateException("Material has already been defined.");
        
        this.material = material;
        return this;
    }
    
    public ItemStackBuilder title(RawTextPart text)
    {
        if(this.title != null)
            throw new IllegalStateException("Title has already been defined.");
        
        this.title = text;
        return this;
    }
    
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
    
    public ItemStackBuilder title(String... text)
    {
        return title(null, text);
    }
    
    public ItemStackBuilder lore(String... lines)
    {
        loreLines.addAll(Arrays.asList(lines));
        return this;
    }
    
    public ItemStackBuilder loreLine(String...text)
    {
        return loreLine(null, text);
    }
    
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
    
    public ItemStackBuilder loreLine(RawTextPart rawText)
    {
        return loreLine(rawText.toFormattedText());
    }
    
    public ItemStackBuilder glow()
    {
        if(this.glowing)
            throw new IllegalStateException("Glowing has already been set.");
        
        this.glowing = true;
        return this;
    }
    
    public ItemStackBuilder hideAttributes()
    {
        if(this.hideAttributes)
            throw new IllegalStateException("'Hidden attributes' has already been set.");
        
        this.hideAttributes = true;
        return this;
    }
    
    public ItemStackBuilder data(short data)
    {
        this.data = data;
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
