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

import com.google.common.collect.ImmutableSet;
import fr.zcraft.zlib.components.scoreboard.sender.ObjectiveSender;
import fr.zcraft.zlib.components.scoreboard.sender.SidebarObjective;
import fr.zcraft.zlib.core.ZLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

    // Asynchronously available list of the logged in players.
    private static final Map<UUID, Player> loggedInPlayers = new ConcurrentHashMap<>();


    /* ** This instance's attributes ** */

    private Set<UUID> recipients = new CopyOnWriteArraySet<>();

    private int lastLineScore = 1;

    private SidebarMode contentMode = SidebarMode.GLOBAL;
    private SidebarMode titleMode = SidebarMode.GLOBAL;

    private boolean async = false;
    private long autoRefreshDelay = 0;
    private BukkitTask refreshTask = null;

    // Only used if both titleMode and contentMode are global.
    private SidebarObjective globalObjective = null;

    // Other cases
    private Map<UUID,SidebarObjective> objectives = new ConcurrentHashMap<>();


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
    public abstract List<String> getContent(final Player player);

    /**
     * Returns the title of the scoreboard. Called by the update methods.
     *
     * @param player The receiver of this title. If the title's mode is set to {@link
     *               SidebarMode#GLOBAL}, this will always be {@code null}, and the method will be
     *               called one time per update cycle.
     *
     * @return The title. {@code null} to cancel this update.
     */
    public abstract String getTitle(final Player player);



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
    public void setAsync(final boolean async)
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
    public void setAutoRefreshDelay(final long autoRefreshDelay)
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
    public void setLastLineScore(final int lastLineScore)
    {
        this.lastLineScore = Math.max(lastLineScore, 1);
    }

    /**
     * Sets the scoreboard content's mode of update: either a scoreboard per player or the same
     * scoreboard from everyone.
     *
     * @param contentMode The mode.
     */
    public void setContentMode(final SidebarMode contentMode)
    {
        this.contentMode = contentMode;
    }

    /**
     * Sets the scoreboard title's mode of update: either a title per player or the same scoreboard
     * from everyone.
     *
     * @param titleMode The mode.
     */
    public void setTitleMode(final SidebarMode titleMode)
    {
        this.titleMode = titleMode;
    }

    /**
     * Adds a recipient to this sidebar.
     *
     * @param id The recipient's UUID.
     */
    public void addRecipient(final UUID id)
    {
        recipients.add(id);
    }

    /**
     * Adds a recipient to this sidebar.
     *
     * @param player The recipient.
     */
    public void addRecipient(final Player player)
    {
        addRecipient(player.getUniqueId());
    }

    /**
     * Removes a recipient from this sidebar.
     *
     * @param id The recipient's UUID.
     */
    public void removeRecipient(final UUID id)
    {
        recipients.remove(id);
        objectives.remove(id);

        if(contentMode == SidebarMode.GLOBAL && titleMode == SidebarMode.GLOBAL && globalObjective != null)
            globalObjective.removeReceiver(id);

        ObjectiveSender.clear(id);
    }

    /**
     * Removes a recipient from this sidebar.
     *
     * @param player The recipient.
     */
    public void removeRecipient(final Player player)
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

        if(titleMode == SidebarMode.GLOBAL && contentMode == SidebarMode.GLOBAL)
        {
            // If nothing was done before (objective never created) and we don't have anything to
            // do, we exit now to avoid errors.
            if (title == null && content == null && globalObjective == null)
                return;

            // If the content needs to be refreshed, a new objective is created
            if(content != null || globalObjective == null)
                globalObjective = constructObjective(title, content, recipients);

            // Else, only the title is updated, or nothing
            else if(title != null)
                globalObjective.setDisplayName(title);
        }

        else
        {
            for (UUID id : recipients)
            {
                Player recipient = getPlayerAsync(id);
                if (recipient != null && recipient.isOnline())
                {
                    refresh(recipient, title, content);
                }
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
    public void runAutoRefresh(final boolean run)
    {
        if(refreshTask != null)
        {
            refreshTask.cancel();
            refreshTask = null;
        }

        if(run)
        {
            Runnable refreshRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    refresh();
                }
            };

            if (async)
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
    }



    /* **  Private API  ** */

    /**
     * Updates the scoreboard for the given player.
     *
     * @param player The player.
     */
    private void refresh(final Player player, final String globalTitle, final List<String> globalContent)
    {
        String title = globalTitle;
        List<String> content = globalContent;

        final UUID playerID = player.getUniqueId();
        final boolean objectiveAlreadyExists = objectives.containsKey(playerID);


        if(titleMode == SidebarMode.PER_PLAYER)
        {
            title = getTitle(player);
        }

        if(contentMode == SidebarMode.PER_PLAYER)
        {
            content = getContent(player);
        }


        if (title != null || content != null)
        {
            if (content != null || !objectiveAlreadyExists)
            {
                final SidebarObjective objective = constructObjective(title, content, Collections.singleton(playerID));

                objectives.put(playerID, objective);
                ObjectiveSender.send(objective);
            }
            else
            {
                final SidebarObjective objective = objectives.get(playerID);

                objective.setDisplayName(title);
                ObjectiveSender.updateDisplayName(objective);
            }
        }
    }

    /**
     * Constructs an objective ready to be sent, from the raw data.
     *
     * @param title The sidebar's title.
     * @param content The sidebar's content.
     * @param receivers The receivers of this objective.
     *
     * @return The objective.
     */
    private SidebarObjective constructObjective(final String title, final List<String> content, Set<UUID> receivers)
    {
        SidebarObjective objective = new SidebarObjective(title);

        int score = lastLineScore + content.size() - 1;  // The score of the first line

        for (String line : content)
        {
            objective.setScore(line, score);
            score--;
        }

        for (UUID receiver : receivers)
        {
            objective.addReceiver(receiver);
        }

        return objective;
    }



    /* **  System-wide methods  ** */

    /**
     * Initializes the scoreboards API.
     * Must be called before this library is used.
     */
    public static void init()
    {
        updateLoggedInPlayers();

        Bukkit.getPluginManager().registerEvents(new OnlinePlayersListener(), ZLib.getPlugin());
    }

    /**
     * Returns the current sidebars.
     *
     * @return The sidebars.
     */
    public static Set<Sidebar> getSidebars()
    {
        return ImmutableSet.copyOf(sidebars);
    }

    /**
     * Returns a set containing the currently logged-in players. This method can be used asynchronously.
     *
     * @return The logged-in players.
     */
    public static Set<Player> getOnlinePlayersAsync()
    {
        return ImmutableSet.copyOf(loggedInPlayers.values());
    }

    /**
     * Get a player from an UUID. This method can be used asynchronously.
     *
     * The returned {@link Player} object must be used read-only for thread safety.
     *
     * @param id The player's UUID.
     * @return The Player object; {@code null} if offline.
     */
    public static Player getPlayerAsync(final UUID id)
    {
        return loggedInPlayers.get(id);
    }

    /**
     * Updates the asynchronously-available list of logged-in players.
     *
     * This method needs to be called from the Bukkit's main thread!
     */
    static void updateLoggedInPlayers()
    {
        synchronized (loggedInPlayers)
        {
            loggedInPlayers.clear();

            for (Player player : Bukkit.getOnlinePlayers())
            {
                loggedInPlayers.put(player.getUniqueId(), player);
            }
        }
    }
}
