/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

import java.util.Collection;
import org.bukkit.command.CommandSender;

public interface CommandNode<T extends CommandNode>
{
    public T getParent();
    public Collection<CommandNode> getChildren();
    
    public String getName();
    public String[] getAliases();
    public String getShortDescription();
    public String getUsage();
    
    public void run(CommandSender sender, String... arguments) throws CommandException;
    public Collection complete(CommandSender sender, String... arguments) throws CommandException;
    public boolean canExecute(CommandSender sender);
    
}
