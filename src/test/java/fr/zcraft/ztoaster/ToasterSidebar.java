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

import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.i18n.I18n;
import fr.zcraft.quartzlib.components.scoreboard.Sidebar;
import fr.zcraft.quartzlib.components.scoreboard.SidebarMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class ToasterSidebar extends Sidebar
{
    private int toastsCount = 0;
    private int insideTheToaster = 0;


    public ToasterSidebar()
    {
        setAsync(true);
        setContentMode(SidebarMode.PER_PLAYER);
        setAutoRefreshDelay(10);
    }


    @Override
    public void preRender()
    {
        Toast[] toasts = Toaster.getToasts();

        toastsCount = toasts.length;

        insideTheToaster = 0;
        for (Toast toast : toasts)
        {
            if (toast.getStatus() == Toast.CookingStatus.NOT_COOKED)
                insideTheToaster++;
        }
    }
    
    @Override
    public List<String> getContent(Player player)
    {
        Locale playerLocale = I18n.getPlayerLocale(player);
        return Arrays.asList(
                I.t(playerLocale, "{darkgreen}{bold}Cook"),
                player.getDisplayName(),
                "",
                I.t(playerLocale, "{yellow}{bold}Inside the toaster"),
                insideTheToaster + "",
                "",
                I.t(playerLocale, "{gold}{bold}Cooked"),
                (toastsCount - insideTheToaster) + ""
        );
    }

    @Override
    public String getTitle(Player player)
    {
        if(insideTheToaster > 0)
            return I.t(I18n.getPlayerLocale(player), "{red}{bold}\u2668 Toaster \u2668");

        else
            return I.t(I18n.getPlayerLocale(player), "{blue}Toaster");
    }
}
