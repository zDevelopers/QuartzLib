/*
 * Copyright (C) 2013 Moribus
 * Copyright (C) 2015 ProkopyL <prokopylmc@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.zcraft.zlib.components.commands;

import fr.zcraft.zlib.tools.PluginLogger;
import org.apache.commons.lang.*;
import org.bukkit.command.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandGroup implements TabCompleter, CommandExecutor
{
    private final CommandGroup shortcutCommandGroup;
    private final String[] names;
    private final Class<? extends Command>[] commandsClasses;
    private final ArrayList<Command> commands = new ArrayList<>();
    private final HashMap<String, String> commandsDescriptions = new HashMap<>();
    private String description = "";
    
    CommandGroup(CommandGroup shortcutCommandGroup, Class<? extends Command> commandClass, String ... names)
    {
        this.names = names;
        this.commandsClasses = new Class[]{commandClass};
        this.shortcutCommandGroup = shortcutCommandGroup;
        initCommands();
    }
    
    CommandGroup(String[] names, Class<? extends Command> ... commandsClasses)
    {
        this.names = names;
        this.commandsClasses = commandsClasses;
        this.shortcutCommandGroup = null;
        initDescriptions();
        initCommands();
    }
    
    private void initDescriptions()
    {
        String fileName = "help/" + getUsualName() + ".txt";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
        if(stream == null)
        {
            PluginLogger.warning("Could not load description file for the " + getUsualName() + " command");
            return;
        }
        
	Scanner scanner = new Scanner(stream);
        StringBuilder builder = new StringBuilder();
        
        //Getting the group's description
        //And then each command's description
        int colonIndex, firstSpaceIndex;
        boolean isGroupDescription = true;
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine();
            colonIndex = line.indexOf(':');
            if(isGroupDescription)
            {
                firstSpaceIndex = line.indexOf(' ');
                if(colonIndex > 0 && firstSpaceIndex > colonIndex)
                    isGroupDescription = false;
            }
            
            if(isGroupDescription)
            {
                builder.append(line).append('\n');
            }
            else
            {
                commandsDescriptions.put(line.substring(0, colonIndex).trim(), 
                                         line.substring(colonIndex + 1).trim());
            }
        }
        
        scanner.close();
        description = builder.toString().trim();
 
    }
    
    private void initCommands()
    {
        for (Class<? extends Command> commandClass : commandsClasses) 
        {
            addCommand(commandClass);
        }
        
        if(!isShortcutCommand()) addCommand(HelpCommand.class);
    }
    
    private void addCommand(Class<? extends Command> commandClass)
    {
        Constructor<? extends Command> constructor;
        try 
        {
            constructor = commandClass.getConstructor(CommandGroup.class);
            commands.add(constructor.newInstance(isShortcutCommand() ? shortcutCommandGroup : this));
        } 
        catch (Exception ex) 
        {
            PluginLogger.warning("Exception while initializing command", ex);
        }
    }
    
    public boolean executeMatchingCommand(CommandSender sender, String[] args)
    {
        if(isShortcutCommand()) 
        {
            commands.get(0).execute(sender, args);
            return true;
        }
        
        if(args.length <= 0)
        {
            sender.sendMessage(getUsage()); return false;
        }
        
        String commandName = args[0];
        String[] commandArgs = getCommandArgsFromGroupArgs(args);
        
        return executeMatchingCommand(sender, commandName, commandArgs);
    }
    
    private boolean executeMatchingCommand(CommandSender sender, String commandName, String[] args)
    {
        Command command = getMatchingCommand(commandName);
        if(command != null)
        {
            command.execute(sender, args);
        }
        else
        {
            sender.sendMessage(getUsage());
        }
        return command != null;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) 
    {
        return tabComplete(sender, args);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
    {
        return executeMatchingCommand(sender, args);
    }
    
    public List<String> tabComplete(CommandSender sender, String[] args)
    {
        if(isShortcutCommand()) return commands.get(0).tabComplete(sender, args);
        if(args.length <= 1) return tabComplete(sender, args.length == 1 ? args[0] : null);
        String commandName = args[0];
        String[] commandArgs = getCommandArgsFromGroupArgs(args);
        return tabCompleteMatching(sender, commandName, commandArgs);
    }
    
    public List<String> tabComplete(CommandSender sender, String commandName)
    {
        ArrayList<String> matchingCommands = new ArrayList<String>();
        for(Command command : commands)
        {
            if(!command.canExecute(sender)) continue;
            if(commandName == null || command.getName().startsWith(commandName.toLowerCase()))
            {
                matchingCommands.add(command.getName());
            }
        }
        return matchingCommands;
    }
    
    private List<String> tabCompleteMatching(CommandSender sender, String commandName, String[] args)
    {
        Command command = getMatchingCommand(commandName);
        if(command != null)
        {
            return command.tabComplete(sender, args);
        }
        else
        {
            return new ArrayList<String>();
        }
    }
    
    static public String[] getCommandArgsFromGroupArgs(String[] args)
    {
        String[] commandArgs = new String[args.length - 1];
        
        for(int i = 0; i < commandArgs.length; i++)
        {
            commandArgs[i] = args[i + 1];
        }
        
        return commandArgs;
    }
    
    public Command getMatchingCommand(String commandName)
    {
        for(Command command : commands)
        {
            if(command.matches(commandName))
            {
                return command;
            }
        }
        return null;
    }
    
    public boolean matches(String name)
    {
        name = name.toLowerCase();
        for(String commandName : names)
        {
            if(commandName.equals(name)) return true;
        }
        return false;
    }
    
    public String[] getCommandsNames()
    {
        String[] commandsNames = new String[commands.size()];
        
        for(int i = 0; i < commands.size(); i++)
        {
            commandsNames[i] = commands.get(i).getName();
        }
        
        return commandsNames;
    }
    
    public void register(JavaPlugin plugin)
    {
        PluginCommand bukkitCommand = plugin.getCommand(getUsualName());
        bukkitCommand.setAliases(getAliases());
        bukkitCommand.setExecutor(this);
        bukkitCommand.setTabCompleter(this);
    }
    
    protected String getUsage()
    {
        if(isShortcutCommand()) return "§cUsage : " + commands.get(0).getUsageString();
        return "§cUsage : /" + getUsualName() + 
                " <" + StringUtils.join(getCommandsNames(), "|") + ">";
    }
    
    public String getUsualName() { return names[0]; }
    public String[] getNames() { return names.clone(); }
    public List<String> getAliases() { return Arrays.asList(names).subList(1, names.length);}
    public Command[] getCommands() { return commands.toArray(new Command[commands.size()]);}
    public String getDescription() { return description; }
    public String getDescription(String commandName) { return commandsDescriptions.get(commandName); }
    public boolean isShortcutCommand() { return shortcutCommandGroup != null; }
    public CommandGroup getShortcutCommandGroup() { return shortcutCommandGroup; }

}
