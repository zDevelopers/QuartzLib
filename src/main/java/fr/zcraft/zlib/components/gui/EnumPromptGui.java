package fr.zcraft.zlib.components.gui;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.zcraft.zlib.tools.Callback;

/**
 * @author Sam54
 *
 * @param <T>
 *            An enumeration
 */
public class EnumPromptGui<T extends Enum<T>> extends ExplorerGui<T> {

	private Callback<T> cb;
	private String purpose;
	private Class<T> typeParam;
	private Function<? super T, ? extends ItemStack> builder;

	/**
	 * @param cb
	 *            Callback called when the player made a choice
	 * @param player
	 *            The player making the choice
	 * @param purpose
	 *            The purpose (gui's title)
	 * @param typeParam
	 *            Return type
	 * @param builder
	 *            Function for building gui's ItemStack from enum values
	 */
	public EnumPromptGui(Callback<T> cb, Player player, String purpose, Class<T> typeParam,
			Function<? super T, ? extends ItemStack> builder) {
		this.cb = cb;
		this.purpose = purpose;
		this.typeParam = typeParam;
		this.builder = builder;

		Gui.open(player, this);
	}

	@Override
	protected void onRightClick(T data) {
		cb.call(data);
	}

	@Override
	protected void onUpdate() {
		setTitle(purpose);
		setData(typeParam.getEnumConstants());
	}

	@Override
	protected ItemStack getViewItem(T data) {
		return builder.apply(data);
	}
}
