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

import fr.zcraft.zlib.tools.PluginLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@CommandInfo(name = "help", usageParameters = "<command name>")
public class HelpCommand extends Command
{
    
    public HelpCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }
    
    @Override
    protected void run() throws CommandException 
    {
        if(args.length < 1)
            groupHelp();
        else
            commandHelp();
    }
    
    private void groupHelp() throws CommandException 
    {
        sender.sendMessage(commandGroup.getDescription());
        
        String tCommandName;
        String tDescription;
        for(Command tCommand: commandGroup.getCommands())
        {
            if(!tCommand.canExecute(sender)) continue;
            tCommandName = tCommand.getName();
            tDescription = commandGroup.getDescription(tCommandName);
            tCommandName = commandGroup.getUsualName() + " " + tCommandName;
            if(tDescription == null)
                sender.sendMessage("§6/" + tCommandName + "§r");
            else
                sender.sendMessage("§6/" + tCommandName + " : §r" + tDescription);
        }
    }
    
    private void commandHelp() throws CommandException 
    {
        Command command = commandGroup.getMatchingCommand(args[0]);
        if(command == null)
        {
            error("The specified command does not exist.");
            return;
        }
        
        if(!command.canExecute(sender))
            warning("You do not have the permission to use this command.");
        
        String message = "§l§6 ||== ImageOnMap help ==||\n" + 
                "§l§6 |Usage : §r" + command.getUsageString();
        
        try
        {
            String help = getHelpText(command);
            if(help.isEmpty())
            {
                sender.sendMessage(message);
                warning("There is no help message for this command.");
            }
            else
            {
                sender.sendMessage(message + "\n" + help);
            }
        }
        catch(IOException ex)
        {
            sender.sendMessage(message);
            warning("Could not read help for this command.");
            PluginLogger.warning("Could not read help for the command : " + command.getName(), ex);
        }
    }
    
    private String getHelpText(Command command) throws IOException
    {
        String fileName = "help/"+ commandGroup.getUsualName() + 
                    "/" + command.getName() + ".txt";
        
        StringBuilder result = new StringBuilder("");
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
        if(stream == null) return "";
        
	Scanner scanner = new Scanner(stream);
        
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine();
            result.append("§l§9 |§r").append(line).append("\n");
        }
 
        scanner.close();
 
	return result.toString().trim();
    }
    
            
    @Override
    protected List<String> complete() throws CommandException
    {
        if(args.length != 1) return null;
        
        ArrayList<String> matches = new ArrayList<String>();
        
        for(Command command : commandGroup.getCommands())
        {
            if(command.getName().startsWith(args[0])) 
                matches.add(command.getName());
        }
        
        return matches;
    }
  
}
