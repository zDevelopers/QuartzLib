package fr.zcraft.quartzlib.tools.items;

import fr.zcraft.quartzlib.MockedToasterTest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CraftingRecipesTest extends MockedToasterTest {

    @Test
    public void canGenerateRecipesShapes() {
        String[] shapes = CraftingRecipes.getRecipeShape("ABCDEFGHI");
        Assertions.assertArrayEquals(new String[] {"ABC", "DEF", "GHI"}, shapes);

        String[] shapesWithSpace = CraftingRecipes.getRecipeShape("ABCDEFG");
        Assertions.assertArrayEquals(new String[] {"ABC", "DEF", "G  "}, shapesWithSpace);
    }
}
