/*
 * Copyright or © or Copr. ZLib contributors (2015)
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

package fr.zcraft.ztoaster;

import fr.zcraft.quartzlib.components.gui.ExplorerGui;
import fr.zcraft.quartzlib.components.gui.GuiUtils;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.tools.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ToastExplorer extends ExplorerGui<Toast> {
    @Override
    protected void onUpdate() {
        setTitle(I.t(getPlayerLocale(), "{black}Toaster contents"));

        setData(Toaster.getToasts());

        // DO NOT TOUCH THE TOASTS !
        setMode(ExplorerGui.Mode.READONLY);
    }

    @Override
    protected ItemStack getViewItem(Toast toast) {
        if (toast.getStatus().equals(Toast.CookingStatus.COOKED)) { // Title of the cooked toast item in GUI
            return GuiUtils.makeItem(Material.COOKED_PORKCHOP,
                    I.t(getPlayerLocale(), "{white}Cooked toast #{0}", toast.getToastId()));
        } else { // Title of the raw toast item in GUI
            return new ItemStackBuilder(Material.PORKCHOP)
                    .title(I.tl(getPlayerLocale(), "{white}Raw toast #{0}", toast.getToastId()))
                    .glow(toast.getStatus() == Toast.CookingStatus.IN_OVEN)
                    .item();
        }
    }
}
