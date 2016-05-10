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

package fr.zcraft.zlib.components.rawtext;

import fr.zcraft.zlib.tools.text.ChatColorParser;
import fr.zcraft.zlib.tools.text.ChatColoredString;
import com.google.common.base.CaseFormat;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

public class RawText extends RawTextPart<RawText>
{
    public RawText(String text)
    {
        super(text);
    }
    
    static public RawText fromFormattedString(char delimiter, String str)
    {
        return fromFormattedString(new ChatColorParser(delimiter, str));
    }
    
    static public RawText fromFormattedString(String str)
    {
        return fromFormattedString(new ChatColorParser(str));
    }
    
    static private RawText fromFormattedString(ChatColorParser parser)
    {
        RawTextPart text = null;
        
        for(ChatColoredString coloredString : parser)
        {
            if(text == null)
            {
                text = new RawText(coloredString.getString());
            }
            else
            {
                text = text.then(coloredString.getString());
            }
            
            text.style(coloredString.getModifiers());
        }
        
        if(text == null)
            throw new IllegalArgumentException("Invalid input string");
        
        return text.build();
    }
    
    static public String toStyleName(ChatColor color)
    {
        switch(color)
        {
            case RESET: 
                throw new IllegalArgumentException("Control code 'RESET' is not a valid style");
            case MAGIC:
                return "obfuscated";
            default:
                return color.name().toLowerCase();
        }
    }
    
    static public JSONObject toJSON(ItemStack item)
    {
        JSONObject obj = new JSONObject();
        
        obj.put("id", item.getType().toString());
        obj.put("Damage", item.getData().getData());
        
        JSONObject itemTag = new JSONObject();
        
        JSONObject displayTag = toJSON(item.getItemMeta());
        if(!displayTag.isEmpty())
            itemTag.put("Display", displayTag);
        
        byte itemFlags = toJSON(item.getItemMeta().getItemFlags());
        if(itemFlags > 0)
            itemTag.put("HideFlags", itemFlags);
        
        if(!itemTag.isEmpty())
            obj.put("tag", itemTag);
        
        return obj;
    }
    
    static public JSONObject toJSON(ItemMeta meta)
    {
        JSONObject obj = new JSONObject();
        
        if(meta.hasDisplayName())
            obj.put("Name", meta.getDisplayName());
        if(meta.hasLore())
            obj.put("Lore", meta.getLore());
        
        return obj;
    }
    
    static public byte toJSON(Set<ItemFlag> itemFlags)
    {
        byte flags = 0;
        
        for(ItemFlag flag : itemFlags)
        {
            switch(flag)
            {
                case HIDE_ENCHANTS:
                    flags += 1; break;
                case HIDE_ATTRIBUTES: 
                    flags += 1 << 1; break;
                case HIDE_UNBREAKABLE:
                    flags += 1 << 2; break;
                case HIDE_DESTROYS:
                    flags += 1 << 3; break;
                case HIDE_PLACED_ON:
                    flags += 1 << 4; break;
                case HIDE_POTION_EFFECTS:
                    flags += 1 << 5; break;
            }
        }
        
        return flags;
    }
    
    static public JSONObject toJSON(Entity entity)
    {
        JSONObject obj = new JSONObject();
        
        String name = entity.getCustomName();
        if(name == null || name.isEmpty())
            name = entity.getName();
        
        obj.put("name", name);
        obj.put("type", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, entity.getType().toString()));
        obj.put("id", entity.getUniqueId().toString());
        
        return obj;
    }
}
