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
	public static TileType COALORE;
	public static TileType GOLDORE;
	
	// Items
	public static Item ITEM_STONEPICK;
	public static Item ITEM_DIRT;
	public static Item ITEM_GRASS;
	public static Item ITEM_GRASS_SEEDS;
	public static Item ITEM_STONE;
	public static Item ITEM_STONECHUNK;
	public static Item ITEM_IRONORE;
	public static Item ITEM_WOODLOG;
	public static Item ITEM_IRONCHUNK;
	public static Item ITEM_SAPLING;
	public static Item ITEM_COAL;
	public static Item ITEM_IRONBAR;
	public static Item ITEM_GOLDORE;
	public static Item ITEM_GOLDCHUNK;
	public static Item ITEM_GOLDBAR;
	public static Item ITEM_PLANTFIBER;
	public static Item ITEM_STRAP;
	public static Item ITEM_MULTITOOL;
	public static Item ITEM_IRONPICK;
	
	// Recipes
	public static CraftingRecipe RECIPE_GRASS_1;
	public static CraftingRecipe RECIPE_GRASS_2;
	public static CraftingRecipe RECIPE_GRASS_3;
	public static CraftingRecipe RECIPE_GRASS_4;
	public static CraftingRecipe RECIPE_GRASS_5;
	public static CraftingRecipe RECIPE_GRASS_6;
	
	public static CraftingRecipe RECIPE_STRAP_1;
	public static CraftingRecipe RECIPE_STRAP_2;
	public static CraftingRecipe RECIPE_STRAP_3;
	public static CraftingRecipe RECIPE_STRAP_4;
	
	public static CraftingRecipe RECIPE_IRONBAR;
	public static CraftingRecipe RECIPE_GOLDBAR;
	
	public static CraftingRecipe RECIPE_STONEPICK;
	public static CraftingRecipe RECIPE_IRONPICK;
	
	public static CraftingRecipe RECIPE_STONE_1;
	public static CraftingRecipe RECIPE_STONE_2;
	public static CraftingRecipe RECIPE_STONE_3;
	public static CraftingRecipe RECIPE_STONE_4;
	
	public static void populate() {
		
		// Blocks
		GRASS = new TileType("Grass", 0, new int[][] { {1, 3, 3, 3}, {100, 50, 50, 50} }, true, 20);
		DIRT = new TileType("Dirt", 3, new int[][] { {1}, {100} }, true, 20);
		AIR = new TileType("Air", 2, new int[][] {{-1}, {-1} }, false, 0);
		STONE = new TileType("Stone", 11, new int[][] { {4, 4, 4, 4}, {100, 75, 50, 25} }, true, 50);
		IRONORE = new TileType("Iron Ore", 6, new int[][] { {7, 7, 7, 7}, {100, 50, 50, 50} }, true, 80);
		BEDROCK = new TileType("Bedrock", 1, new int[][] {{-1},{-1}}, true, 0);
		SAPLING = new TileType("Sapling", 13, new int[][] {{8}, {100}}, false, 5);
		WOOD = new TileType("Wood", 14, new int[][] { {6}, {100}}, false, 30);
		LEAVES = new TileType("Leaves", 7, new int[][] { {8, 14, 14, 14, 14, 14}, {75, 100, 50, 50, 50, 25}}, false, 5);
		COALORE = new TileType("Coal", 18, new int[][] { {9, 9}, {100, 50}}, true, 50);
		GOLDORE = new TileType("Gold", 25, new int[][] { {12, 12}, {100, 50} }, true, 85);
		
		// Items
		ITEM_STONEPICK = new Item(0, "Stone Pick", 12, 2.0f, null);
		ITEM_DIRT = new Item(1, "Dirt", 3, 0.0f, DIRT);
		ITEM_GRASS = new Item(2, "Grass", 0, 0.0f, GRASS);
		ITEM_GRASS_SEEDS = new Item(3, "Grass Seeds", 4, 0.0f, null);
		ITEM_STONECHUNK = new Item(4, "Stone", 17, 0.0f, null);
		ITEM_IRONORE = new Item(5, "Iron Ore", 6, 0.0f, IRONORE);
		ITEM_WOODLOG = new Item(6, "Wood Log", 10, 0.0f, null);
		ITEM_IRONCHUNK = new Item(7, "Iron Ore", 8, 0.0f, null);
		ITEM_SAPLING = new Item(8, "Sapling", 13, 0.0f, SAPLING);
		ITEM_COAL = new Item(9, "Coal", 16, 0.0f, null);
		ITEM_IRONBAR = new Item(10, "Iron Bar", 19, 0.0f, null);
		ITEM_GOLDORE = new Item(11, "Gold Ore", 25, 0.0f, GOLDORE);
		ITEM_GOLDCHUNK = new Item(12, "Gold Ore", 26, 0.0f, null);
		ITEM_GOLDBAR = new Item(13, "Gold Bar", 27, 0.0f, null);
		ITEM_PLANTFIBER = new Item(14, "Plant Fiber", 28, 0.0f, null);
		ITEM_STRAP = new Item(15, "Strap", 29, 0.0f, null);
		ITEM_STONE = new Item(16, "Stone", 11, 0.0f, STONE);
		ITEM_MULTITOOL = new Item(17, "Multitool", 30, 1.0f, null);
		ITEM_IRONPICK = new Item(18, "Iron Pick", 32, 4.0f, null);
		
		// Recipes
		//Grass variants
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
		
		// Tools
		RECIPE_STONEPICK = new CraftingRecipe(new Item[] {	ITEM_STONECHUNK, ITEM_STONECHUNK, ITEM_STONECHUNK,
				ITEM_STRAP, ITEM_WOODLOG, ITEM_STRAP,
				null,  null, null}, new ItemStack(ITEM_STONEPICK, 1));
		RECIPE_IRONPICK = new CraftingRecipe(new Item[] { ITEM_IRONBAR, ITEM_IRONBAR, ITEM_IRONBAR,
				ITEM_STRAP, ITEM_WOODLOG, ITEM_STRAP,
				null, null, null}, new ItemStack(ITEM_IRONPICK, 1));
		
		// Bars
		RECIPE_IRONBAR = new CraftingRecipe(new Item[] {null, ITEM_IRONCHUNK, null, 
				ITEM_IRONCHUNK, ITEM_COAL, ITEM_IRONCHUNK,
				null, ITEM_IRONCHUNK, null}, new ItemStack(ITEM_IRONBAR, 2));
		
		RECIPE_GOLDBAR = new CraftingRecipe(new Item[] {null, ITEM_GOLDCHUNK, null,
				ITEM_GOLDCHUNK, ITEM_COAL, ITEM_GOLDCHUNK,
				null, ITEM_GOLDCHUNK, null}, new ItemStack(ITEM_GOLDBAR, 2));
		
		// Strap recipe variants
		RECIPE_STRAP_1 = new CraftingRecipe(new Item[] {ITEM_PLANTFIBER, ITEM_PLANTFIBER, null, 
				ITEM_PLANTFIBER, ITEM_PLANTFIBER, null,
				null, null, null }, new ItemStack(ITEM_STRAP, 2));
		RECIPE_STRAP_2 = new CraftingRecipe(new Item[] {null, ITEM_PLANTFIBER, ITEM_PLANTFIBER, 
				null, ITEM_PLANTFIBER, ITEM_PLANTFIBER,
				null, null, null }, new ItemStack(ITEM_STRAP, 2));
		RECIPE_STRAP_3 = new CraftingRecipe(new Item[] {null, null, null, 
				null, ITEM_PLANTFIBER, ITEM_PLANTFIBER,
				null, ITEM_PLANTFIBER, ITEM_PLANTFIBER }, new ItemStack(ITEM_STRAP, 2));
		RECIPE_STRAP_4 = new CraftingRecipe(new Item[] {null, null, null, 
				ITEM_PLANTFIBER, ITEM_PLANTFIBER, null,
				ITEM_PLANTFIBER, ITEM_PLANTFIBER, null }, new ItemStack(ITEM_STRAP, 2));
		
		// Stone recipe variants
		RECIPE_STONE_1 = new CraftingRecipe(new Item[] {ITEM_STONECHUNK, ITEM_STONECHUNK, null, 
				ITEM_STONECHUNK, ITEM_STONECHUNK, null,
				null, null, null }, new ItemStack(ITEM_STONE, 1));
		RECIPE_STONE_2 = new CraftingRecipe(new Item[] {null, ITEM_STONECHUNK, ITEM_STONECHUNK, 
				null, ITEM_STONECHUNK, ITEM_STONECHUNK,
				null, null, null }, new ItemStack(ITEM_STONE, 1));
		RECIPE_STONE_3 = new CraftingRecipe(new Item[] {null, null, null, 
				null, ITEM_STONECHUNK, ITEM_STONECHUNK,
				null, ITEM_STONECHUNK, ITEM_STONECHUNK }, new ItemStack(ITEM_STONE, 1));
		RECIPE_STONE_4 = new CraftingRecipe(new Item[] {null, null, null, 
				ITEM_STONECHUNK, ITEM_STONECHUNK, null,
				ITEM_STONECHUNK, ITEM_STONECHUNK, null }, new ItemStack(ITEM_STONE, 1));
	}
}
