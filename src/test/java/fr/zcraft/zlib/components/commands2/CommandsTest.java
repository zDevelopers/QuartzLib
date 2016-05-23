/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

import org.junit.Test;

public class CommandsTest 
{
    @Test
    public void simpleTest()
    {
        Commands.register()
            .subCommand("belovedblocks", "bb")
                .subCommand("give", "g")
                    .subCommand("tool", new CustomGroup(1), "t")
                        .command(MyCommand.class)
                    .endGroup()
                    .subCommand("block", new CustomGroup(2), "b")
                        .command(MyCommand.class)
                    .end();
    }
}
