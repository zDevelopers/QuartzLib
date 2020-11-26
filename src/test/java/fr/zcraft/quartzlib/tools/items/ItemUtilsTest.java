/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
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

package fr.zcraft.quartzlib.tools.items;

import com.google.common.collect.ImmutableMap;
import fr.zcraft.quartzlib.MockedBukkitTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:linelength") // Justification: tests are much more readable with one per line
public class ItemUtilsTest extends MockedBukkitTest {

    @Test
    public void chatColorAreCorrectlyConvertedToDye() {
        final Map<ChatColor, DyeColor> expectedConversion = ImmutableMap.<ChatColor, DyeColor>builder()
                // All 16 colours are converted to their closest match
                .put(ChatColor.BLACK, DyeColor.BLACK)
                .put(ChatColor.BLUE, DyeColor.BLUE)
                .put(ChatColor.DARK_BLUE, DyeColor.BLUE)
                .put(ChatColor.GREEN, DyeColor.LIME)
                .put(ChatColor.DARK_GREEN, DyeColor.GREEN)
                .put(ChatColor.DARK_AQUA, DyeColor.CYAN)
                .put(ChatColor.DARK_RED, DyeColor.RED)
                .put(ChatColor.DARK_PURPLE, DyeColor.PURPLE)
                .put(ChatColor.GOLD, DyeColor.YELLOW)
                .put(ChatColor.YELLOW, DyeColor.YELLOW)
                .put(ChatColor.GRAY, DyeColor.LIGHT_GRAY)
                .put(ChatColor.DARK_GRAY, DyeColor.GRAY)
                .put(ChatColor.AQUA, DyeColor.LIGHT_BLUE)
                .put(ChatColor.RED, DyeColor.ORANGE)
                .put(ChatColor.LIGHT_PURPLE, DyeColor.PINK)
                .put(ChatColor.WHITE, DyeColor.WHITE)

                .build();

        final Set<DyeColor> dyes = new HashSet<>(expectedConversion.values());
        Assert.assertEquals(
                "All dye colors are matched against something except brown and magenta",
                dyes.size(), DyeColor.values().length - 2
        );

        Arrays.stream(ChatColor.values()).forEach(chatColor -> {
            final DyeColor dye = expectedConversion.get(chatColor);
            Assert.assertEquals(
                    chatColor + " is correctly converted to" + (dye != null ? dye : "Optional.EMPTY"),
                    ItemUtils.asDye(chatColor),
                    dye != null ? Optional.of(dye) : Optional.empty()
            );
        });
    }

    @Test
    public void blocksCanBeColorizedWithDyeColors() {
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.BANNER, DyeColor.BLUE), Material.BLUE_BANNER);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.BED, DyeColor.LIME), Material.LIME_BED);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.CARPET, DyeColor.GREEN), Material.GREEN_CARPET);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.CONCRETE, DyeColor.BROWN), Material.BROWN_CONCRETE);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.CONCRETE_POWDER, DyeColor.ORANGE), Material.ORANGE_CONCRETE_POWDER);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.DYE, DyeColor.BLACK), Material.BLACK_DYE);
    }

    @Test
    public void blocksCanBeColorizedWithChatColors() {
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.GLAZED_TERRACOTTA, ChatColor.AQUA), Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.SHULKER_BOX, ChatColor.DARK_AQUA), Material.CYAN_SHULKER_BOX);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.STAINED_GLASS, ChatColor.DARK_RED), Material.RED_STAINED_GLASS);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.STAINED_GLASS_PANE, ChatColor.RED), Material.ORANGE_STAINED_GLASS_PANE);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.TERRACOTTA, ChatColor.LIGHT_PURPLE), Material.PINK_TERRACOTTA);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.WALL_BANNER, ChatColor.MAGIC), Material.WHITE_WALL_BANNER);
        Assertions.assertEquals(ItemUtils.colorize(ColorableMaterial.WOOL, ChatColor.STRIKETHROUGH), Material.WHITE_WOOL);
    }
}
