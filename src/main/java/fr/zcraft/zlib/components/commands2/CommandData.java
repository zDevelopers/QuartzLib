/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

import java.util.Collection;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandSender;

public class CommandData<T extends CommandNode> implements CommandNode<T>
{

    @Override
    public T getParent()
    {
        throw new NotImplementedException();
    }

    @Override
    public Collection getChildren()
    {
        throw new NotImplementedException();
    }

    @Override
    public String getName()
    {
        throw new NotImplementedException();
    }

    @Override
    public String[] getAliases()
    {
        throw new NotImplementedException();
    }

    @Override
    public String getShortDescription()
    {
        throw new NotImplementedException();
    }

    @Override
    public String getUsage()
    {
        throw new NotImplementedException();
    }

    @Override
    public void run(CommandSender sender, String... arguments) throws CommandException
    {
        throw new NotImplementedException();
    }

    @Override
    public Collection complete(CommandSender sender, String... arguments) throws CommandException
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean canExecute(CommandSender sender)
    {
        throw new NotImplementedException();
    }

}
