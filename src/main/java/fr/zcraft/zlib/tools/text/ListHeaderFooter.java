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

import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.exceptions.IncompatibleMinecraftVersionException;
import fr.zcraft.zlib.tools.reflection.NMSNetwork;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;


/**
 * This utility class sets the player list header and footer for a single player or everyone.
 *
 * @author Amaury Carrade
 */
public final class ListHeaderFooter
{
    private static boolean enabled = true;

    private static Class<?> packetPlayOutPlayerListHeaderFooterClass;
    private static Class<?> chatSerializerClass;
    private static Class<?> iChatBaseComponentClass;

    static
    {
        try
        {
            packetPlayOutPlayerListHeaderFooterClass = Reflection.getMinecraftClassByName("PacketPlayOutPlayerListHeaderFooter");
            iChatBaseComponentClass = Reflection.getMinecraftClassByName("IChatBaseComponent");

            try
            {
                chatSerializerClass = Reflection.getMinecraftClassByName("ChatSerializer");
            }
            catch (ClassNotFoundException e)
            {
                chatSerializerClass = Reflection.getMinecraftClassByName("IChatBaseComponent$ChatSerializer");
            }
        }
        catch (Exception e)
        {
            enabled = false;
            throw new IncompatibleMinecraftVersionException("Unable to load classes needed to send player list header and footer.", e);
        }
    }


    private ListHeaderFooter() {}


    /**
     * Sends the player list headers and footers to the given player.
     *
     * @param player The receiver of the header & footers.
     * @param header The header.
     * @param footer The footer.
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendListHeaderFooter(Player player, String header, String footer)
    {
        sendRawListHeaderFooter(
                player,
                "{\"text\": \"" + (header != null ? header.replace("\"", "\\\"") : "") + "\"}",
                "{\"text\": \"" + (footer != null ? footer.replace("\"", "\\\"") : "") + "\"}"
        );
    }

    /**
     * Sends the player list headers and footers to the given player.
     *
     * @param player The receiver of the header & footers.
     * @param header The header.
     * @param footer The footer.
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendListHeaderFooter(Player player, RawText header, RawText footer)
    {
        sendRawListHeaderFooter(
                player,
                header != null ? header.toJSONString() : "{\"text\": \"\"}",
                footer != null ? footer.toJSONString() : "{\"text\": \"\"}"
        );
    }

    /**
     * Sends the player list headers and footers to the given player.
     *
     * @param player    The receiver of the header & footers.
     * @param rawHeader The header (raw JSON message).
     * @param rawFooter The footer (raw JSON message).
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendRawListHeaderFooter(Player player, String rawHeader, String rawFooter)
    {
        if (!enabled) return;

        try
        {
            Object serializedHeader = iChatBaseComponentClass.cast(Reflection.call(chatSerializerClass, chatSerializerClass, "a", rawHeader));
            Object serializedFooter = iChatBaseComponentClass.cast(Reflection.call(chatSerializerClass, chatSerializerClass, "a", rawFooter));

            Object packet = packetPlayOutPlayerListHeaderFooterClass.getConstructor(iChatBaseComponentClass).newInstance(serializedHeader);
            Reflection.setFieldValue(packet, "b", serializedFooter);

            NMSNetwork.sendPacket(player, packet);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException e)
        {
            throw new IncompatibleMinecraftVersionException("Unable to send player list header and footer.", e);
        }
    }

    /**
     * Sends the player list headers and footers to the whole server.
     *
     * @param header The header.
     * @param footer The footer.
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendListHeaderFooter(String header, String footer)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            sendListHeaderFooter(player, header, footer);
        }
    }

    /**
     * Sends the player list headers and footers to the whole server.
     *
     * @param header The header.
     * @param footer The footer.
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendListHeaderFooter(RawText header, RawText footer)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            sendListHeaderFooter(player, header, footer);
        }
    }

    /**
     * Sends the player list headers and footers to the whole server.
     *
     * @param rawHeader The header (raw JSON message).
     * @param rawFooter The footer (raw JSON message).
     *
     * @throws IncompatibleMinecraftVersionException if an error is encountered while sending the title.
     */
    public static void sendRawListHeaderFooter(String rawHeader, String rawFooter)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            sendRawListHeaderFooter(player, rawHeader, rawFooter);
        }
    }
}
