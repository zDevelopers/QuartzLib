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
package fr.zcraft.zlib.tools.text;

import fr.zcraft.zlib.core.ZLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * An utility class to send action bar messages to the players.
 */
public final class ActionBar
{
    private static Map<UUID, String> actionMessages = new ConcurrentHashMap<>();

    private static Runnable actionMessagesUpdater = null;
    private static BukkitTask actionMessagesUpdaterTask = null;


    private ActionBar() {}


    /**
     * Sends a constant message to the given player.
     *
     * This message will remain on the screen until the {@link #removeMessage} method is called, or
     * the server is stopped.
     *
     * @param player  The player.
     * @param message The message to display.
     */
    public static void sendPermanentMessage(Player player, String message)
    {
        actionMessages.put(player.getUniqueId(), message);
        MessageSender.sendActionBarMessage(player, message);

        checkActionMessageUpdaterRunningState();
    }

    /**
     * Sends a constant message to the given player.
     *
     * This message will remain on the screen until the {@link #removeMessage} method is called, or
     * the server is stopped.
     *
     * @param playerUUID The player's UUID.
     * @param message    The message to display.
     */
    public static void sendPermanentMessage(UUID playerUUID, String message)
    {
        actionMessages.put(playerUUID, message);
        MessageSender.sendActionBarMessage(playerUUID, message);

        checkActionMessageUpdaterRunningState();
    }

    /**
     * Removes the action bar message displayed to the given player.
     *
     * @param player  The player.
     * @param instant If {@code true}, the message will be removed instantly. Else, it will dismiss
     *                progressively. Please note that in that case, the message may be displayed a
     *                few more seconds.
     */
    public static void removeMessage(Player player, boolean instant)
    {
        actionMessages.remove(player.getUniqueId());

        if (instant)
        {
            MessageSender.sendActionBarMessage(player, "");
        }

        checkActionMessageUpdaterRunningState();
    }

    /**
     * Removes the action bar message displayed to the given player.
     *
     * @param playerUUID The UUID of the player.
     * @param instant    If {@code true}, the message will be removed instantly. Else, it will
     *                   dismiss progressively. Please note that in that case, the message may be
     *                   displayed a few more seconds.
     */
    public static void removeMessage(UUID playerUUID, boolean instant)
    {
        actionMessages.remove(playerUUID);

        if (instant)
        {
            MessageSender.sendActionBarMessage(playerUUID, "");
        }

        checkActionMessageUpdaterRunningState();
    }

    /**
     * Removes the action bar message displayed to the given player.
     *
     * @param player The player.
     */
    public static void removeMessage(Player player)
    {
        removeMessage(player, false);
    }

    /**
     * Removes the action bar message displayed to the given player.
     *
     * @param playerUUID The UUID of the player.
     */
    public static void removeMessage(UUID playerUUID)
    {
        removeMessage(playerUUID, false);
    }


    /**
     * Initializes the ActionBar API.
     *
     * Initializes the {@link Runnable} that will re-send the permanent action messages to the
     * players.
     */
    public static void initActionMessageUpdaterTask()
    {
        if(actionMessagesUpdater != null) return;

        actionMessagesUpdater = new Runnable()
        {
            @Override
            public void run()
            {
                for (Map.Entry<UUID, String> entry : actionMessages.entrySet())
                {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline())
                    {
                        MessageSender.sendActionBarMessage(player, entry.getValue());
                    }
                }
            }
        };
    }

    /**
     * Checks if the task sending the permanent actions message needs to run and is not running, or
     * is useless and running. Stops or launches the task if needed.
     */
    private static void checkActionMessageUpdaterRunningState()
    {
        initActionMessageUpdaterTask();

        int messagesCount = actionMessages.size();

        if (messagesCount == 0 && actionMessagesUpdaterTask != null)
        {
            actionMessagesUpdaterTask.cancel();
            actionMessagesUpdaterTask = null;
        }
        else if (messagesCount > 0 && actionMessagesUpdaterTask == null)
        {
            actionMessagesUpdaterTask = Bukkit.getScheduler().runTaskTimer(ZLib.getPlugin(), actionMessagesUpdater, 20l, 30l);
        }
    }
}
