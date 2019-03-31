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

package fr.zcraft.zlib.components.gui;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.tools.Callback;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class PromptGui extends GuiBase
{
    static private final int SIGN_LINES_COUNT = 4;
    static private final int SIGN_COLUMNS_COUNT = 15;

    static private boolean isInitialized = false;

    /* ===== Reflection to Sign API ===== */
    static private Field fieldTileEntitySign = null; // 1.11.2-: CraftSign.sign; 1.12+: CraftBlockEntityState.tileEntity
    static private Field fieldTileEntitySignEditable = null; // 1.12+ only: CraftBlockEntityState.isEditable
    static private Method methodGetHandle = null; // CraftPlayer.getHandle()
    static private Method methodOpenSign = null; // EntityHuman.openSign()

    static public boolean isAvailable()
    {
        if (!isInitialized) init();
        return fieldTileEntitySign != null;
    }

    static public void prompt(Player owner, Callback<String> callback)
    {
        prompt(owner, callback, "", null);
    }

    static public void prompt(Player owner, Callback<String> callback, String contents, GuiBase parent)
    {
        Gui.open(owner, new PromptGui(callback, contents), parent);
    }

    static private void init()
    {
        isInitialized = true;

        try
        {
            final Class<?> CraftBlockEntityState = Reflection.getBukkitClassByName("block.CraftBlockEntityState");
            final Class<?> CraftSign = Reflection.getBukkitClassByName("block.CraftSign");
            final Class<?> classTileEntitySign = Reflection.getMinecraftClassByName("TileEntitySign");
            final Class<?> CraftPlayer = Reflection.getBukkitClassByName("entity.CraftPlayer");
            final Class<?> EntityHuman = Reflection.getMinecraftClassByName("EntityHuman");

            try
            {
                fieldTileEntitySign = Reflection.getField(CraftSign, "sign");
            }
            catch (NoSuchFieldException e) // 1.12+
            {
                fieldTileEntitySign = Reflection.getField(CraftBlockEntityState, "tileEntity");
            }

            try
            {
                fieldTileEntitySignEditable = Reflection.getField(classTileEntitySign, "isEditable");
            }
            catch (NoSuchFieldException e) // 1.11.2 or below
            {
                fieldTileEntitySignEditable = null;
            }

            methodGetHandle = CraftPlayer.getDeclaredMethod("getHandle");
            methodOpenSign = EntityHuman.getDeclaredMethod("openSign", classTileEntitySign);
        }
        catch (Exception e)
        {
            PluginLogger.error("Unable to initialize Sign Prompt API", e);
            fieldTileEntitySign = null;
        }
    }

    private final Callback<String> callback;
    private Location signLocation;
    private String contents;

    public PromptGui(Callback<String> callback, String contents)
    {
        this(callback);
        this.contents = contents;
    }

    public PromptGui(Callback<String> callback)
    {
        super();
        if (!isAvailable())
            throw new IllegalStateException("Sign-based prompt GUI are not available");

        this.callback = callback;
    }

    @Override
    protected void open(final Player player)
    {
        super.open(player);

        signLocation = findAvailableLocation(player);

        if (signLocation == null)
        {
            throw new RuntimeException("Too many players are using a PromptGui at the same time. (…wait, the limit is about 7 500, how did you do that??)");
        }

        // Ugly workaround for spigot still applying physics in spigot 1.9.2+
        signLocation.getWorld().getBlockAt(signLocation.clone().add(0, -1, 0)).setType(Material.GLASS);

        final Block block = signLocation.getWorld().getBlockAt(signLocation);
        block.setType(Material.SIGN, false);

        final Sign sign = (Sign) block.getState();
        setSignContents(sign, contents);
        sign.update();

        Bukkit.getScheduler().scheduleSyncDelayedTask(ZLib.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final Object signTileEntity = fieldTileEntitySign.get(sign);
                    final Object playerEntity = methodGetHandle.invoke(player);

                    // In Minecraft 1.12+, there's a lock on the signs to avoid them
                    // to be edited after they are loaded into the game.
                    if (fieldTileEntitySignEditable != null)
                        fieldTileEntitySignEditable.set(signTileEntity, true);

                    methodOpenSign.invoke(playerEntity, signTileEntity);
                }
                catch (final Throwable e)
                {
                    PluginLogger.error("Error while opening Sign prompt", e);
                }
            }
        }, 3);
    }

    @Override
    protected void onClose()
    {
        final Block block = signLocation.getWorld().getBlockAt(signLocation);
        block.setType(Material.AIR);

        signLocation.getWorld().getBlockAt(signLocation.clone().add(0, -1, 0)).setType(Material.AIR);

        super.onClose();
    }

    private void validate(String[] lines)
    {
        callback.call(getSignContents(lines));

        // Bukkit sends extra InventoryCloseEvents when closing a sign GUI...
        this.close(true);
    }

    static private String getSignContents(String[] lines)
    {
        StringBuilder content = new StringBuilder(lines[0].trim());

        for (int i = 1; i < lines.length; i++)
        {
            if (lines[i] == null || lines[i].isEmpty()) continue;
            content.append(" ").append(lines[i].trim());
        }

        return content.toString().trim();
    }

    static private void setSignContents(Sign sign, String content)
    {
        String[] lines = new String[SIGN_LINES_COUNT + 1];
        String curLine;
        int curLineIndex = 0, spacePos;

        if (content != null)
        {
            lines[0] = content;
            while (curLineIndex < SIGN_LINES_COUNT)
            {
                curLine = lines[curLineIndex];
                if (curLine.length() <= SIGN_COLUMNS_COUNT)
                    break;

                spacePos = curLine.lastIndexOf(' ', SIGN_COLUMNS_COUNT);
                if (spacePos < 0) break;
                lines[curLineIndex + 1] = curLine.substring(spacePos + 1);
                lines[curLineIndex] = curLine.substring(0, spacePos);
                curLineIndex++;
            }
        }

        for (int i = SIGN_LINES_COUNT; i-- > 0; )
        {
            sign.setLine(i, lines[i]);
        }
    }

    static private Location findAvailableLocation(Player player)
    {
        World world = player.getWorld();
        Chunk playerChunk = player.getLocation().getChunk();
        Chunk firstChunk = world.getChunkAt(playerChunk.getX() - 1, playerChunk.getZ() - 1);
        Location firstLoc = firstChunk.getBlock(0, 255, 0).getLocation();
        Location loc;

        for (int i = 48; i-- > 0; )
        {
            for (int j = 0; j-- > -10; )
            {
                for (int k = 48; k-- > 0; )
                {
                    loc = firstLoc.add(i, j, k);
                    if (hasSpace(world, loc))
                        return loc;
                }
            }
        }

        return null;
    }

    static private boolean hasSpace(World world, Location loc)
    {
        if (!Material.AIR.equals(world.getBlockAt(loc).getType()))
            return false;

        for (int i = 1; i-- > -1; )
        {
            for (int j = 1; j-- > -2; )
            {
                for (int k = 1; k-- > -1; )
                {
                    if (!Material.AIR.equals(world.getBlockAt(
                            loc.getBlockX() + i, loc.getBlockY() + j, loc.getBlockZ() + k)
                            .getType()))
                        return false;
                }
            }
        }

        return true;
    }

    @Override
    protected Listener getEventListener() { return new PromptGuiListener(); }

    private final class PromptGuiListener implements Listener
    {
        @EventHandler
        public void onSignChange(SignChangeEvent event)
        {
            if (event.getPlayer() != getPlayer()) return;
            validate(event.getLines());
        }
    }
}
