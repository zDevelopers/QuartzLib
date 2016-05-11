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
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.items.ItemUtils;
import fr.zcraft.zlib.tools.reflection.NMSException;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
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
    
    //Mojang does not support strict JSON ...........
    static public String toJSONString(ItemStack item)
    {
        StringBuilder str = new StringBuilder("{");
        
        str.append("id:\"");
        try
        {
            str.append(ItemUtils.getMinecraftId(item));
        }
        catch (NMSException ex)
        {
            PluginLogger.warning("NMS Exception while parsing ItemStack to JSON String", ex);
            str.append(item.getTypeId());
        }
        str.append("\"");
        
        str.append(",Damage:\"");
        str.append(item.getData().getData());
        str.append("\"");
        
        
        String displayTag = toJSONString(item.getItemMeta());
        byte itemFlags = toJSON(item.getItemMeta().getItemFlags());
        
        if(itemFlags > 0 || displayTag != null)
        {
            str.append(",tag:{");
            if(itemFlags > 0)
            {
                str.append("HideFlags:\"");
                str.append(itemFlags);
                str.append("\"");
            }
            
            if(displayTag != null)
            {
                if(itemFlags > 0)
                    str.append(",");
                str.append("display:");
                str.append(displayTag);
            }
            
            str.append("}");
        }
        
        str.append("}");
        
        return str.toString();
    }
    
    static public String toJSONString(ItemMeta meta)
    {
        StringBuilder str = new StringBuilder("{");
        
        if(meta.hasDisplayName())
        {
            str.append("Name:\"");
            str.append(meta.getDisplayName());
            str.append("\"");
        }
        if(meta.hasLore())
        {
            if(meta.hasDisplayName())
                str.append(',');
            str.append("Lore:");
            JSONArray lore = new JSONArray();
            lore.addAll(meta.getLore());
            str.append(lore.toJSONString());
        }
        
        str.append("}");
        
        if(!meta.hasLore() && !meta.hasDisplayName())
            return null;
        
        return str.toString();
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
