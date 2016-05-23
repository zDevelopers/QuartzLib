/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.components.commands2;

public class CustomGroup extends CommandGroup
{
    private final int veryImportantNumber;
    
    public CustomGroup(int veryImportantNumber)
    {
        this.veryImportantNumber = veryImportantNumber;
    }

    public int getVeryImportantNumber()
    {
        return veryImportantNumber;
    }
    
}
