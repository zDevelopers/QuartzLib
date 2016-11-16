package fr.zcraft.zlib.components.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.Gui;
import fr.zcraft.zlib.tools.Callback;

/* Built from recommendations in PR https://github.com/zDevelopers/zLib/pull/14 */
public class ArrayPromptGui<T> extends ExplorerGui<T> {

	private Callback<T> cb;
	private String purpose;
	private T[] datas;
	private ItemRenderer<T> renderer;
	private boolean closeOnChoice;

	/**
	 * @param callback
	 *            Callback called when the player made a choice
	 * @param player
	 *            The player making the choice
	 * @param purpose
	 *            The purpose (gui's title)
	 * @param datas
	 *            An array of datas to display
	 * @param renderer
	 *            Interface for building gui's ItemStack from array values
	 * @param closeOnChoice
	 *            Close the interface when the player has choosen if true
	 */
	public <A> ArrayPromptGui(Callback<T> callback, Player player, String purpose, T[] datas, ItemRenderer<T> renderer,
			boolean closeOnChoice) {
		this.cb = callback;
		this.purpose = purpose;
		this.datas = datas;
		this.renderer = renderer;
		this.closeOnChoice = closeOnChoice;

		Gui.open(player, this);
	}

	@Override
	protected void onRightClick(T data) {
		cb.call(data);
		if (closeOnChoice)
			close();
	}

	@Override
	protected void onUpdate() {
		setTitle(purpose);
		setMode(Mode.READONLY);
		setData(datas);
	}

	@Override
	protected ItemStack getViewItem(T data) {
		return renderer.build(data);
	}

	public Callback<T> getCallback() {
		return cb;
	}

	public String getPurpose() {
		return purpose;
	}

	public T[] getDatas() {
		return datas;
	}

	public boolean closeOnChoice() {
		return closeOnChoice;
	}

	public ItemRenderer<T> getRenderer() {
		return renderer;
	}

	public static interface ItemRenderer<T> {
		/**
		 * Parse an array element to an ItemStack
		 * 
		 * @param The
		 *            object to parse
		 * @return The ItemStack to display
		 */
		public ItemStack build(T data);
	}
}
