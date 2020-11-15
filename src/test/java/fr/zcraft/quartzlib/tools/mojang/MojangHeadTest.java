package fr.zcraft.quartzlib.tools.mojang;

import fr.zcraft.quartzlib.MockedBukkitTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
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

    @Test
    void headNamesMatch() {
        HashMap<MojangHead, String> headNames = new HashMap<>();

        headNames.put(MojangHead.ALEX, "MHF_Alex");
        headNames.put(MojangHead.BLAZE, "MHF_Blaze");
        headNames.put(MojangHead.CAVE_SPIDER, "MHF_CaveSpider");
        headNames.put(MojangHead.CHICKEN, "MHF_Chicken");
        headNames.put(MojangHead.COW, "MHF_Cow");
        headNames.put(MojangHead.CREEPER, "MHF_Creeper");
        headNames.put(MojangHead.ENDERMAN, "MHF_Enderman");
        headNames.put(MojangHead.GHAST, "MHF_Ghast");
        headNames.put(MojangHead.GOLEM, "MHF_Golem");
        headNames.put(MojangHead.HEROBRINE, "MHF_Herobrine");
        headNames.put(MojangHead.LAVA_SLIME, "MHF_LavaSlime");
        headNames.put(MojangHead.MUSHROOM_COW, "MHF_MushroomCow");
        headNames.put(MojangHead.OCELOT, "MHF_Ocelot");
        headNames.put(MojangHead.PIG, "MHF_Pig");
        headNames.put(MojangHead.PIG_ZOMBIE, "MHF_PigZombie");
        headNames.put(MojangHead.SHEEP, "MHF_Sheep");
        headNames.put(MojangHead.SKELETON, "MHF_Skeleton");
        headNames.put(MojangHead.SLIME, "MHF_Slime");
        headNames.put(MojangHead.SPIDER, "MHF_Spider");
        headNames.put(MojangHead.SQUID, "MHF_Squid");
        headNames.put(MojangHead.STEVE, "MHF_Steve");
        headNames.put(MojangHead.VILLAGER, "MHF_Villager");
        headNames.put(MojangHead.WITHER_SKELETON, "MHF_WSkeleton");
        headNames.put(MojangHead.ZOMBIE, "MHF_Zombie");

        headNames.put(MojangHead.CACTUS, "MHF_Cactus");
        headNames.put(MojangHead.CAKE, "MHF_Cake");
        headNames.put(MojangHead.CHEST, "MHF_Chest");
        headNames.put(MojangHead.COCONUT_BROWN, "MHF_CoconutB");
        headNames.put(MojangHead.COCONUT_GREEN, "MHF_CoconutG");
        headNames.put(MojangHead.MELON, "MHF_Melon");
        headNames.put(MojangHead.OAK_LOG, "MHF_OakLog");
        headNames.put(MojangHead.PRESENT, "MHF_Present1");
        headNames.put(MojangHead.PRESENT_2, "MHF_Present2");
        headNames.put(MojangHead.PUMPKIN, "MHF_Pumpkin");
        headNames.put(MojangHead.TNT, "MHF_TNT");
        headNames.put(MojangHead.TNT_2, "MHF_TNT2");

        headNames.put(MojangHead.ARROW_UP, "MHF_ArrowUp");
        headNames.put(MojangHead.ARROW_DOWN, "MHF_ArrowDown");
        headNames.put(MojangHead.ARROW_LEFT, "MHF_ArrowLeft");
        headNames.put(MojangHead.ARROW_RIGHT, "MHF_ArrowRight");
        headNames.put(MojangHead.EXCLAMATION, "MHF_Exclamation");
        headNames.put(MojangHead.QUESTION, "MHF_Question");

        Assertions.assertEquals(headNames.size(), MojangHead.values().length);
        Assertions.assertTrue(headNames.keySet().containsAll(Arrays.asList(MojangHead.values().clone())));

        headNames.forEach((head, name) -> Assertions.assertEquals(name, head.getHeadName()));
    }
}
