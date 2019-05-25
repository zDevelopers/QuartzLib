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
package fr.zcraft.zlib.tools.items;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * A fake enchantment to add a glowing effect on any item.
 *
 * @author Amaury Carrade
 */
public class GlowEffect {

	/**
	 * Adds a glowing effect to the given item stack.
	 *
	 * Warning: this effect is a bit unstable Since it uses a normal enchantment and
	 * an ItemFlag to achieve the glow effect always call update(ItemStack toUpdate)
	 * after it was modified!
	 *
	 * @param item The item.
	 */
	public static void addGlow(ItemStack item) {
		if (item == null || item.getItemMeta().hasEnchants())
			return;
		Enchantment fakeGlow = item.getType() != Material.FISHING_ROD ? Enchantment.LURE : Enchantment.ARROW_DAMAGE;
		ItemMeta im = item.getItemMeta();
		im.addEnchant(fakeGlow, 1, true);
		ItemUtils.hideItemAttributes(im, "HIDE_ENCHANTS");
		item.setItemMeta(im);
	}
	
	/**
	 * Updates Items that have the *glow* Effect
	 * 
	 * @param item The item.
	 */
	public static void update(ItemStack item) {
		if (item == null)
			return;
		Enchantment fakeGlow = item.getType() != Material.FISHING_ROD ? Enchantment.LURE : Enchantment.ARROW_DAMAGE;
		ItemMeta im = item.getItemMeta();
		if(im.hasEnchant(fakeGlow)) {
			if(im.getEnchants().size()>1) {
				im.removeEnchant(fakeGlow);
				ItemUtils.removeItemFlags(im, "HIDE_ENCHANTS");
			} else {
				ItemUtils.hideItemAttributes(im, "HIDE_ENCHANTS");
			}
			
		} else {
			ItemUtils.removeItemFlags(im, "HIDE_ENCHANTS");
		}
		item.setItemMeta(im);
	}

	/**
	 * Removes a previously-added glowing effect from the given item.
	 *
	 * @param item The item.
	 */
	public static void removeGlow(ItemStack item) {
		if (item == null)
			return;
		Enchantment fakeGlow = item.getType() != Material.FISHING_ROD ? Enchantment.LURE : Enchantment.ARROW_DAMAGE;
		ItemMeta im = item.getItemMeta();
		im = ItemUtils.removeItemFlags(im, "HIDE_ENCHANTS");
		im.removeEnchant(fakeGlow);
		item.setItemMeta(im);
	}

	/**
	 * Returns if the give item has the glowing effect applied to it.
	 * 
	 * @param item The item.
	 * @return if the give item has the glowing effect applied to it.
	 */
	public static boolean hasGlow(ItemStack item) {
		if (item == null)
			return false;

		if (!item.hasItemMeta() || !item.getItemMeta().hasEnchants())
			return false;
		ItemMeta im = item.getItemMeta();
		Enchantment fakeGlow = item.getType() != Material.FISHING_ROD ? Enchantment.LURE : Enchantment.ARROW_DAMAGE;
		return im.hasEnchant(fakeGlow);

	}

}
