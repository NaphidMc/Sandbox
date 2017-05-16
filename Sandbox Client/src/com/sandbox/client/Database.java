package com.sandbox.client;
import java.util.ArrayList;

import com.sandbox.client.item.CraftingRecipe;
import com.sandbox.client.item.Item;
import com.sandbox.client.item.ItemStack;
import com.sandbox.client.map.TileType;

public class Database {
	
	public static ArrayList<Item> items = new ArrayList<Item>();
	public static ArrayList<CraftingRecipe> craftingRecipes = new ArrayList<CraftingRecipe>();
	
	// Blocks
	public static TileType GRASS;
	public static TileType DIRT;
	public static TileType AIR;
	public static TileType STONE;
	public static TileType IRONORE;
	public static TileType BEDROCK;
	public static TileType SAPLING;
	public static TileType WOOD;
	public static TileType LEAVES;
	public static TileType TEST;
	
	// Items
	public static Item ITEM_PICKAXE;
	public static Item ITEM_DIRT;
	public static Item ITEM_GRASS;
	public static Item ITEM_GRASS_SEEDS;
	public static Item ITEM_STONE;
	public static Item ITEM_IRONORE;
	public static Item ITEM_WOODLOG;
	public static Item ITEM_IRONCHUNK;
	public static Item ITEM_SAPLING;
	
	// Recipes
	public static CraftingRecipe RECIPE_GRASS_1;
	public static CraftingRecipe RECIPE_GRASS_2;
	public static CraftingRecipe RECIPE_GRASS_3;
	public static CraftingRecipe RECIPE_GRASS_4;
	public static CraftingRecipe RECIPE_GRASS_5;
	public static CraftingRecipe RECIPE_GRASS_6;
	
	public static void populate() {
		
		// Blocks
		GRASS = new TileType("Grass", 0, new int[][] { {1, 3, 3, 3}, {100, 50, 50, 50} }, true, 20);
		DIRT = new TileType("Dirt", 3, new int[][] { {1}, {100} }, true, 20);
		AIR = new TileType("Air", 2, new int[][] {{-1}, {-1} }, false, 0);
		STONE = new TileType("Stone", 11, new int[][] { {4}, {100} }, true, 50);
		IRONORE = new TileType("Iron Ore", 6, new int[][] { {7, 7, 7, 7}, {100, 50, 50, 50} }, true, 80);
		BEDROCK = new TileType("Bedrock", 1, new int[][] {{-1},{-1}}, true, 0);
		SAPLING = new TileType("Sapling", 13, new int[][] {{8}, {100}}, false, 5);
		WOOD = new TileType("Wood", 14, new int[][] { {6}, {100}}, false, 30);
		LEAVES = new TileType("Leaves", 7, new int[][] { {8, 8}, {75, 50}}, false, 5);
		
		// Items
		ITEM_PICKAXE = new Item(0, "Pickaxe", 12, 2.0f, null);
		ITEM_DIRT = new Item(1, "Dirt", 3, 0.0f, DIRT);
		ITEM_GRASS = new Item(2, "Grass", 0, 0.0f, GRASS);
		ITEM_GRASS_SEEDS = new Item(3, "Grass Seeds", 4, 0.0f, null);
		ITEM_STONE = new Item(4, "Stone", 11, 0.0f, STONE);
		ITEM_IRONORE = new Item(5, "Iron ore", 6, 0.0f, IRONORE);
		ITEM_WOODLOG = new Item(6, "Wood Log", 10, 0.0f, null);
		ITEM_IRONCHUNK = new Item(7, "Iron Ore", 8, 0.0f, null);
		ITEM_SAPLING = new Item(8, "Sapling", 13, 0.0f, SAPLING);
		
		// Recipes
		RECIPE_GRASS_1 = new CraftingRecipe(new Item[] { ITEM_GRASS_SEEDS, null, null,
						 ITEM_DIRT, null, null,
						 null, null, null }, new ItemStack(ITEM_GRASS, 1));
		RECIPE_GRASS_2 = new CraftingRecipe(new Item[] { null, ITEM_GRASS_SEEDS, null,
				 null, ITEM_DIRT, null,
				 null, null, null }, new ItemStack(ITEM_GRASS, 1));
		RECIPE_GRASS_3 = new CraftingRecipe(new Item[] { null, null, ITEM_GRASS_SEEDS,
				 null, null, ITEM_DIRT,
				 null, null, null }, new ItemStack(ITEM_GRASS, 1));
		RECIPE_GRASS_4 = new CraftingRecipe(new Item[] { null, null, null,
				 ITEM_GRASS_SEEDS, null, null,
				 ITEM_DIRT, null, null }, new ItemStack(ITEM_GRASS, 1));
		RECIPE_GRASS_5 = new CraftingRecipe(new Item[] { null, null, null,
				 null, ITEM_GRASS_SEEDS, null,
				 null, ITEM_DIRT, null }, new ItemStack(ITEM_GRASS, 1));
		RECIPE_GRASS_6 = new CraftingRecipe(new Item[] { null, null, null,
				 null, null, ITEM_GRASS_SEEDS,
				 null, null, ITEM_DIRT }, new ItemStack(ITEM_GRASS, 1));
		
		
		items.add(ITEM_PICKAXE);
		items.add(ITEM_DIRT);
		items.add(ITEM_GRASS);
		items.add(ITEM_GRASS_SEEDS);
		items.add(ITEM_STONE);
		items.add(ITEM_IRONORE);
		items.add(ITEM_WOODLOG);
		items.add(ITEM_IRONCHUNK);
		items.add(ITEM_SAPLING);
		
		craftingRecipes.add(RECIPE_GRASS_1);
		craftingRecipes.add(RECIPE_GRASS_2);
		craftingRecipes.add(RECIPE_GRASS_3);
		craftingRecipes.add(RECIPE_GRASS_4);
		craftingRecipes.add(RECIPE_GRASS_5);
		craftingRecipes.add(RECIPE_GRASS_6);
	}
}
