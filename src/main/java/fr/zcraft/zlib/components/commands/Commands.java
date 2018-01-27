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
package fr.zcraft.zlib.components.commands;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Commands extends ZLibComponent
{
    public static final String CHAT_PREFIX = "\u2503";

    static private final List<CommandGroup> commandGroups =  new ArrayList<>();
    static private String globalPermission;

    static public void registerShortcut(String commandGroupName, Class<? extends Command> commandClass, String ... shortcutNames)
    {
        CommandGroup group = getMatchingCommandGroup(commandGroupName);
        if(group == null) throw new IllegalArgumentException("Invalid command group name: " + commandGroupName);
        CommandGroup newCommandGroup = new CommandGroup(group, commandClass, shortcutNames);
        
        newCommandGroup.register(ZLib.getPlugin());
        commandGroups.add(newCommandGroup);
    }
    
    static public void register(String[] names, Class<? extends Command> ... commandsClasses)
    {
        final CommandGroup commandGroup = new CommandGroup(names, commandsClasses);
        commandGroup.register(ZLib.getPlugin());

        commandGroups.add(commandGroup);
    }
    
    static public void register(String name, Class<? extends Command> ... commandsClasses)
    {
        register(new String[] {name}, commandsClasses);
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
    
    static public Command getCommandInfo(Class<? extends Command> commandClass)
    {
        Command command = null;
        for(CommandGroup commandGroup : commandGroups)
        {
            command = commandGroup.getCommandInfo(commandClass);
            if(command != null) break;
        }
        return command;
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
