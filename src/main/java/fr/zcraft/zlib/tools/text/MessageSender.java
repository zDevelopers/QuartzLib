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
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.NMSNetwork;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


public final class MessageSender
{
    private static boolean enabled = true;

    private final static String nmsVersion = Reflection.getBukkitPackageVersion();

    private static Class<?> packetPlayOutChatClass;
    private static Class<?> chatSerializerClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> chatComponentTextClass;

    private static Class<?> chatMessageTypeEnum;
    private static Method chatMessageByteToTypeMethod;

    static
    {
        try
        {
            iChatBaseComponentClass = Reflection.getMinecraftClassByName("IChatBaseComponent");
            packetPlayOutChatClass = Reflection.getMinecraftClassByName("PacketPlayOutChat");

            // TODO centralize the chat serialization mechanisms
            try
            {
                chatSerializerClass = Reflection.getMinecraftClassByName("ChatSerializer");
            }
            catch (ClassNotFoundException e)
            {
                chatSerializerClass = Reflection.getMinecraftClassByName("IChatBaseComponent$ChatSerializer");
            }

            // We only support 1.8+
            if (!nmsVersion.equalsIgnoreCase("v1_8_R1"))
            {
                chatComponentTextClass = Reflection.getMinecraftClassByName("ChatComponentText");

                // This enum was introduced in 1.12;  before, a byte was directly used.
                try {
                    chatMessageTypeEnum = Reflection.getMinecraftClassByName("ChatMessageType");
                    chatMessageByteToTypeMethod = Reflection.findMethod(chatMessageTypeEnum, "a", byte.class);

                    if (chatMessageByteToTypeMethod == null)
                    {
                        PluginLogger.error("You are using a version of Minecraft ({0}) incompatible with zLib.", nmsVersion);
                        PluginLogger.error("The MessageSender component will not work due to a change in Minecraft code.");
                        PluginLogger.error("Please report this to the zLib developers at https://github.com/zDevelopers/zLib/issues - thanks you.");

                        chatMessageTypeEnum = null;
                    }
                }
                catch (ClassNotFoundException e)
                {
                    chatMessageTypeEnum = null;
                    chatMessageByteToTypeMethod = null;
                }
            }
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
        return sendChatPacket(
                receiver, type.isJSON() ? "{\"text\": \"" + message.replace("\"", "\\\"") + "\"}" : message, type
        );
    }

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
    public static boolean sendMessage(Player receiver, RawText message, MessageType type)
    {
        return sendChatPacket(receiver, type.isJSON() ? message.toJSONString() : message.toFormattedText(), type);
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

        return!(player == null || !player.isOnline()) && sendMessage(player, message, type);
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
    public static boolean sendMessage(UUID receiver, RawText message, MessageType type)
    {
        Player player = Bukkit.getPlayer(receiver);

        return !(player == null || !player.isOnline()) && sendMessage(player, message, type);
    }


    /**
     * Sends a message.
     *
     * <p><strong>WARNING:</strong> don't use this method to send action bars, as they does NOT support JSON (the JSON code
     * would be displayed in the action bar).</p>
     *
     * @param receiver The receiver of the message.
     * @param json  The JSON message to be sent.
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
    public static boolean sendJSONMessage(Player receiver, String json, MessageType type)
    {
        return sendChatPacket(receiver, json, type);
    }

    /**
     * Sends a message.
     *
     * <p><strong>WARNING:</strong> don't use this method to send action bars, as they does NOT support JSON (the JSON code
     * would be displayed in the action bar).</p>
     *
     * @param receiver The UUId of the receiver of the message.
     * @param json  The message to be sent.
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
    public static boolean sendJSONMessage(UUID receiver, String json, MessageType type)
    {
        Player player = Bukkit.getPlayer(receiver);

        return !(player == null || !player.isOnline()) && sendJSONMessage(player, json, type);
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
    public static boolean sendChatMessage(Player receiver, RawText message)
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
    public static boolean sendChatMessage(UUID receiver, RawText message)
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
    public static boolean sendSystemMessage(Player receiver, RawText message)
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
    public static boolean sendSystemMessage(UUID receiver, RawText message)
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
    public static boolean sendActionBarMessage(Player receiver, RawText message)
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
    public static boolean sendActionBarMessage(UUID receiver, RawText message)
    {
        return sendMessage(receiver, message, MessageType.ACTION_BAR);
    }


    /**
     * Sends a chat packet.
     *
     * @param receiver The player receiving the packet.
     * @param content The raw content to be sent in the packet (either JSON or plain text), according to the chat packet type.
     * @param type The chat packet type.
     * @return {@code true} if the packet was sent.
     */
    private static boolean sendChatPacket(Player receiver, String content, MessageType type)
    {
        if (receiver == null || content == null) return false;

        // Fallback to sendMessage if a problem occurs.
        if (!enabled)
        {
            if (type != MessageType.ACTION_BAR)
                receiver.sendMessage(content);

            return false;
        }

        if (type == null) type = MessageType.SYSTEM;

        Object chatPacket = null;

        try
        {
            if (nmsVersion.equalsIgnoreCase("v1_8_R1"))
            {
                Object baseComponent = iChatBaseComponentClass.cast(Reflection.call(chatSerializerClass, chatSerializerClass, "a", content));
                chatPacket = Reflection.instantiate(packetPlayOutChatClass, baseComponent, type.getMessagePositionByte());
            }
            else
            {
                Object componentText;

                if (type.isJSON())
                {
                    componentText = iChatBaseComponentClass.cast(Reflection.call(chatSerializerClass, chatSerializerClass, "a", content));
                }
                else
                {
                    componentText = Reflection.instantiate(chatComponentTextClass, content);
                }

                final Enum<?> nmsMessageType = type.getMessagePositionEnumValue();

                if (nmsMessageType != null)
                    chatPacket = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeEnum).newInstance(componentText, nmsMessageType);
                else
                    chatPacket = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, byte.class).newInstance(componentText, type.getMessagePositionByte());
            }

            NMSNetwork.sendPacket(receiver, chatPacket);
            return true;
        }
        catch (Exception e)
        {
            PluginLogger.error(
                "Unable to send packet {0} to player {1} (UUID = {2}).", e,
                chatPacket != null ? chatPacket.getClass().getName() : "<null>",
                receiver.getName(), receiver.getUniqueId()
            );

            if (type != MessageType.ACTION_BAR)
                receiver.sendMessage(content);

            return false;
        }
    }


