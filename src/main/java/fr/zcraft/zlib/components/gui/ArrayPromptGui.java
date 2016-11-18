package fr.zcraft.zlib.components.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.Gui;
import fr.zcraft.zlib.tools.Callback;

public abstract class ArrayPromptGui<T> extends ExplorerGui<T> {

	private Callback<T> cb;
	private String title;
	private T[] data;
	private boolean closeOnChoice;

	/**
	 * @param callback
	 *            Callback called when the player made a choice
	 * @param player
	 *            The player making the choice
	 * @param title
	 *            The gui title
	 * @param data
	 *            An array of datas to display
	 * @param renderer
	 *            Interface for building gui's ItemStack from array values
	 * @param closeOnChoice
	 *            Close the interface when the player has choosen if true
	 */
	public <A> ArrayPromptGui(Callback<T> callback, Player player, String title, T[] data, boolean closeOnChoice) {
		this.cb = callback;
		this.title = title;
		this.data = data;
		this.closeOnChoice = closeOnChoice;

		Gui.open(player, this);
	}

	/**
	 * @see #onChoice(Object)
	 * 
	 *      Constructor with no callback argument. Note that you must override
	 *      the onChoice method by using this constructor
	 * 
	 * @param player
	 *            The player making the choice
	 * @param title
	 *            The gui title
	 * @param data
	 *            An array of datas to display
	 * @param renderer
	 *            Interface for building gui's ItemStack from array values
	 * @param closeOnChoice
	 *            Close the interface when the player has choosen if true
	 */
	public <A> ArrayPromptGui(Player player, String title, T[] data, boolean closeOnChoice) {
		this(null, player, title, data, closeOnChoice);
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
	public void onChoice(T data) {
		System.err.println("Damn ! I'm not properly implemented : override me or use a callback.");
	}

	@Override
	protected void onRightClick(T data) {

		if (cb != null)
			cb.call(data);
		else
			onChoice(data);

		if (closeOnChoice)
			close();
	}

	@Override
	protected void onUpdate() {
		setTitle(title);
		setMode(Mode.READONLY);
		setData(data);
	}

	public Callback<T> getCallback() {
		return cb;
	}

	public String getPurpose() {
		return title;
	}

	public T[] getData() {
		return data;
	}

	public boolean closeOnChoice() {
		return closeOnChoice;
	}
}
