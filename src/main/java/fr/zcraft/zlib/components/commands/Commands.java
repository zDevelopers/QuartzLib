package fr.zcraft.zlib.components.commands;

import fr.zcraft.zlib.core.ZLib;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Commands 
{
    static private final ArrayList<CommandGroup> commandGroups =  new ArrayList<>();
    static private String globalPermission;
    
    static public void init(JavaPlugin plugin)
    {
        for(CommandGroup commandGroup : commandGroups)
        {
            commandGroup.register(ZLib.getPlugin());
        }
    }
    
    static public void registerShortcut(String shortcutCommandName, Class<? extends Command> commandClass, String ... names)
    {
        CommandGroup group = getMatchingCommandGroup(shortcutCommandName);
        if(group == null) throw new IllegalArgumentException("Invalid command name : " + shortcutCommandName);
        
        commandGroups.add(new CommandGroup(group, commandClass, names));
    }
    
    static public void register(String[] names, Class<? extends Command> ... commandsClasses)
    {
        commandGroups.add(new CommandGroup(names, commandsClasses));
    }
    
    static public void register(String name, Class<? extends Command> ... commandsClasses)
    {
        commandGroups.add(new CommandGroup(new String[]{name}, commandsClasses));
    }
    
    static public boolean execute(CommandSender sender, String commandName, String[] args)
    {
        CommandGroup commandGroup = getMatchingCommandGroup(commandName);
        if(commandGroup == null) return false;
        commandGroup.executeMatchingCommand(sender, args);
        return true;
    }
    
    static public List<String> tabComplete(CommandSender sender, String commandName, String[] args)
    {
        CommandGroup commandGroup = getMatchingCommandGroup(commandName);
        if(commandGroup == null) return new ArrayList<String>();
        return commandGroup.tabComplete(sender, args);
    }
    
    static private CommandGroup getMatchingCommandGroup(String commandName)
    {
        for(CommandGroup commandGroup : commandGroups)
        {
            if(commandGroup.matches(commandName))
                return commandGroup;
        }
        return null;
    }
    
    
    static public String getGlobalPermission()
    {
        return globalPermission;
    }
    
    static public void setGlobalPermission(String permission)
    {
        globalPermission = permission;
    }
    
}