    public enum MessageType
    {
        /**
         * A chat message, like a message in the main channel or a private message.
         *
         * A message of this kind will be hidden if the « Show chat » option is set to « commands
         * only ».
         */
        CHAT((byte) 0, true),

        /**
         * A system message, like the result of a command.
         */
        SYSTEM((byte) 1, true),

        /**
         * A temporary message displayed above the hotbar during approximately three seconds.
         */
        ACTION_BAR((byte) 2, false);


        private byte messagePositionByte;
        private boolean isJSON;

        MessageType(byte messagePositionByte, boolean isJSON)
        {
            this.messagePositionByte = messagePositionByte;
            this.isJSON = isJSON;
        }

        /**
         * @return the position byte to send in the chat packet.
         */
        public byte getMessagePositionByte()
        {
            return messagePositionByte;
        }

        /**
         * If the Minecraft version uses enum to assign message type into the packet, returns
         * the Enum value to be used for this message type.
         *
         * Else, returns {@code null}.
         *
         * @return The enum value to use, if the system uses them; {@code null}, else.
         * @see #getMessagePositionByte() The method returning the byte to use if the server uses bytes.
         */
        public Enum<?> getMessagePositionEnumValue()
        {
            if (chatMessageTypeEnum == null)
                return null;

            try
            {
                chatMessageByteToTypeMethod.setAccessible(true);

                return (Enum<?>) chatMessageByteToTypeMethod.invoke(null, messagePositionByte);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                PluginLogger.error("Unable to retrieve the ");
                return null;
            }
        }

        /**
         * @return {@code true} if the chat packet wants a JSON-formatted message, {@code false} else.
         */
        public boolean isJSON()
        {
            return isJSON;
        }
    }
}
