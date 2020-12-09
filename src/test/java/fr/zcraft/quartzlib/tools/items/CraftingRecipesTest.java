package fr.zcraft.quartzlib.tools.items;

import fr.zcraft.quartzlib.MockedToasterTest;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.Assert;
import org.junit.Test;

public class CraftingRecipesTest extends MockedToasterTest {

    @Test
    public void canGenerateRecipesShapes() {
        String[] shapes = CraftingRecipes.getRecipeShape("ABCDEFGHI");
        Assert.assertArrayEquals(new String[] {"ABC", "DEF", "GHI"}, shapes);

        String[] shapesWithSpace = CraftingRecipes.getRecipeShape("ABCDEFG");
        Assert.assertArrayEquals(new String[] {"ABC", "DEF", "G  "}, shapesWithSpace);
    }

    @Test
    public void canGenerateDummyShape() {
        // FIXME: this is a legacy material here, because of missing implementations in MockBukkit
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", result, Material.QUARTZ, Material.DIORITE);
        Assert.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assert.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assert.assertEquals(recipe.getResult().getType(), result.getType());
        Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());

        recipe = CraftingRecipes.shaped("foo", result, new RecipeChoice.MaterialChoice(Material.QUARTZ));
        Assert.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assert.assertEquals(recipe.getResult().getType(), result.getType());
        Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
    }

    @Test
    public void canGenerateSimpleShape() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", result,
                "AB ", "   ", "  A",
                Material.QUARTZ, Material.DIORITE);
        Assert.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assert.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assert.assertEquals(recipe.getResult().getType(), result.getType());
        Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assert.assertArrayEquals(new String[] {"AB ", "   ", "  A"}, recipe.getShape());

        recipe = CraftingRecipes.shaped("foo", result,
                "AB ", "   ", "  A",
                new RecipeChoice.MaterialChoice(Material.QUARTZ));
        Assert.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assert.assertEquals(recipe.getResult().getType(), result.getType());
        Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assert.assertArrayEquals(new String[] {"AB ", "   ", "  A"}, recipe.getShape());
    }

    @Test
    public void canGenerateRecipeMatchingOther() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe originalRecipe = CraftingRecipes.shaped("foo", result,
                "AB ", "   ", "  A",
                Material.QUARTZ, Material.DIORITE);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", originalRecipe, "AA ", "   ", "  B");
        Assert.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assert.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assert.assertEquals(recipe.getResult().getType(), result.getType());
        Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assert.assertArrayEquals(new String[] {"AA ", "   ", "  B"}, recipe.getShape());
    }

    @Test
    public void canGenerate2x2DiagonalRecipesWithOneMaterial() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
                Material.QUARTZ, result);

        Assert.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"A  ", " A ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {" A ", "A  ", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {" A ", "  A", "   "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {"  A", " A ", "   "}, recipes.get(3).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "A  ", " A "}, recipes.get(4).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " A ", "A  "}, recipes.get(5).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " A ", "  A"}, recipes.get(6).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "  A", " A "}, recipes.get(7).getShape());

        recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
                new RecipeChoice.MaterialChoice(Material.QUARTZ), result);

        Assert.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"A  ", " A ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {" A ", "A  ", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {" A ", "  A", "   "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {"  A", " A ", "   "}, recipes.get(3).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "A  ", " A "}, recipes.get(4).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " A ", "A  "}, recipes.get(5).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " A ", "  A"}, recipes.get(6).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "  A", " A "}, recipes.get(7).getShape());
    }

    @Test
    public void canGenerate2x2DiagonalRecipesWithTwoMaterials() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
                Material.QUARTZ, Material.DIORITE, result);

        Assert.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"AB ", "BA ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {"BA ", "AB ", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {" AB", " BA", "   "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {" BA", " AB", "   "}, recipes.get(3).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "AB ", "BA "}, recipes.get(4).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "BA ", "AB "}, recipes.get(5).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " AB", " BA"}, recipes.get(6).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " BA", " AB"}, recipes.get(7).getShape());

        recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
                new RecipeChoice.MaterialChoice(Material.QUARTZ),
                new RecipeChoice.MaterialChoice(Material.DIORITE), result);

        Assert.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"AB ", "BA ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {"BA ", "AB ", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {" AB", " BA", "   "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {" BA", " AB", "   "}, recipes.get(3).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "AB ", "BA "}, recipes.get(4).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "BA ", "AB "}, recipes.get(5).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " AB", " BA"}, recipes.get(6).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " BA", " AB"}, recipes.get(7).getShape());
    }

    @Test
    public void canGenerate2x2RecipesWithOneMaterial() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2Recipes("foo", Material.QUARTZ, result);

        Assert.assertEquals(4, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"AA ", "AA ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {" AA", " AA", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "AA ", "AA "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " AA", " AA"}, recipes.get(3).getShape());

        recipes = CraftingRecipes.get2x2Recipes("foo",
                new RecipeChoice.MaterialChoice(Material.QUARTZ), result);

        Assert.assertEquals(4, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assert.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assert.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assert.assertEquals(recipe.getResult().getType(), result.getType());
            Assert.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assert.assertArrayEquals(new String[] {"AA ", "AA ", "   "}, recipes.get(0).getShape());
        Assert.assertArrayEquals(new String[] {" AA", " AA", "   "}, recipes.get(1).getShape());
        Assert.assertArrayEquals(new String[] {"   ", "AA ", "AA "}, recipes.get(2).getShape());
        Assert.assertArrayEquals(new String[] {"   ", " AA", " AA"}, recipes.get(3).getShape());
    }
}
