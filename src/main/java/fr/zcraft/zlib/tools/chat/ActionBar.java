package fr.zcraft.zlib.tools.chat;

import fr.zcraft.zlib.ZLib;
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
    public static void init()
    {
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
