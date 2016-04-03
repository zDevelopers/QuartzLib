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
import fr.zcraft.zlib.components.commands.Command;
import fr.zcraft.zlib.components.commands.Commands;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public abstract class RawTextPart<T extends RawTextPart<T>> implements Iterable<RawTextPart>, JSONAware
{
    static private enum ActionClick
    {
        OPEN_URL,
        RUN_COMMAND,
        SUGGEST
    }
    
    static private enum ActionHover
    {
        SHOW_TEXT,
        SHOW_ACHIEVEMENT,
        SHOW_ITEM,
        SHOW_ENTITY
    }
    
    private final String text;
    private final RawTextPart parent;
    private final ArrayList<RawTextPart> extra = new ArrayList<>();
    
    private ChatColor color;
    //Text styles
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private boolean obfuscated = false;
    private boolean strikethrough = false;
    
    //Other
    private ActionClick actionClick = null;
    private String actionClickValue = null;
    
    private ActionHover actionHover = null;
    private Object actionHoverValue = null;
    
    RawTextPart(String text)
    {
        this(text, null);
    }
    
    RawTextPart(String text, RawTextPart parent)
    {
        this.text = text;
        this.parent = parent;
    }
    
    public RawTextPart then(String text)
    {
        RawTextPart root = getRoot();
        RawTextPart newPart = new RawTextSubPart(text, root);
        
        root.extra.add(newPart);
        return newPart;
    }
    
    public T color(ChatColor color)
    {
        if(this.color != null) throw new IllegalStateException("Color already set.");
        if(color == null || !color.isColor()) 
            throw new IllegalArgumentException("Invalid color.");
        this.color = color;
        
        return (T)this;
    }
    
    public T style(ChatColor style)
    {
        if(style.isColor())
            return this.color(style);
        
        switch(style)
        {
            case BOLD:
                bold = true; break;
            case ITALIC:
                italic = true; break;
            case STRIKETHROUGH:
                strikethrough = true; break;
            case UNDERLINE:
                underline = true; break;
            case MAGIC:
                obfuscated = true; break;
            default:
                throw new IllegalArgumentException("Invalid style : " + style.name());
        }
        
        return (T)this;
    }
    
    public T style(ChatColor... styles)
    {
        for(ChatColor style : styles)
        {
            style(style);
        }
        return (T)this;
    }
    
    private T hover(ActionHover action, Object object)
    {
        if(actionHover != null)
            throw new IllegalStateException("Hover action " + actionHover.name() + " has already been set.");
        actionHover = action;
        actionHoverValue = object;
        return (T)this;
    }
    
    public T hover(RawTextPart hoverText)
    {
        return hover(ActionHover.SHOW_TEXT, hoverText.build());
    }
    
    private String enumCamel(Enum enumValue)
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.toString());
    }
    
    public T hover(Achievement achievement)
    {
        return hover(ActionHover.SHOW_ACHIEVEMENT, "achievement." + enumCamel(achievement));
    }
    
    public T hover(Statistic statistic)
    {
        return hover(ActionHover.SHOW_ACHIEVEMENT, "stat." + enumCamel(statistic));
    }
    
    public T hover(ItemStack item)
    {
        return hover(ActionHover.SHOW_ITEM, RawText.toJSON(item).toJSONString());
    }
    
    public T hover(Entity entity)
    {
        return hover(ActionHover.SHOW_ENTITY, RawText.toJSON(entity).toJSONString());
    }
    
    private T click(ActionClick action, String value)
    {
        if(actionClick != null)
            throw new IllegalStateException("Hover action " + actionClick.name() + " has already been set.");
        actionClick = action;
        actionClickValue = value;
        return (T)this;
    }
    
    public T command(String command)
    {
        return click(ActionClick.RUN_COMMAND, command);
    }
    
    public T command(Class<? extends Command> command, String... args)
    {
        Command commandInfo = Commands.getCommandInfo(command);
        if(commandInfo == null)
            throw new IllegalArgumentException("Unknown command");
        return command(commandInfo.build(args));
    }
    
    public T uri(String uri) throws URISyntaxException
    {
        return uri(new URI(uri));
    }
    
    public T uri(URI uri)
    {
        return click(ActionClick.OPEN_URL, uri.toString());
    }
    
    public T suggest(String suggestion)
    {
        return click(ActionClick.SUGGEST, suggestion);
    }
    
    public RawText build()
    {
        if(parent != null) return parent.build();
        if(this instanceof RawText) return (RawText)this;
        throw new RuntimeException("Dangling non-root text part");
    }
    
    private RawTextPart getRoot()
    {
        if(parent != null) return parent.getRoot();
        return this;
    }
    
    static private JSONObject actionToJSON(Enum action, Object value)
    {
        JSONObject obj = new JSONObject();
        obj.put("action", action.name().toLowerCase());
        obj.put("value", value);
        return obj;
    }
    
    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        obj.put("text", text);
        
        if(!extra.isEmpty())
        {
            JSONArray extraArray = new JSONArray();
            for(RawTextPart childPart: this)
            {
                extraArray.add(childPart.toJSON());
            }
            obj.put("extra", extraArray);
        }
        
        if(color != null)
            obj.put("color", RawText.toStyleName(color));
        
        if(bold) obj.put("bold", true);
        if(italic) obj.put("italic", true);
        if(underline) obj.put("underlined", true);
        if(strikethrough) obj.put("strikethrough", true);
        if(obfuscated) obj.put("obfuscated", true);
        
        if(actionClick != null && actionClickValue != null)
            obj.put("clickEvent", actionToJSON(actionClick, actionClickValue));
        
        if(actionHover != null && actionHoverValue != null)
            obj.put("hoverEvent", actionToJSON(actionHover, actionHoverValue));
        
        return obj;
    }
    
    @Override
    public String toJSONString()
    {
        return toJSON().toJSONString();
    }
    
    public String toPlainText()
    {
        StringBuilder buf = new StringBuilder();
        writePlainText(buf);
        return buf.toString();
    }
    
    private void writePlainText(StringBuilder buf)
    {
        buf.append(text);
        
        for(RawTextPart subPart : this)
        {
            subPart.writePlainText(buf);
        }
    }
    
    public String toFormattedText()
    {
        StringBuilder buf = new StringBuilder();
        writeFormattedText(buf);
        return buf.toString();
    }
    
    private void writeFormattedText(StringBuilder buf)
    {
        if(color != null) buf.append(color);
        if(bold) buf.append(ChatColor.BOLD);
        if(italic) buf.append(ChatColor.ITALIC);
        if(underline) buf.append(ChatColor.UNDERLINE);
        if(strikethrough) buf.append(ChatColor.STRIKETHROUGH);
        if(obfuscated) buf.append(ChatColor.MAGIC);
        
        buf.append(text);
        buf.append(ChatColor.RESET);
        
        for(RawTextPart subPart : this)
        {
            subPart.writeFormattedText(buf);
        }
        
    }

    @Override
    public Iterator<RawTextPart> iterator()
    {
        return extra.iterator();
    }
}
