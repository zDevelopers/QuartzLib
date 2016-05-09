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

package fr.zcraft.zlib.components.rawtext;

import fr.zcraft.zlib.tools.text.ChatColorParser;
import fr.zcraft.zlib.tools.text.ChatColoredString;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.junit.Assert;
import org.junit.Test;

public class ChatColorParserTest 
{
    /*static private <T> void assertContainsOnly(Set<T> set, T... comparison)
    {
        assertContainsConly(set, Arrays.asList(comparison));
    }
    
    static private <T> void assertContainsConly(Set<T> set, Collection<T> comparison)
    {
        if(!set.containsAll(comparison)) 
            Assert.fail("Input set does not contain all required elements : " + set + ", required : " + comparison);
        
        set = new HashSet(set);
        set.removeAll(comparison);
        
        if(!set.isEmpty())
            Assert.fail("Input set does not only contain only required elements : " + set + ", required : " + comparison);
    }*/
    
    @Test
    public void helloWorldTest()
    {
        String chatColoredText = ChatColor.RED + "Hello" + ChatColor.GREEN + " world !";
        ChatColorParser parser = new ChatColorParser(chatColoredText);
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("Hello", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));
        
        Assert.assertTrue(parser.hasNext());
        str = parser.next();
        Assert.assertEquals(" world !", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.GREEN));
        
        Assert.assertFalse(parser.hasNext());
    }
    
    @Test
    public void emptyTest()
    {
        ChatColorParser parser = new ChatColorParser("");
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.noneOf(ChatColor.class));
        
        Assert.assertFalse(parser.hasNext());
    }
    
    @Test
    public void delimiterAtTheEndTest()
    {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED.toString());
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));
        
        Assert.assertFalse(parser.hasNext());
    }
    
    @Test
    public void resetTest()
    {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED + "Hello" + ChatColor.RESET + " world !");
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("Hello", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));
        
        Assert.assertTrue(parser.hasNext());
        str = parser.next();
        Assert.assertEquals(" world !", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.noneOf(ChatColor.class));
        
        Assert.assertFalse(parser.hasNext());
    }
    
    @Test
    public void doubleCodeTest()
    {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED + "" + ChatColor.UNDERLINE + "Hello");
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("Hello", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED, ChatColor.UNDERLINE));
        
        Assert.assertFalse(parser.hasNext());
    }
    
    @Test
    public void colorTest()
    {
        String chatColoredText = ChatColor.RED + "" + ChatColor.UNDERLINE + "Hello" + ChatColor.GREEN + " world !";
        ChatColorParser parser = new ChatColorParser(chatColoredText);
        
        Assert.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assert.assertEquals("Hello", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED, ChatColor.UNDERLINE));
        
        Assert.assertTrue(parser.hasNext());
        str = parser.next();
        Assert.assertEquals(" world !", str.getString());
        Assert.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.GREEN, ChatColor.UNDERLINE));
        
        Assert.assertFalse(parser.hasNext());
        
    }
}
