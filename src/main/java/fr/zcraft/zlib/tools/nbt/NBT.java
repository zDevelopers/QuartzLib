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

package fr.zcraft.zlib.tools.nbt;

import fr.zcraft.zlib.tools.items.ItemUtils;
import fr.zcraft.zlib.tools.reflection.NMSException;
import fr.zcraft.zlib.tools.reflection.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class NBT 
{
    private NBT() {}
    
    static public String toNBTJSONString(Object value)
    {
        StringBuilder sb = new StringBuilder();
        toNBTJSONString(sb, value);
        return sb.toString();
    }
    
    /* ========== Item utilities ========== */
    
    static public Map<String, Object> fromItemStack(ItemStack item) throws NMSException
    {
        init();
        return new NBTCompoundWrapper(getMcNBTCompound(item));
    }
    
    static public Map<String, Object> fromItemMeta(ItemMeta meta)
    {
        Map<String, Object> itemData = new HashMap<String, Object>();
        
        if(meta.hasDisplayName())
            itemData.put("Name", meta.getDisplayName());
        
        if(meta.hasLore())
            itemData.put("Lore", meta.getLore());
        
        return itemData;
    }
    
    static public List<Map<String, Object>> fromEnchantments(Map<Enchantment, Integer> enchants)
    {
        List<Map<String, Object>> enchantList = new ArrayList<>();
        
        for (Map.Entry<Enchantment, Integer> enchantment : enchants.entrySet())
        {
            Map<String, Object> enchantmentData = new HashMap<String, Object>();
            enchantmentData.put("id", enchantment.getKey().getId());
            enchantmentData.put("lvl", enchantment.getValue());
            enchantList.add(enchantmentData);
        }
        
        return enchantList;
    }
    
    static public byte fromItemFlags(Set<ItemFlag> itemFlags)
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
    
    /* ========== Internal utilities ========== */
    
    static Class MC_ITEM_STACK = null;
    
    static Class getMinecraftClass(String className) throws NMSException
    {
        try
        {
            return Reflection.getMinecraftClassByName(className);
        }
        catch(ClassNotFoundException ex)
        {
            throw new NMSException("Unable to find class : " + className, ex);
        }
    }
    
    static private void init() throws NMSException
    {
        if(MC_ITEM_STACK != null) return;//Already initialized
        
        MC_ITEM_STACK = getMinecraftClass("ItemStack");
    }
    
    static private Object getMcNBTCompound(ItemStack item) throws NMSException
    {
        Object mcItemStack = ItemUtils.getNMSItemStack(item);
        try
        {
            return Reflection.getFieldValue(MC_ITEM_STACK, mcItemStack, "tag");
        }
        catch (Exception ex)
        {
            throw new NMSException("Unable to retrieve NBT tag from item", ex);
        }
    }
    
    static Object fromNativeValue(Object value)
    {
        NBTType type = NBTType.fromClass(value.getClass());
        return type.newTag(value);
    }
    
    static Object toNativeValue(Object nbtTag)
    {
        NBTType type = NBTType.fromNmsNbtTag(nbtTag);
        
        switch(type)
        {
            case TAG_COMPOUND:
                return new NBTCompoundWrapper(nbtTag);
            case TAG_LIST:
                return new NBTListWrapper(nbtTag);
            default:
                return type.getData(nbtTag);
        }
    }
    
    /* ========== NBT String Utilities ========== */
    
    static private void toNBTJSONString(StringBuilder builder, Object value)
    {
        if(value == null) return;
        
        if(value instanceof List)
            toNBTJSONString(builder, (List) value);
        else if(value instanceof Map)
            toNBTJSONString(builder, (Map) value);
        else if(value instanceof String)
            toNBTJSONString(builder, (String) value);
        else
            builder.append(value.toString());
    }
    
    static private void toNBTJSONString(StringBuilder builder, List list)
    {
        builder.append("[");
        
        boolean isFirst = true;
        for(Object value : list)
        {
            if(!isFirst)
                builder.append(",");
            toNBTJSONString(builder, value);
            isFirst = false;
        }
        
        builder.append("]");
    }
    
    static private void toNBTJSONString(StringBuilder builder, Map<Object, Object> map)
    {
        builder.append("{");
        
        boolean isFirst = true;
        for(Entry<Object, Object> entry : map.entrySet())
        {
            if(!isFirst)
                builder.append(",");
            builder.append(entry.getKey().toString());
            builder.append(':');
            toNBTJSONString(builder, entry.getValue());
            isFirst = false;
        }
        
        builder.append("}");
    }
    
    static private void toNBTJSONString(StringBuilder builder, String value)
    {
        builder.append('"');
        builder.append(value);
        builder.append('"');
    }
}
