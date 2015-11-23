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
package fr.zcraft.zlib.components.scoreboard.sender;


import fr.zcraft.zlib.components.scoreboard.Sidebar;
import fr.zcraft.zlib.exceptions.IncompatibleMinecraftVersionException;
import fr.zcraft.zlib.tools.ReflectionUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class uses packets to send the {@link SidebarObjective SidebarObjectives} to their
 * receivers.
 *
 *
 * <h3>WARNING</h3>
 *
 * This class is intended for use with the sidebar objectives only. Notably, it will force the
 * display position and unregister the objectives previously sent through it. This API may evolve to
 * a generic way to send scoreboard packets, but it's not currently the case.
 *
 * @author Amaury Carrade
 */
public class ObjectiveSender
{
    private final static Map<UUID, Object> playersConnections = new ConcurrentHashMap<>();
    private final static Map<UUID, String> sentObjectives = new HashMap<>();


    // The action field of the scoreboard objective packet.
    private final static int PACKET_SCOREBOARD_OBJECTIVE_ACTION_CREATE = 0;
    private final static int PACKET_SCOREBOARD_OBJECTIVE_ACTION_DELETE = 1;
    private final static int PACKET_SCOREBOARD_OBJECTIVE_ACTION_UPDATE = 2;

    // The location field of the objective display packet.
    // For the curious ones:  0 = list ; 1 = sidebar ; 2 = below name.
    private final static int PACKET_DISPLAY_OBJECTIVE_SIDEBAR_LOCATION = 1;


    // The NMS classes & enum values needed to send the packets.
    private final static Class<?> packetPlayOutScoreboardObjectiveClass;
    private final static Class<?> packetPlayOutScoreboardDisplayObjectiveClass;
    private final static Class<?> packetPlayOutScoreboardScoreClass;

    private static Object enumScoreboardHealthDisplay_INTEGER = null;
    private static Object enumScoreboardAction_CHANGE = null;
    private static Object enumScoreboardAction_REMOVE = null;


    static
    {
        try
        {
            packetPlayOutScoreboardObjectiveClass = ReflectionUtils.getMinecraftClassByName("PacketPlayOutScoreboardObjective");
            packetPlayOutScoreboardDisplayObjectiveClass = ReflectionUtils.getMinecraftClassByName("PacketPlayOutScoreboardDisplayObjective");
            packetPlayOutScoreboardScoreClass = ReflectionUtils.getMinecraftClassByName("PacketPlayOutScoreboardScore");


            // IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER value

            Class<?> enumScoreboardHealthDisplay;
            try
            {
                enumScoreboardHealthDisplay = ReflectionUtils.getMinecraftClassByName("IScoreboardCriteria$EnumScoreboardHealthDisplay");
            }
            catch (ClassNotFoundException e)
            {
                enumScoreboardHealthDisplay = ReflectionUtils.getMinecraftClassByName("EnumScoreboardHealthDisplay");
            }

            for (Object enumConstant : enumScoreboardHealthDisplay.getEnumConstants())
            {
                if (Enum.class.cast(enumConstant).name().equals("INTEGER"))
                {
                    enumScoreboardHealthDisplay_INTEGER = enumConstant;
                    break;
                }
            }

            if (enumScoreboardHealthDisplay_INTEGER == null)
                throw new ClassNotFoundException("Unable to retrieve the INTEGER value of the IScoreboardCriteria$EnumScoreboardHealthDisplay enum");


            // PacketPlayOutScoreboardScore.EnumScoreboardAction values

            Class<?> enumScoreboardAction;
            try
            {
                enumScoreboardAction = ReflectionUtils.getMinecraftClassByName("PacketPlayOutScoreboardScore$EnumScoreboardAction");
            }
            catch (ClassNotFoundException e)
            {
                enumScoreboardAction = ReflectionUtils.getMinecraftClassByName("EnumScoreboardAction");
            }

            for (Object enumConstant : enumScoreboardAction.getEnumConstants())
            {
                switch (Enum.class.cast(enumConstant).name())
                {
                    case "CHANGE":
                        enumScoreboardAction_CHANGE = enumConstant;
                        break;

                    case "REMOVE":
                        enumScoreboardAction_REMOVE = enumConstant;
                        break;
                }
            }

            if (enumScoreboardAction_CHANGE == null)
                throw new ClassNotFoundException("Unable to retrieve the CHANGE value of the PacketPlayOutScoreboardScore$EnumScoreboardAction enum");
            if (enumScoreboardAction_REMOVE == null)
                throw new ClassNotFoundException("Unable to retrieve the REMOVE value of the PacketPlayOutScoreboardScore$EnumScoreboardAction enum");
        }
        catch (ClassNotFoundException e)
        {
            throw new IncompatibleMinecraftVersionException("Unable to get the required classes to send scoreboard packets", e);
        }
    }



