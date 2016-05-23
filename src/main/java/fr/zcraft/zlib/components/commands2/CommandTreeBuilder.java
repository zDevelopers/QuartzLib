/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

public class CommandTreeBuilder
{
    public CommandTreeBuilder command(Class<? extends Command> command)
    {
        return this;
    }
    
    public CommandTreeBuilder command(String name, Class<? extends Command> command)
    {
        return this;
    }
    
    public CommandTreeBuilder commands(Class<? extends Command>... command)
    {
        return this;
    }
    
    public CommandTreeBuilder subCommand(String name, String... aliases)
    {
        return this;
    }
    
    public CommandTreeBuilder subCommand(String name, CommandGroup group, String... aliases)
    {
        return this;
    }
    
    public CommandTreeBuilder endGroup()
    {
        return this;
    }
    
    public CommandTreeBuilder end()
    {
        return this;
    }
}
