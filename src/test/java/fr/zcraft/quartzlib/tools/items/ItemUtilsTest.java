/*
 * Plugin UHCReloaded : Alliances
 *
 * Copyright ou © ou Copr. Amaury Carrade (2016)
 * Idées et réflexions : Alexandre Prokopowicz, Amaury Carrade, "Vayan".
 *
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA
 * sur le site "http://www.cecill.info".
 *
 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.
 *
 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement,
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
 *
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */

package fr.zcraft.quartzlib.tools.items;

import com.google.common.collect.ImmutableMap;
import fr.zcraft.quartzlib.MockedBukkitTest;
import java.util.HashSet;
import java.util.Map;
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

                // Formatting codes are converted to white
                .put(ChatColor.BOLD, DyeColor.WHITE)
                .put(ChatColor.ITALIC, DyeColor.WHITE)
                .put(ChatColor.UNDERLINE, DyeColor.WHITE)
                .put(ChatColor.STRIKETHROUGH, DyeColor.WHITE)
                .put(ChatColor.MAGIC, DyeColor.WHITE)
                .put(ChatColor.RESET, DyeColor.WHITE)

                .build();

        Assert.assertEquals("All chat colors are covered", expectedConversion.size(), ChatColor.values().length);

        final Set<DyeColor> dyes = new HashSet<>(expectedConversion.values());
        Assert.assertEquals(
                "All dye colors are matched against something except brown and magenta",
                dyes.size(), DyeColor.values().length - 2
        );

        expectedConversion.forEach((chatColor, dye) -> Assert.assertEquals(chatColor + " is correctly converted to" + dye, ItemUtils.asDye(chatColor), dye));
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