    /* **  Public API  ** */

    /**
     * Sends the given objective to its receiver(s).
     *
     * This method will:
     * <ul>
     *     <li>send the “create objective” packet if needed;</li>
     *     <li>send the “display scoreboard” packet, to show the sidebar at the right place;</li>
     *     <li>send the “update score” packet for each score in the objective;</li>
     *     <li>if previously present, send a “remove objective” packet to remove the previously-sent objective.</li>
     * </ul>
     *
     * @param objective The objective to be displayed.
     */
    public static void send(SidebarObjective objective)
    {
        Validate.notNull(objective, "The objective cannot be null");

        for (UUID receiver : objective.getReceivers())
        {
            try
            {
                final String oldObjective = sentObjectives.get(receiver);
                final Object connection = getPlayerConnection(receiver);

                createObjective(connection, objective);
                sendScores(connection, objective);

                // The objective is displayed when the scores are sent, so all the lines are displayed
                // instantaneously, even with bad connections.
                setObjectiveDisplay(connection, objective);


                sentObjectives.put(receiver, objective.getName());

                if (oldObjective != null)
                {
                    destroyObjective(connection, oldObjective);
                }
            }
            catch (RuntimeException ignored) {} // Caught, so the packets are not sent for this player only.
        }
    }



    /* **  Objective senders private API  ** */

    private static void createObjective(Object connection, SidebarObjective objective)
    {
        sendScoreboardObjectivePacket(connection, objective.getName(), objective.getDisplayName(), PACKET_SCOREBOARD_OBJECTIVE_ACTION_CREATE);
    }

    private static void updateObjectiveDisplayName(Object connection, SidebarObjective objective)
    {
        sendScoreboardObjectivePacket(connection, objective.getName(), objective.getDisplayName(), PACKET_SCOREBOARD_OBJECTIVE_ACTION_UPDATE);
    }

    private static void setObjectiveDisplay(Object connection, SidebarObjective objective)
    {
        sendScoreboardDisplayObjectivePacket(connection, objective.getName(), PACKET_DISPLAY_OBJECTIVE_SIDEBAR_LOCATION);
    }

    private static void sendScores(Object connection, SidebarObjective objective)
    {
        for (Map.Entry<String, Integer> score : objective.getScores().entrySet())
        {
            sendScore(connection, objective, score.getKey(), score.getValue());
        }
    }

    private static void sendScore(Object connection, SidebarObjective objective, String score, Integer value)
    {
        sendScoreboardScorePacket(connection, objective.getName(), score, value, enumScoreboardAction_CHANGE);
    }

    private static void deleteScore(Object connection, SidebarObjective objective, String score)
    {
        sendScoreboardScorePacket(connection, objective.getName(), score, 0, enumScoreboardAction_REMOVE);
    }

    private static void destroyObjective(Object connection, String objectiveName)
    {
        sendScoreboardObjectivePacket(connection, objectiveName, "", PACKET_SCOREBOARD_OBJECTIVE_ACTION_DELETE);
    }



    /* **  Low level packet senders  ** */

