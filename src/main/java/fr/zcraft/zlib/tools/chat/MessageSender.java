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
package fr.zcraft.zlib.tools.chat;

import fr.zcraft.zlib.tools.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;


public final class MessageSender
{
    private static boolean enabled = true;

    private static String nmsVersion = ReflectionUtils.getBukkitPackageVersion();

    private static Class<?> packetPlayOutChatClass;
    private static Class<?> chatSerializerClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> chatComponentTextClass;

    static
    {
        try
        {
            iChatBaseComponentClass = ReflectionUtils.getMinecraftClassByName("IChatBaseComponent");
            packetPlayOutChatClass = ReflectionUtils.getMinecraftClassByName("PacketPlayOutChat");

            if (nmsVersion.equalsIgnoreCase("v1_8_R1") || !nmsVersion.startsWith("v1_8_"))
                chatSerializerClass = ReflectionUtils.getMinecraftClassByName("ChatSerializer");
            else
                chatComponentTextClass = ReflectionUtils.getMinecraftClassByName("ChatComponentText");
        }
        catch (Exception e)
        {
            enabled = false;
        }
    }

    private MessageSender() {}

    /**
     * Sends a message.
     *
     * @param receiver The receiver of the message.
     * @param message  The message to be sent.
     * @param type     The message's type.
     *
     * @return {@code false} if an error occurred while sending the message.<br />
     * If this happens:
     * <ul>
     *     <li>
     *         either the message's type was {@link MessageSender.MessageType#CHAT CHAT} or {@link
     *         MessageSender.MessageType#SYSTEM SYSTEM}, and the {@link org.bukkit.command.CommandSender#sendMessage(String)
     *         sendMessage} method was used as a fallback;
     *     </li>
     *     <li>
     *         or the message's type was {@link
     *         MessageSender.MessageType#ACTION_BAR ACTION_BAR}, and the message was not sent.
     *     </li>
     * </ul>
     */
    public static boolean sendMessage(Player receiver, String message, MessageType type)
    {
        if (receiver == null || message == null) return false;

        // Fallback to sendMessage if a problem occurs.
        if (!enabled)
        {
            if (type != MessageType.ACTION_BAR)
                receiver.sendMessage(message);

            return false;
        }


        if (type == null) type = MessageType.SYSTEM;

        try
        {
            Object chatPacket;

            if (nmsVersion.equalsIgnoreCase("v1_8_R1") || !nmsVersion.startsWith("v1_8_"))
            {
                Object baseComponent = iChatBaseComponentClass.cast(ReflectionUtils.call(chatSerializerClass, chatSerializerClass, "a", "{\"text\": \"" + message + "\"}"));
                chatPacket = ReflectionUtils.instanciate(packetPlayOutChatClass, baseComponent, type.getMessagePositionByte());
            }
            else
            {
                Object componentText = ReflectionUtils.instanciate(chatComponentTextClass, message);
                chatPacket = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, byte.class).newInstance(componentText, type.getMessagePositionByte());
            }

            ReflectionUtils.sendPacket(receiver, chatPacket);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if (type != MessageType.ACTION_BAR)
                receiver.sendMessage(message);

            return false;
        }
    }

    /**
     * Sends a message.
     *
     * @param receiver The UUId of the receiver of the message.
     * @param message  The message to be sent.
     * @param type     The message's type.
     *
     * @return {@code false} if no player with the given UUID is currently logged in, or if an error
     * occurred while sending the message.<br />
     * If this happens:
     * <ul>
     *     <li>
     *         either the message's type was {@link MessageSender.MessageType#CHAT CHAT} or {@link
     *         MessageSender.MessageType#SYSTEM SYSTEM}, and the {@link org.bukkit.command.CommandSender#sendMessage(String)
     *         sendMessage} method was used as a fallback;
     *     </li>
     *     <li>
     *         or the message's type was {@link
     *         MessageSender.MessageType#ACTION_BAR ACTION_BAR}, and the message was not sent.
     *     </li>
     * </ul>
     */
    public static boolean sendMessage(UUID receiver, String message, MessageType type)
    {
        Player player = Bukkit.getPlayer(receiver);

        return !(player == null || !player.isOnline()) && sendMessage(player, message, type);
    }


    /**
     * Sends a chat message, like a message in the main channel or a private message.
     *
     * A message of this kind will be hidden if the « Show chat » option is set to « commands only
     * ».
     *
     * @param receiver The receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if an error occurred while sending the message. If this happens, the
     * {@link org.bukkit.command.CommandSender#sendMessage(String) sendMessage} method is
     * automatically called as a fallback.
     */
    public static boolean sendChatMessage(Player receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.CHAT);
    }

    /**
     * Sends a chat message, like a message in the main channel or a private message.
     *
     * A message of this kind will be hidden if the « Show chat » option is set to « commands only
     * ».
     *
     * @param receiver The UUID of the receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if no player with the given UUID is currently logged in, or if an error
     * occurred while sending the message. If this happens, the {@link
     * org.bukkit.command.CommandSender#sendMessage(String) sendMessage} method is automatically
     * called as a fallback.
     */
    public static boolean sendChatMessage(UUID receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.CHAT);
    }

    /**
     * Sends a system message, like the result of a command.
     *
     * It is technically equivalent to call {@link org.bukkit.command.CommandSender#sendMessage(String)
     * receiver.sendMessage(message)}, but this method uses packets directly.
     *
     * @param receiver The receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if an error occurred while sending the message. If this happens, the
     * {@link org.bukkit.command.CommandSender#sendMessage(String) sendMessage} method is
     * automatically called as a fallback.
     */
    public static boolean sendSystemMessage(Player receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.SYSTEM);
    }

    /**
     * Sends a system message, like the result of a command.
     *
     * It is technically equivalent to call {@link org.bukkit.command.CommandSender#sendMessage(String)
     * Bukkit.getPlayer(receiver).sendMessage(message)}, but this method uses packets directly and
     * don't fail with an NPE (returns false) if the player is not logged in.
     *
     * @param receiver The receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if no player with the given UUID is currently logged in, or if an error
     * occurred while sending the message. If this happens, the {@link
     * org.bukkit.command.CommandSender#sendMessage(String) sendMessage} method is automatically
     * called as a fallback.
     */
    public static boolean sendSystemMessage(UUID receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.SYSTEM);
    }

    /**
     * Sends a temporary message displayed above the hotbar during approximately three seconds.
     *
     * @param receiver The receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if an error occurred while sending the message. If this happens, the
     * message is not sent at all. Such an error is likely to be caused by an incompatible Bukkit
     * version.
     *
     * @see ActionBar
     */
    public static boolean sendActionBarMessage(Player receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.ACTION_BAR);
    }

    /**
     * Sends a temporary message displayed above the hotbar during approximately three seconds.
     *
     * @param receiver The receiver of the message.
     * @param message  The message.
     *
     * @return {@code false} if no player with the given UUID is currently logged in, or if an error
     * occurred while sending the message. If this happens, the message is not sent at all. Such an
     * error is likely to be caused by an incompatible Bukkit version.
     *
     * @see ActionBar
     */
    public static boolean sendActionBarMessage(UUID receiver, String message)
    {
        return sendMessage(receiver, message, MessageType.ACTION_BAR);
    }


    public enum MessageType
    {
        /**
         * A chat message, like a message in the main channel or a private message.
         *
         * A message of this kind will be hidden if the « Show chat » option is set to « commands
         * only ».
         */
        CHAT((byte) 0),

        /**
         * A system message, like the result of a command.
         */
        SYSTEM((byte) 1),

        /**
         * A temporary message displayed above the hotbar during approximately three seconds.
         */
        ACTION_BAR((byte) 2);


        private byte messagePositionByte;

        MessageType(byte messagePositionByte)
        {
            this.messagePositionByte = messagePositionByte;
        }

        /**
         * @return the position byte to send in the chat packet.
         */
        public byte getMessagePositionByte()
        {
            return messagePositionByte;
        }
    }
}
