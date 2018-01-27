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

import com.google.common.base.CaseFormat;
import fr.zcraft.zlib.components.nbt.NBT;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.items.ItemUtils;
import fr.zcraft.zlib.tools.reflection.NMSException;
import fr.zcraft.zlib.tools.text.ChatColorParser;
import fr.zcraft.zlib.tools.text.ChatColoredString;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * {@link RawText} provides a tool to construct rich-formatted texts in Minecraft
 * using the builder pattern.
 *
 * <p>It follows the same logic as Minecraft's JSON format. Example of use:</p>
 *
 * <pre>
 *     RawText text = new RawText()
 *                      .then("I ")
 *                      .then("am ")
 *                          .color(ChatColor.RED)
 *                          .hover("This is displayed as a tooltip")
 *                          // .command executes directly while .suggest puts
 *                          // the command into the chat
 *                          .suggest("/say Hi there")
 *                      .then("BOLD")
 *                          .color(ChatColor.DARK_GREEN)
 *                          .style(ChatColor.BOLD)
 *                          .uri("http://perdu.com")
 *                          .hover(
 *                              new RawText()
 *                                  .then("Tooltips")
 *                                      .color(ChatColor.YELLOW)
 *                                  .then("\ncan be rich text too!")
 *                           )
 *                      .build();
 * </pre>
 *
 * <p>See the javadoc below to check out all the possibilities and methods available.</p>
 *
 * <p>A lot of methods in zLib accepts {@link RawText} instances where a text is required,
 * like {@link fr.zcraft.zlib.tools.text.MessageSender}, {@link fr.zcraft.zlib.tools.text.RawMessage},
 * {@link fr.zcraft.zlib.tools.text.Titles}…</p>
 *
 * <p>Please note—due to the way this works, if you want to create progressively a raw text
 * using this, you'll have to store the unfinished variable into a {@link RawTextPart RawTextPart&lt;?&gt;}
 * variable. Example:</p>
 *
 * <pre>
 *     RawTextPart text = new RawText().then("The beginning").color(ChatColor.RED);
 *     text.then("… and the end").color(ChatColor.DARK_GREEN).insert("The end is far");
 *
 *     RawMessage.broadcast(text.build());
 * </pre>
 */
public class RawText extends RawTextPart<RawText>
{
    public RawText()
    {
        super("");
    }

    public RawText(String text)
    {
        super(text);
    }

    /**
     * Converts a Minecraft-formatted string (with formatters like §a) to a raw text component.
     *
     * @param delimiter The formatters delimiter (vanilla Minecraft uses §)
     * @param str The string to convert.
     * @return The RawText equivalent.
     */
    static public RawText fromFormattedString(char delimiter, String str)
    {
        return fromFormattedString(new ChatColorParser(delimiter, str), null);
    }

    /**
     * Converts a Minecraft-formatted string (with formatters like §a) to a raw text component.
     *
     * @param delimiter The formatters delimiter (vanilla Minecraft uses §)
     * @param str The string to convert.
     * @param baseComponent The converted component will be added to this component.
     * @return The RawText equivalent.
     */
    static public RawText fromFormattedString(char delimiter, String str, RawText baseComponent)
    {
        return fromFormattedString(new ChatColorParser(delimiter, str), baseComponent);
    }

    /**
     * Converts a Minecraft-formatted string (with formatters like §a) to a raw text component.
     *
     * @param str The string to convert.
     * @return The RawText equivalent.
     */
    static public RawText fromFormattedString(String str)
    {
        return fromFormattedString(new ChatColorParser(str), null);
    }

    /**
     * Converts a Minecraft-formatted string (with formatters like §a) to a raw text component.
     *
     * @param str The string to convert.
     * @param baseComponent The converted component will be added to this component.
     * @return The RawText equivalent.
     */
    static public RawText fromFormattedString(String str, RawText baseComponent)
    {
        return fromFormattedString(new ChatColorParser(str), baseComponent);
    }
    
