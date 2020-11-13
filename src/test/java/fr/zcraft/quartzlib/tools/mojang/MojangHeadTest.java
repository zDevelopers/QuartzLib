package fr.zcraft.quartzlib.tools.mojang;

import fr.zcraft.quartzlib.MockedBukkitTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

class MojangHeadTest extends MockedBukkitTest {
    @Test
    void canCreateHeadItems() {
        Assertions.assertEquals("MHF_Cake", MojangHead.CAKE.getHeadName());
        ItemStack item = MojangHead.CAKE.asItem();

        Assertions.assertEquals(Material.PLAYER_HEAD, item.getType());
        Assertions.assertEquals(1, item.getAmount());
        Assertions.assertEquals("MHF_Cake", ((SkullMeta) Objects.requireNonNull(item.getItemMeta())).getOwner());

        ItemStack isbItem = MojangHead.CAKE.asItemBuilder().item();

        Assertions.assertEquals(Material.PLAYER_HEAD, isbItem.getType());
        Assertions.assertEquals(1, item.getAmount());
        Assertions.assertEquals("MHF_Cake", ((SkullMeta) Objects.requireNonNull(isbItem.getItemMeta())).getOwner());
    }
}
