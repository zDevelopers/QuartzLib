package fr.zcraft.quartzlib.tools.items;

import fr.zcraft.quartzlib.MockedBukkitTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ItemStackBuilderTest extends MockedBukkitTest {
    @Rule
    public ExpectedException ilgStExceptionRule = ExpectedException.none();

    @Test
    public void throwsOnEmptyBuilder() {
        ilgStExceptionRule.expect(IllegalStateException.class);
        ilgStExceptionRule.expectMessage("Cannot build item without a specified material");

        new ItemStackBuilder().item();
    }

    @Test
    public void buildsWithDefaultAmount() {
        Assert.assertEquals(1, new ItemStackBuilder(Material.QUARTZ).item().getAmount());
    }

    @Test
    public void canBuildBasicItemStack() {
        ItemStack itemStack = new ItemStackBuilder(Material.QUARTZ, 42).item();

        Assert.assertEquals(Material.QUARTZ, itemStack.getType());
        Assert.assertEquals(42, itemStack.getAmount());
    }

    @Test
    public void canEditExistingItemStack() {
        ItemStack itemStack = new ItemStack(Material.QUARTZ, 42);
        new ItemStackBuilder(itemStack).amount(50).item();

        Assert.assertEquals(Material.QUARTZ, itemStack.getType());
        Assert.assertEquals(50, itemStack.getAmount());

        new ItemStackBuilder(itemStack).material(Material.DIORITE).item();

        Assert.assertEquals(Material.DIORITE, itemStack.getType());
        Assert.assertEquals(50, itemStack.getAmount());
    }

    @Test
    public void noopsWhenEditingItemStackWithoutChanges() {
        ItemStack itemStack = new ItemStack(Material.QUARTZ, 42);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setLore(Arrays.asList("foo", "bar"));
        itemStack.setItemMeta(itemMeta);

        new ItemStackBuilder(itemStack).item();

        Assert.assertEquals(Material.QUARTZ, itemStack.getType());
        Assert.assertEquals(42, itemStack.getAmount());
        Assert.assertEquals(Arrays.asList("foo", "bar"), itemStack.getItemMeta().getLore());
    }

    @Test
    public void canSetDisplayName() {
        ItemStack itemStack = new ItemStackBuilder(Material.QUARTZ).title("foo").item();
        Assert.assertEquals("§rfoo§r", Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName());

        ItemStack itemStack2 = new ItemStackBuilder(Material.QUARTZ).title("foo ", "bar", " baz").item();
        Assert.assertEquals("§rfoo bar baz§r", Objects.requireNonNull(itemStack2.getItemMeta()).getDisplayName());

        ItemStack itemStack3 = new ItemStackBuilder(Material.QUARTZ).title(ChatColor.GREEN, "foo").item();
        Assert.assertEquals("§r§afoo§r", Objects.requireNonNull(itemStack3.getItemMeta()).getDisplayName());

        ItemStack itemStack4 =
                new ItemStackBuilder(Material.QUARTZ).title(ChatColor.GREEN, "foo ", "blue", " baz").item();
        Assert
                .assertEquals("§r§afoo blue baz§r", Objects.requireNonNull(itemStack4.getItemMeta()).getDisplayName());

        ItemStack itemStack5 =
                new ItemStackBuilder(Material.QUARTZ).title(ChatColor.GREEN, "foo ").title("blue ").title("baz").item();
        Assert.assertEquals("§r§afoo §rblue §rbaz§r",
                Objects.requireNonNull(itemStack5.getItemMeta()).getDisplayName());

        ItemStack itemStack6 =
                new ItemStackBuilder(Material.QUARTZ).title(ChatColor.GREEN, "foo ").title(ChatColor.BLUE, "blue ")
                        .title("baz").item();
        Assert.assertEquals("§r§afoo §r§9blue §rbaz§r",
                Objects.requireNonNull(itemStack6.getItemMeta()).getDisplayName());
    }

    @Test
    public void canSetLore() {
        ItemStack itemStack = new ItemStackBuilder(Material.QUARTZ).lore("foo").item();
        Assert.assertEquals(Collections.singletonList("foo"),
                Objects.requireNonNull(itemStack.getItemMeta()).getLore());

        ItemStack itemStack2 = new ItemStackBuilder(Material.QUARTZ).lore("foo", "bar", "baz").item();
        Assert.assertEquals(Arrays.asList("foo", "bar", "baz"),
                Objects.requireNonNull(itemStack2.getItemMeta()).getLore());

        ItemStack itemStack3 = new ItemStackBuilder(Material.QUARTZ).lore(Arrays.asList("foo", "bar", "baz")).item();
        Assert.assertEquals(Arrays.asList("foo", "bar", "baz"),
                Objects.requireNonNull(itemStack3.getItemMeta()).getLore());

        ItemStack itemStack4 = new ItemStackBuilder(Material.QUARTZ).lore("foo", "bar").lore("baz").item();
        Assert.assertEquals(Arrays.asList("foo", "bar", "baz"),
                Objects.requireNonNull(itemStack4.getItemMeta()).getLore());

        ItemStack itemStack5 = new ItemStackBuilder(Material.QUARTZ).loreLine("foo", " bar").loreLine("baz").item();
        Assert.assertEquals(Arrays.asList("foo bar", "baz"),
                Objects.requireNonNull(itemStack5.getItemMeta()).getLore());

        ItemStack itemStack6 = new ItemStackBuilder(Material.QUARTZ).loreLine("foo", " bar").lore("baz", "boo").item();
        Assert.assertEquals(Arrays.asList("foo bar", "baz", "boo"),
                Objects.requireNonNull(itemStack6.getItemMeta()).getLore());

        ItemStack itemStack7 =
                new ItemStackBuilder(Material.QUARTZ).loreLine(ChatColor.GREEN, "foo", " bar").loreLine("baz").item();
        Assert.assertEquals(Arrays.asList("§afoo bar", "baz"),
                Objects.requireNonNull(itemStack7.getItemMeta()).getLore());

        ItemStack itemStack8 = new ItemStackBuilder(Material.QUARTZ)
                .loreLine("foo")
                .longLore(ChatColor.GREEN, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.").loreLine("bar")
                .item();
        Assert.assertEquals(Arrays.asList("foo",
                "§aLorem ipsum dolor sit amet,",
                "§aconsectetur adipiscing elit.",
                "bar"), Objects.requireNonNull(itemStack8.getItemMeta()).getLore());

        ItemStack itemStack9 = new ItemStackBuilder(Material.QUARTZ)
                .loreLine("foo")
                .longLore(ChatColor.GREEN, "Lorem ipsum dolor sit amet", 10).loreLine("bar").item();
        Assert.assertEquals(Arrays.asList("foo",
                "§aLorem",
                "§aipsum",
                "§adolor sit",
                "§aamet",
                "bar"), Objects.requireNonNull(itemStack9.getItemMeta()).getLore());

        ItemStack itemStack10 = new ItemStackBuilder(Material.QUARTZ)
                .loreLine("foo")
                .loreSeparator()
                .loreLine("bar").item();
        Assert.assertEquals(Arrays.asList("foo", "", "bar"),
                Objects.requireNonNull(itemStack10.getItemMeta()).getLore());

        ItemStack itemStack11 = new ItemStackBuilder(Material.QUARTZ)
                .loreLine("foo")
                .resetLore()
                .loreLine("bar").item();
        Assert.assertEquals(Collections.singletonList("bar"),
                Objects.requireNonNull(itemStack11.getItemMeta()).getLore());
    }

    @Test
    public void canCreateSkullItem() {
        ItemStack fooSkull = new ItemStackBuilder(Material.PLAYER_HEAD)
                .withMeta((SkullMeta s) -> s.setOwner("foo"))
                .item();

        Assert.assertEquals(Material.PLAYER_HEAD, fooSkull.getType());
        Assert.assertEquals("foo", ((SkullMeta) Objects.requireNonNull(fooSkull.getItemMeta())).getOwner());

        ItemStack quartz = new ItemStackBuilder(Material.QUARTZ)
                .withMeta((SkullMeta s) -> s.setOwner("foo"))
                .item();

        Assert.assertEquals(Material.QUARTZ, quartz.getType());
        Assert.assertFalse(Objects.requireNonNull(quartz.getItemMeta()) instanceof SkullMeta);
    }

    @Test
    public void canAddFlags() {
        ItemStack itemStack = new ItemStackBuilder(Material.QUARTZ).withFlags(ItemFlag.HIDE_ATTRIBUTES).item();
        Set<ItemFlag> flags = Objects.requireNonNull(itemStack.getItemMeta()).getItemFlags();
        Assert.assertEquals(1, flags.size());
        Assert.assertTrue(flags.contains(ItemFlag.HIDE_ATTRIBUTES));

        ItemStack itemStack2 =
                new ItemStackBuilder(Material.QUARTZ).withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                        .item();
        flags = Objects.requireNonNull(itemStack2.getItemMeta()).getItemFlags();
        Assert.assertEquals(2, flags.size());
        Assert.assertTrue(flags.containsAll(Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)));

        ItemStack itemStack3 = new ItemStackBuilder(Material.QUARTZ)
                .withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS).item();
        flags = Objects.requireNonNull(itemStack3.getItemMeta()).getItemFlags();
        Assert.assertEquals(2, flags.size());
        Assert.assertTrue(flags.containsAll(Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)));

        ItemStack itemStack4 = new ItemStackBuilder(Material.QUARTZ)
                .withFlags(ItemFlag.HIDE_ATTRIBUTES)
                .withFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .item();
        flags = Objects.requireNonNull(itemStack4.getItemMeta()).getItemFlags();
        Assert.assertEquals(2, flags.size());
        Assert.assertTrue(flags.containsAll(Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)));

        ItemStack itemStack5 = new ItemStackBuilder(Material.QUARTZ)
                .hideAllAttributes()
                .item();
        flags = Objects.requireNonNull(itemStack5.getItemMeta()).getItemFlags();
        Assert.assertEquals(ItemFlag.values().length, flags.size());
        Assert.assertTrue(flags.containsAll(Arrays.asList(ItemFlag.values())));
    }
}