    private static void sendScoreboardObjectivePacket(Object connection, String objectiveName, String objectiveDisplayName, int action)
    {
        try
        {
            Object packet = ReflectionUtils.instantiate(packetPlayOutScoreboardObjectiveClass);

            ReflectionUtils.setFieldValue(packet, "a", objectiveName);                       // Objective name
            ReflectionUtils.setFieldValue(packet, "b", objectiveDisplayName);                // Display name
            ReflectionUtils.setFieldValue(packet, "c", enumScoreboardHealthDisplay_INTEGER); // Display mode (integer or hearts)
            ReflectionUtils.setFieldValue(packet, "d", action);                              // Action (0 = create; 1 = delete; 2 = update)

            ReflectionUtils.sendPacket(connection, packet);
        }
        catch (NoSuchMethodException | IllegalAccessException | InstantiationException | NoSuchFieldException e)
        {
            throw new IncompatibleMinecraftVersionException("Cannot send PacketPlayOutScoreboardObjective", e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("An exception was caught while sending a PacketPlayOutScoreboardObjective", e.getCause());
        }
    }

    private static void sendScoreboardDisplayObjectivePacket(Object connection, String objectiveName, int location)
    {
        try
        {
            Object packet = ReflectionUtils.instantiate(packetPlayOutScoreboardDisplayObjectiveClass);

            ReflectionUtils.setFieldValue(packet, "a", location);      // Objective location (0 = list ; 1 = sidebar ; 2 = below name)
            ReflectionUtils.setFieldValue(packet, "b", objectiveName); // Objective name

            ReflectionUtils.sendPacket(connection, packet);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | NoSuchFieldException e)
        {
            throw new IncompatibleMinecraftVersionException("Cannot send PacketPlayOutScoreboardDisplayObjective", e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("An exception was caught while sending a PacketPlayOutScoreboardDisplayObjective", e.getCause());
        }
    }

    private static void sendScoreboardScorePacket(Object connection, String objectiveName, String scoreName, int scoreValue, Object action)
    {
        try
        {
            Object packet = ReflectionUtils.instantiate(packetPlayOutScoreboardScoreClass);

            ReflectionUtils.setFieldValue(packet, "a", scoreName);     // Score name
            ReflectionUtils.setFieldValue(packet, "b", objectiveName); // Objective name this score belongs to
            ReflectionUtils.setFieldValue(packet, "c", scoreValue);    // Score value
            ReflectionUtils.setFieldValue(packet, "d", action);        // Action (enum member - CHANGE or REMOVE)

            ReflectionUtils.sendPacket(connection, packet);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | NoSuchFieldException e)
        {
            throw new IncompatibleMinecraftVersionException("Cannot send PacketPlayOutScoreboardScore", e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("An exception was caught while sending a PacketPlayOutScoreboardScore", e.getCause());
        }
    }



    /* **  Players connections caching  ** */

    /**
     * Retrieves and returns a player's network connection. This method caches the connection.
     *
     * @param id The player's UUID.
     *
     * @return The connection, or {@code null} if the player is not logged in.
     * @throws RuntimeException if the connection cannot be retrieved for some reason.
     */
    private static Object getPlayerConnection(UUID id)
    {
        if (playersConnections.containsKey(id))
            return playersConnections.get(id);

        try
        {
            final Player player = Sidebar.getPlayerAsync(id);

            if (player == null)
                return null;

            Object connection = ReflectionUtils.getPlayerConnection(player);
            if (connection != null)
            {
                playersConnections.put(id, connection);
                return connection;
            }
            else
            {
                throw new RuntimeException("Unable to retrieve a player's connection (UUID: " + id + ")");
            }
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("Unable to retrieve a player's connection (UUID: " + id + ")", e);
        }
    }

    /**
     * Invalidates the stored connection of a player.
     *
     * @param id The UUID of the invalidated connection.
     */
    public static void invalidateConnection(UUID id)
    {
        synchronized (playersConnections)
        {
            playersConnections.remove(id);
        }
    }
}
