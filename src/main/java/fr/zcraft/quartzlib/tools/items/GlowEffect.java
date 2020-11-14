/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
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
package fr.zcraft.quartzlib.tools.items;

import fr.zcraft.quartzlib.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

/**
 * A fake enchantment to add a glowing effect on any item.
 *
 * <strong>Only work with 1.12 and before.</strong>
 *
 * @author Amaury Carrade
 */
public class GlowEffect extends EnchantmentWrapper {
	private final static int ENCHANTMENT_ID = 254;
	private final static String ENCHANTMENT_NAME = "GlowEffect";
	private static Enchantment glow;

	protected GlowEffect(String id) {

		super(id);

	}

	/**
	 * Registers, if needed, and returns the fake enchantment to apply on items.
	 *
	 * @return an instance of the fake enchantment.
	 */
	private static Enchantment getGlow() {
		if (glow != null) {
			return glow;
		}

		try {
			// We change this to force Bukkit to accept a new enchantment.
			// Thanks to Cybermaxke on BukkitDev.
			Field acceptingNewField = Enchantment.class.getDeclaredField("acceptingNew");
			acceptingNewField.setAccessible(true);
			acceptingNewField.set(null, true);
		} catch (Exception e) {
			PluginLogger.error("Unable to re-enable enchantments registrations", e);
		}

		try {
			try {
				glow = new GlowEffect("LURE");
				Enchantment.registerEnchantment(glow);
			} catch (NoSuchMethodError e) {
				// 1.13+

			}

		} catch (IllegalArgumentException e) {
			// If the enchantment is already registered - happens on server
			// reload
			glow = Enchantment.getByName("LURE"); // getByID required - by
														// name it doesn't work
														// (returns null).
		}

		return glow;
	}

	/**
	 * Adds a glowing effect to the given item stack.
	 * <p>
	 * Warning: this effect is a bit unstable: it will be thrown away if the
	 * item's meta is updated. So add it at the end.
	 *
	 * @param item The item.
	 */
	public static void addGlow(ItemStack item)
	{
		if (item == null)
			return;

		final Enchantment glow = getGlow();

		if (glow != null)
		{
			item.addEnchantment(glow, 1);
		}
		else
		{
			//from https://github.com/zDevelopers/QuartzLib/pull/21/files#diff-cd248f55f1484c684edc6fa27c585899L167-R44
			if (item.getItemMeta().hasEnchants()) return;

			final Enchantment fakeGlow = item.getType() != Material.FISHING_ROD ? Enchantment.LURE : Enchantment.ARROW_DAMAGE;
			final ItemMeta im = item.getItemMeta();
			im.addEnchant(fakeGlow, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(im);
		}
	}

	/**
	 * Removes a previously-added glowing effect from the given item.
	 *
	 * @param item
	 *            The item.
	 */
	public static void removeGlow(ItemStack item) {
		if (item == null)
			return;

		Enchantment glow = getGlow();
		if (glow != null)
			item.removeEnchantment(glow);
	}

	/**
	 * Returns if the give item has the glowing effect applied to it.
	 * 
	 * @param item
	 *            The item.
	 * @return if the give item has the glowing effect applied to it.
	 */
	public static boolean hasGlow(ItemStack item) {
		if (item == null)
			return false;

		Enchantment glow = getGlow();
		if (glow != null)
			return item.getEnchantmentLevel(glow) > 0;
		return false;
	}

	/* ** Enchantment properties overwritten ** */

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public String getName() {
		return ENCHANTMENT_NAME;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}
}
