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

package fr.zcraft.zlib.tools.items;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

/**
 * This class provides various utilities for Crafting Recipes management.
 * Crafting recipes can be registered safely before the plugin/server is loaded.
 * If this is the case, their registration will be done at plugin initialization. 
 */
public class CraftingRecipes extends ZLibComponent
{
    static private boolean registered = false;
    static private boolean enabled = false;
    static private final ArrayList<Recipe> recipesToLoad = new ArrayList<>();
    
    @Override
    protected void onEnable()
    {
        for(Recipe recipe : recipesToLoad)
        {
            ZLib.getPlugin().getServer().addRecipe(recipe);
        }
        
        recipesToLoad.clear();
        enabled = true;
    }
    
    static private boolean checkEnabled()
    {
        if(registered) return enabled;
        
        ZLib.loadComponent(CraftingRecipes.class);
        registered = true;
        
        return true;
    }
    
    /**
     * Registers a new recipe.
     * @param recipe The recipe to register.
     */
    static public void add(Recipe recipe)
    {
        if(recipe == null) return;
        
        if(checkEnabled())
        {
            ZLib.getPlugin().getServer().addRecipe(recipe);
        }
        else
        {
            recipesToLoad.add(recipe);
        }
    }
    
    /**
     * Registers several new recipes.
     * @param recipes 
     */
    static public void add(Recipe... recipes)
    {
        if(recipes == null) return;
        
        for(Recipe recipe : recipes)
        {
            add(recipe);
        }
    }
    
    /**
     * Registers several new recipes.
     * @param recipes 
     */
    static public void add(Iterable<Recipe> recipes)
    {
        if(recipes == null) return;
        
        for(Recipe recipe : recipes)
        {
            add(recipe);
        }
    }
    
    /**
     * Generates a recipe string array from a single string.
     * If the string is shorter than 9 characters, spaces will be appended.
     * @param str The recipe string
     * @return The recipe string array
     */
    static public String[] getRecipeShape(String str)
    {
        if(str.length() > 9) throw new IllegalArgumentException("Invalid recipe shape string");
        
        while(str.length() < 9)
        {
            str += " ";
        }
        
        return new String[]{
            str.substring(0, 2),
            str.substring(3, 5),
            str.substring(6, 8)
        };
    }
    
    static private String[] generateRecipeShape(int count)
    {
        String shape = "";
        char key = 'A';
        for(;count --> 0;)
        {
            shape += key;
            key = (char)(key + 1);
        }
        return getRecipeShape(shape);
    }
    
    /**
     * Generates a dummy shaped recipe, without any specified shape.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param result The resulting item of the recipe.
     * @param materials The ingredients for the recipe
     * @return The dummy recipe.
     */
    static public ShapedRecipe shaped(ItemStack result, Material... materials)
    {
        ShapedRecipe recipe = new ShapedRecipe(result)
                .shape(generateRecipeShape(materials.length));
                
        if(materials.length > 9)
            throw new IllegalArgumentException("Too many materials for a recipe.");
        
        char key = 'A';
        for(int i = 0; i < materials.length; ++i)
        {
            recipe.setIngredient(key, materials[i]);
            key = (char)(key + 1);
        }
        
        return recipe;
    }
    
    /**
     * Generates a dummy shaped recipe, without any specified shape.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param result The resulting item of the recipe.
     * @param materials The ingredients for the recipe
     * @return The dummy recipe.
     */
    static public ShapedRecipe shaped(ItemStack result, MaterialData... materials)
    {
        ShapedRecipe recipe = new ShapedRecipe(result)
                .shape(generateRecipeShape(materials.length));
                
        if(materials.length > 9)
            throw new IllegalArgumentException("Too many materials for a recipe.");
        
        char key = 'A';
        for(int i = 0; i < materials.length; ++i)
        {
            recipe.setIngredient(key, materials[i]);
            key = (char)(key + 1);
        }
        
        return recipe;
    }
    
    /**
     * Generates a new shaped recipe.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param result The resulting item of the recipe.
     * @param line1 The first line of the recipe
     * @param line2 The second line of the recipe
     * @param line3 The third line of the recipe
     * @param materials The ingredients for the recipe
     * @return The shaped recipe
     */
    static public ShapedRecipe shaped(ItemStack result, String line1, String line2, String line3, Material... materials)
    {
        ShapedRecipe recipe = shaped(result, materials);
        recipe.shape(line1, line2, line3);
        return recipe;
    }
    
    /**
     * Generates a new shaped recipe.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param result The resulting item of the recipe.
     * @param line1 The first line of the recipe
     * @param line2 The second line of the recipe
     * @param line3 The third line of the recipe
     * @return The shaped recipe
     */
    static public ShapedRecipe shaped(ItemStack result, String line1, String line2, String line3)
    {
        return shaped(result, line1, line2, line3, new Material[]{});
    }
    
