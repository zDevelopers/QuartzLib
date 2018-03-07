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
package fr.zcraft.zlib.components.gui;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.Gui;
import fr.zcraft.zlib.tools.Callback;

public abstract class ArrayPromptGui<T> extends ExplorerGui<T>
{
	private Callback<T> cb;
	private String title;
	private T[] data;
	private boolean closeOnChoice;

	/**
	 * @param player
	 *            The player making the choice
	 * @param title
	 *            The gui title
	 * @param data
	 *            An array of datas to display
	 * @param closeOnChoice
	 *            If true, close the interface when the player has choosen
	 * @param callback
	 *            Callback called when the player made a choice
	 */
	public <A> ArrayPromptGui(Player player, String title, T[] data, boolean closeOnChoice, Callback<T> callback) {
		this.cb = callback;
		this.title = title;
		this.data = data;
		this.closeOnChoice = closeOnChoice;

		Gui.open(player, this);
	}

	/**
	 * @see #onClick(Object)
	 * 
	 *      Constructor with no callback argument. Note that you must override
	 *      the onClick method if you use this constructor
	 * 
	 * @param player
	 *            The player making the choice
	 * @param title
	 *            The gui title
	 * @param data
	 *            An array of datas to display
	 * @param closeOnChoice
	 *            Close the interface when the player has choosen if true
	 */
	public <A> ArrayPromptGui(Player player, String title, T[] data, boolean closeOnChoice) {
		this(player, title, data, closeOnChoice, null);
	}

	/**
	 * Convert an object to an ItemStack
	 * 
	 * @return The ItemStack to display
	 */
	public abstract ItemStack getViewItem(T data);

	/**
	 * Called when player made a choice if no callback was provided
	 * 
	 * @param data
	 */
	public void onClick(T data) {
		throw new NotImplementedException("Override this method or use a callback.");
	}

	@Override
	protected void onRightClick(T data) {

		if (cb != null)
			cb.call(data);
		else
			onClick(data);

		if (closeOnChoice)
			close();
	}

	@Override
	protected void onUpdate() {
		setTitle(title);
		setMode(Mode.READONLY);
		setData(data);
	}
}
