/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

public abstract class Commands 
{
    static public CommandTreeBuilder register()
    {
        return new CommandTreeBuilder();
    }
}