    /**
     * Generates a new shaped recipe.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param result The resulting item of the recipe.
     * @param line1 The first line of the recipe
     * @param line2 The second line of the recipe
     * @param line3 The third line of the recipe
     * @param materials The ingredients for the recipe
     * @return The shaped recipe
     */
    static public ShapedRecipe shaped(ItemStack result, String line1, String line2, String line3, MaterialData... materials)
    {
        ShapedRecipe recipe = shaped(result, materials);
        recipe.shape(line1, line2, line3);
        return recipe;
    }
    
    /**
     * Generates a new shaped recipe, using the same ingredients and results as
     * the given recipe.
     * Ingredients are automatically indexed, using uppercase letters
     * (A = first ingredient, B = second ingredient, etc.)
     * @param other The recipe to retreive the ingredients and result from.
     * @param line1 The first line of the recipe
     * @param line2 The second line of the recipe
     * @param line3 The third line of the recipe
     * @return The shaped recipe
     */
    static public ShapedRecipe shaped(ShapedRecipe other, String line1, String line2, String line3)
    {
        ShapedRecipe newRecipe = shaped(other.getResult(), line1, line2, line3);
        
        for(char key : other.getIngredientMap().keySet())
        {
            ItemStack ingredient =  other.getIngredientMap().get(key);
            if(ingredient != null && ingredient.getType() != Material.AIR) 
                newRecipe.setIngredient(key, ingredient.getData());
        }
        
        return newRecipe;
    }
    
    /**
     * Returns a list of all the possible recipes for two ingredients in a
     * 2x2 shaped, placed diagonally.
     * Example :
     * A B -
     * B A -
     * - - -
     * @param a The first material of the recipe.
     * @param b The second material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2DiagonalRecipes(Material a, Material b, ItemStack result)
    {
        return get2x2DiagonalRecipes(new MaterialData(a), new MaterialData(b), result);
    }
    
    /**
     * Returns a list of all the possible recipes for two ingredients in a
     * 2x2 shaped, placed diagonally.
     * Example :
     * A B -
     * B A -
     * - - -
     * @param a The first material of the recipe.
     * @param b The second material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2DiagonalRecipes(MaterialData a, MaterialData b, ItemStack result)
    {
        ArrayList<Recipe> recipes = new ArrayList<>();
        
        recipes.add(shaped(result, "AB ", "BA ", "   ", a, b));
        recipes.add(shaped(result, "BA ", "AB ", "   ", a, b));
        recipes.add(shaped(result, " AB", " BA", "   ", a, b));
        recipes.add(shaped(result, " BA", " AB", "   ", a, b));
        recipes.add(shaped(result, "   ", "AB ", "BA ", a, b));
        recipes.add(shaped(result, "   ", "BA ", "AB ", a, b));
        recipes.add(shaped(result, "   ", " AB", " BA", a, b));
        recipes.add(shaped(result, "   ", " BA", " AB", a, b));
        
        return recipes;
    }
    
     /**
     * Returns a list of all the possible recipes for one ingredient in a
     * 2x2 shape, placed diagonally. The other diagonal is empty.
     * Example :
     * A - -
     * - A -
     * - - -
     * @param a The material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2DiagonalRecipes(Material a, ItemStack result)
    {
        return get2x2DiagonalRecipes(new MaterialData(a), result);
    }
    
    /**
     * Returns a list of all the possible recipes for one ingredient in a
     * 2x2 shape, placed diagonally. The other diagonal is empty.
     * Example :
     * A - -
     * - A -
     * - - -
     * @param a The material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2DiagonalRecipes(MaterialData a, ItemStack result)
    {
        ArrayList<Recipe> recipes = new ArrayList<>();
        
        recipes.add(shaped(result, "A  ", " A ", "   ", a));
        recipes.add(shaped(result, " A ", "A  ", "   ", a));
        recipes.add(shaped(result, " A ", "  A", "   ", a));
        recipes.add(shaped(result, "  A", " A ", "   ", a));
        recipes.add(shaped(result, "   ", "A  ", " A ", a));
        recipes.add(shaped(result, "   ", " A ", "A  ", a));
        recipes.add(shaped(result, "   ", " A ", "  A", a));
        recipes.add(shaped(result, "   ", "  A", " A ", a));
        
        return recipes;
    }
    
    /**
     * Returns a list of all the possible recipes for one ingredient in a
     * 2x2 shape.
     * Example :
     * A A -
     * A A -
     * - - -
     * @param a The material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2Recipes(Material a, ItemStack result)
    {
        return get2x2Recipes(new MaterialData(a), result);
    }
    
    /**
     * Returns a list of all the possible recipes for one ingredient in a
     * 2x2 shape.
     * Example :
     * A A -
     * A A -
     * - - -
     * @param a The material of the recipe.
     * @param result The resulting item of the recipe.
     * @return All the possible recipes.
     */
    static public List<Recipe> get2x2Recipes(MaterialData a, ItemStack result)
    {
        ArrayList<Recipe> recipes = new ArrayList<>();
        
        recipes.add(shaped(result, "AA ", "AA ", "   ", a));
        recipes.add(shaped(result, " AA", " AA", "   ", a));
        recipes.add(shaped(result, "   ", "AA ", "AA ", a));
        recipes.add(shaped(result, "   ", " AA", " AA", a));
        
        return recipes;
    }
}
