/*
 * Copyright or © or Copr. ZLib contributors (2015)
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

package fr.zcraft.zlib.components.commands;

import fr.zcraft.zlib.components.commands.CommandException.Reason;
import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.tools.text.RawMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract public class Command 
{   
    protected CommandGroup commandGroup;
    protected String commandName;
    protected String usageParameters;
    protected String commandDescription;
    protected String[] aliases;
    
    protected CommandSender sender;
    protected String[] args;
    
    abstract protected void run() throws CommandException;
    
    void init(CommandGroup commandGroup)
    {
        this.commandGroup = commandGroup;
        
        CommandInfo commandInfo = this.getClass().getAnnotation(CommandInfo.class);
        if(commandInfo == null) 
            throw new IllegalArgumentException("Command has no CommandInfo annotation");
        
        commandName = commandInfo.name().toLowerCase();
        usageParameters = commandInfo.usageParameters();
        commandDescription = commandGroup.getDescription(commandName);
        aliases = commandInfo.aliases();
    }
    
    public boolean canExecute(CommandSender sender)
    {
        String permissionPrefix = ZLib.getPlugin().getName().toLowerCase() + ".";
        String globalPermission = Commands.getGlobalPermission();
        
        if(globalPermission != null)
            if(sender.hasPermission(permissionPrefix + globalPermission))
                return true;
        
        return sender.hasPermission(permissionPrefix + commandGroup.getUsualName());
    }
    
    protected List<String> complete() throws CommandException
    {
        return null;
    }
    
    public void execute(CommandSender sender, String[] args)
    {
        this.sender = sender; this.args = args;
        try
        {
            if(!canExecute(sender))
                throw new CommandException(this, Reason.SENDER_NOT_AUTHORIZED);
            run();
        }
        catch(CommandException ex)
        {
            warning(ex.getReasonString());
        }
        this.sender = null; this.args = null;
    }
    
    public List<String> tabComplete(CommandSender sender, String[] args)
    {
        List<String> result = null;
        this.sender = sender; this.args = args;
        try
        {
            if(canExecute(sender))
                result = complete();
        }
        catch(CommandException ex){}
        
        this.sender = null; this.args = null;
        if(result == null) result = new ArrayList<String>();
        return result;
    }
    
    
    public String getUsageString()
    {
        return "/" + commandGroup.getUsualName() + " " + commandName + " " + usageParameters;
    }
    
    public String getName()
    {
        return commandName;
    }
    
    CommandGroup getCommandGroup()
    {
        return commandGroup;
    }
    
    public String[] getAliases()
    {
        return aliases;
    }
    
    public boolean matches(String name)
    {
        if(commandName.equals(name.toLowerCase())) return true;
        
        for(String alias : aliases)
        {
            if(alias.equals(name)) return true;
        }
        
        return false;
    }
    
    
    ///////////// Common methods for commands /////////////
    
    protected void throwInvalidArgument(String reason) throws CommandException
    {
        throw new CommandException(this, Reason.INVALID_PARAMETERS, reason);
    }
        
    protected Player playerSender() throws CommandException
    {
        if(!(sender instanceof Player)) 
            throw new CommandException(this, Reason.COMMANDSENDER_EXPECTED_PLAYER);
        return (Player)sender;
    }
        
    ///////////// Methods for command execution /////////////
    
    static protected void info(CommandSender sender, String message)
    {
        sender.sendMessage("§7" + message);
    }
    
    protected void info(String message)
    {
        info(sender, message);
    }

    static protected void success(CommandSender sender, String message)
    {
        sender.sendMessage("§a" + message);
    }

    protected void success(String message)
    {
        success(sender, message);
    }
    
    static protected void warning(CommandSender sender, String message)
    {
        sender.sendMessage("§c" + message);
    }
    
    protected void warning(String message)
    {
        warning(sender, message);
    }
    
    protected void error(String message) throws CommandException
    {
        throw new CommandException(this, Reason.COMMAND_ERROR, message);
    }
    
    protected void tellRaw(String rawMessage) throws CommandException
    {
        RawMessage.send(playerSender(), rawMessage);
    }
    
    ///////////// Methods for autocompletion /////////////
    
    protected List<String> getMatchingSubset(String prefix, String... list)
    {
        return getMatchingSubset(Arrays.asList(list), prefix);
    }
    
    protected List<String> getMatchingSubset(Iterable<? extends String> list, String prefix)
    {
        List<String> matches = new ArrayList<String>();
        
        for(String item : list)
        {
            if(item.startsWith(prefix)) matches.add(item);
        }
        
        return matches;
    }
    
    protected List<String> getMatchingPlayerNames(String prefix)
    {
        return getMatchingPlayerNames(Bukkit.getOnlinePlayers(), prefix);
    }
    
    protected List<String> getMatchingPlayerNames(Iterable<? extends Player> players, String prefix)
    {
        List<String> matches = new ArrayList<String>();
        
        for(Player player : players)
        {
            if(player.getName().startsWith(prefix)) matches.add(player.getName());
        }
        
        return matches;
    }
    
    ///////////// Methods for parameters /////////////
    
    static private String invalidParameterString(int index, final String expected)
    {
        return "Argument #" + (index + 1) + " invalid : expected " + expected;
    }
    
    static private String invalidParameterString(int index, final Object[] expected)
    {
        String[] expectedStrings = new String[expected.length];
        
        for(int i = expected.length; i --> 0;)
        {
            expectedStrings[i] = expected[i].toString().toLowerCase();
        }
        
        String expectedString =  StringUtils.join(expectedStrings, ',');
        
        return "Argument #" + (index + 1) + " invalid : expected " + expectedString;
    }
    
    protected int getIntegerParameter(int index) throws CommandException
    {
        try
        {
            return Integer.parseInt(args[index]);
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, "integer"));
        }
    }

    protected double getDoubleParameter(int index) throws CommandException
    {
        try
        {
            return Double.parseDouble(args[index]);
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, "integer or decimal value"));
        }
    }
    
    protected float getFloatParameter(int index) throws CommandException
    {
        try
        {
            return Float.parseFloat(args[index]);
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, "integer or decimal value"));
        }
    }

    protected long getLongParameter(int index) throws CommandException
    {
        try
        {
            return Long.parseLong(args[index]);
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, "integer"));
        }
    }

    protected boolean getBooleanParameter(int index) throws CommandException
    {
        switch (args[index].toLowerCase().trim())
        {
            case "yes":
            case "y":
            case "on":
            case "true":
            case "1":
                return true;

            case "no":
            case "n":
            case "off":
            case "false":
            case "0":
                return false;

            default:
                throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, "Boolean (yes/no)"));
        }
    }
    
    protected <T extends Enum> T getEnumParameter(int index, Class<T> enumType) throws CommandException
    {
        Enum[] enumValues = enumType.getEnumConstants();
        String parameter = args[index].toLowerCase();
        
        for(Enum value : enumValues)
        {
            if(value.toString().toLowerCase().equals(parameter))
                return (T) value;
        }
        
        throw new CommandException(this, Reason.INVALID_PARAMETERS, invalidParameterString(index, enumValues));
    }
    
}
