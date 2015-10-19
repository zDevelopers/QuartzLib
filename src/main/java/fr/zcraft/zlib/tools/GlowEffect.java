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

package fr.zcraft.zlib.tools;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

/**
 * A fake enchantment to add a glowing effect on any item.
 *
 * @author Amaury Carrade
 */
public class GlowEffect extends EnchantmentWrapper
{

    private final static int ENCHANTMENT_ID = 254;
    private final static String ENCHANTMENT_NAME = "GlowEffect";
    private static Enchantment glow;

    public GlowEffect(int id)
    {
        super(id);
    }

    public static Enchantment getGlow()
    {
        if (glow != null)
        {
            return glow;
        }

        try
        {
            // We change this to force Bukkit to accept a new enchantment.
            // Thanks to Cybermaxke on BukkitDev.
            Field acceptingNewField = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNewField.setAccessible(true);
            acceptingNewField.set(null, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            glow = new GlowEffect(ENCHANTMENT_ID);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e)
        {
            // If the enchantment is already registered - happens on server reload
            glow = Enchantment.getById(ENCHANTMENT_ID); // getByID required - by name it doesn't work (returns null).
        }

        return glow;
    }

    public static void addGlow(ItemStack item)
    {
        if (item == null) return;

        Enchantment glow = getGlow();
        if (glow != null) item.addEnchantment(glow, 1);
    }

    @Override
    public boolean canEnchantItem(ItemStack item)
    {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment other)
    {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget()
    {
        return null;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public String getName()
    {
        return ENCHANTMENT_NAME;
    }

    @Override
    public int getStartLevel()
    {
        return 1;
    }
}
