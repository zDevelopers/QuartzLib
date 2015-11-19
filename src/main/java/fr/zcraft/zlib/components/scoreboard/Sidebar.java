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
package fr.zcraft.zlib.components.scoreboard;

import fr.zcraft.zlib.core.ZLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * This is the base class of a sidebar scoreboard. To create one, create a class extending this one,
 * set the properties of the scoreboard, and complete the methods returning the scoreboard's
 * content.
 *
 * As this utility does uses packets, it does not interferes with the Bukkit's scoreboard assigned
 * to the players: you can use both of these utilities at the same time, as long as you don't
 * display a sidebar scoreboard using the Bukkit API.
 *
 * @author Amaury Carrade
 */
public abstract class Sidebar
{
    /* ** Global attributes ** */

    private static Set<Sidebar> sidebars = new CopyOnWriteArraySet<>();


    /* ** This instance's attributes ** */

    private Set<UUID> recipients = new CopyOnWriteArraySet<>();

    private int lastLineScore = 1;

    private SidebarMode contentMode = SidebarMode.GLOBAL;
    private SidebarMode titleMode = SidebarMode.GLOBAL;

    private boolean async = false;
    private long autoRefreshDelay = 0;
    private BukkitTask refreshTask = null;


    public Sidebar()
    {
        sidebars.add(this);
    }



    /* ** Methods to override ** */

    /**
     * Returns the content of the scoreboard. Called by the update methods.
     *
     * The first item of the list will be the top line of the sidebar board, and so on. If this
     * returns {@code null}, the scoreboard will not be updated.
     *
     * @param player The receiver of this content. If the content's mode is set to {@link
     *               SidebarMode#GLOBAL}, this will always be {@code null}, and the method will be
     *               called one time per update cycle.
     *
     * @return The content. {@code null} to cancel this update.
     */
    abstract List<String> getContent(Player player);

    /**
     * Returns the title of the scoreboard. Called by the update methods.
     *
     * @param player The receiver of this title. If the title's mode is set to {@link
     *               SidebarMode#GLOBAL}, this will always be {@code null}, and the method will be
     *               called one time per update cycle.
     *
     * @return The title. {@code null} to cancel this update.
     */
    abstract String getTitle(Player player);



    /* ** Public API ** */

    /**
     * Sets the asynchronous status of this scoreboard.
     *
     * If {@code true}, the scoreboard will be updated asynchronously. Else, the update task will be
     * executed from the Bukkit's main thread.
     *
     * WARNING — The async status is only guaranteed if the auto-refresh is used.
     *
     * @param async {@code true} if async.
     */
    public void setAsync(boolean async)
    {
        this.async = async;
    }

    /**
     * Sets the auto-refresh delay, in ticks. To disable, set to 0.
     *
     * This will not launch or update the updater. Use {@link #runAutoRefresh(boolean)} to
     * (re)launch it.
     *
     * @param autoRefreshDelay The delay, in ticks.
     */
    public void setAutoRefreshDelay(long autoRefreshDelay)
    {
        this.autoRefreshDelay = autoRefreshDelay;
    }

    /**
     * The score of the last line. The scores of the lines will be automatically calculated from
     * this score to the max score needed.
     *
     * If a score less than 1 is given, 1 is used.
     *
     * @param lastLineScore The score of the bottom line.
     */
    public void setLastLineScore(int lastLineScore)
    {
        this.lastLineScore = Math.max(lastLineScore, 1);
    }

    /**
     * Sets the scoreboard content's mode of update: either a scoreboard per player or the same
     * scoreboard from everyone.
     *
     * @param contentMode The mode.
     */
    public void setContentMode(SidebarMode contentMode)
    {
        this.contentMode = contentMode;
    }

    /**
     * Sets the scoreboard title's mode of update: either a title per player or the same scoreboard
     * from everyone.
     *
     * @param titleMode The mode.
     */
    public void setTitleMode(SidebarMode titleMode)
    {
        this.titleMode = titleMode;
    }

    /**
     * Adds a recipient to this sidebar.
     *
     * @param id The recipient's UUID.
     */
    public void addRecipient(UUID id)
    {
        recipients.add(id);
    }

    /**
     * Adds a recipient to this sidebar.
     *
     * @param player The recipient.
     */
    public void addRecipient(Player player)
    {
        addRecipient(player.getUniqueId());
    }

    /**
     * Removes a recipient from this sidebar.
     *
     * @param id The recipient's UUID.
     */
    public void removeRecipient(UUID id)
    {
        recipients.remove(id);
    }

    /**
     * Removes a recipient from this sidebar.
     *
     * @param player The recipient.
     */
    public void removeRecipient(Player player)
    {
        removeRecipient(player.getUniqueId());
    }

    /**
     * Refresh the whole scoreboard.
     *
     * Calls the update methods and updates the scoreboard for each recipient.
     */
    public void refresh()
    {
        String title = null;
        List<String> content = null;

        if(titleMode == SidebarMode.GLOBAL)
            title = getTitle(null);

        if (contentMode == SidebarMode.GLOBAL)
            content = getContent(null);


        for (UUID id : recipients)
        {
            Player recipient = Bukkit.getPlayer(id);
            if (recipient != null && recipient.isOnline())
            {
                refresh(recipient, title, content);
            }
        }
    }

    /**
     * (Re)Launches the auto-refresh task.
     *
     * Warning: if this method is called with {@code true} and the update task is already launched,
     * it will be killed and re-launched.
     *
     * @param run {@code true} to run the task. {@code false} to stop it.
     */
    public void runAutoRefresh(boolean run)
    {
        if(refreshTask != null)
            refreshTask.cancel();

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run()
            {
                refresh();
            }
        };

        if(async)
        {
            refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    ZLib.getPlugin(), refreshRunnable, 1l, autoRefreshDelay
            );
        }
        else
        {
            refreshTask = Bukkit.getScheduler().runTaskTimer(
                    ZLib.getPlugin(), refreshRunnable, 1l, autoRefreshDelay
            );
        }
    }



    /* **  Private API  ** */

    /**
     * Updates the scoreboard for the given player.
     *
     * @param player The player.
     */
    private void refresh(Player player, String globalTitle, List<String> globalContent)
    {

    }



    /* **  System-wide methods  ** */

    public static void init()
    {

    }
}