    static private RawText fromFormattedString(ChatColorParser parser, RawText baseComponent)
    {
        RawTextPart text = baseComponent;
        
        for (ChatColoredString coloredString : parser)
        {
            if (text == null)
            {
                text = new RawText(coloredString.getString());
            }
            else
            {
                text = text.then(coloredString.getString());
            }
            
            text.style(coloredString.getModifiers());
        }
        
        if (text == null)
            throw new IllegalArgumentException("Invalid input string");
        
        return text.build();
    }

    /**
     * Converts a style name to a tellraw-format compatible name.
     *
     * @param color A color.
     * @return The tellraw-compatible name.
     * @throws IllegalArgumentException if {@link ChatColor#RESET RESET} is passed.
     */
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

    /**
     * Converts an item name to a tellraw-compatible JSON.
     *
     * @param item The item.
     * @return The tellraw-compatible JSON.
     */
    static public String toJSONString(ItemStack item)
    {
        Map<String, Object> itemData = new HashMap<>();
        
        String itemId = null;
        try
        {
            itemId = ItemUtils.getMinecraftId(item);
        }
        catch (NMSException ex)
        {
            PluginLogger.warning("NMS Exception while parsing ItemStack to JSON String", ex);
        }
        
        if (itemId == null) itemId = String.valueOf(item.getType().getId());

        itemData.put("id", itemId);
        itemData.put("Damage", item.getDurability());
        itemData.put("Count", item.getAmount());

        try
        {
            itemData.put("tag", NBT.fromItemStack(item));
        }
        catch (NMSException ex)
        {
            PluginLogger.warning("Unable to retrieve NBT data", ex);
            Map<String, Object> tag = new HashMap<>();
            
            tag.put("display", NBT.fromItemMeta(item.getItemMeta()));
            tag.put("ench", NBT.fromEnchantments(item.getEnchantments()));
            tag.put("HideFlags", NBT.fromItemFlags(item.getItemMeta().getItemFlags()));

            itemData.put("tag", tag);
        }
        
        return NBT.toNBTJSONString(itemData);
    }
    
    @Deprecated
    static public String toJSONString(ItemMeta meta)
    {
        return NBT.toNBTJSONString(NBT.fromItemMeta(meta));
    }

    @Deprecated
    static public String toJSONString(Map<Enchantment, Integer> enchants)
    {
        return NBT.toNBTJSONString(NBT.fromEnchantments(enchants));
    }
    
    @Deprecated
    static public byte toJSON(Set<ItemFlag> itemFlags)
    {
        return NBT.fromItemFlags(itemFlags);
    }

    /**
     * Converts an entity name to a tellraw-compatible JSON.
     *
     * @param entity The entity.
     * @return The tellraw-compatible JSON.
     */
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


    /**
     * @deprecated Future Minecraft versions does not support achievements (they use advancements instead).
     */
    @Deprecated
    static private final EnumMap<Achievement, String> ACHIEVEMENTS_NAMES = new EnumMap<>(Achievement.class);
    
    static {
        ACHIEVEMENTS_NAMES.put(Achievement.BUILD_WORKBENCH, "buildWorkBench");
        ACHIEVEMENTS_NAMES.put(Achievement.GET_DIAMONDS, "diamonds");
        ACHIEVEMENTS_NAMES.put(Achievement.NETHER_PORTAL, "portal");
        ACHIEVEMENTS_NAMES.put(Achievement.GHAST_RETURN, "ghast");
        ACHIEVEMENTS_NAMES.put(Achievement.GET_BLAZE_ROD, "blazeRod");
        ACHIEVEMENTS_NAMES.put(Achievement.BREW_POTION, "potion");
        ACHIEVEMENTS_NAMES.put(Achievement.END_PORTAL, "theEnd");
        ACHIEVEMENTS_NAMES.put(Achievement.THE_END, "theEnd2");
    }

    /**
     * @param achievement An achievement.
     * @return The Minecraft I18N key for this achievement.
     * @deprecated Future Minecraft versions does not support achievements (they use advancements instead).
     */
    @Deprecated
    static public String getI18nKey(Achievement achievement)
    {
        String key = ACHIEVEMENTS_NAMES.get(achievement);
        
        if(key == null)
        {
            key = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, achievement.name());
            ACHIEVEMENTS_NAMES.put(achievement, key);
        }
        
        return key;
    }
}
