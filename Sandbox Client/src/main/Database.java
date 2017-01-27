package main;
import java.util.ArrayList;

public class Database {
	
	public static ArrayList<Item> items = new ArrayList<Item>();
	public static ArrayList<CraftingRecipe> craftingRecipes = new ArrayList<CraftingRecipe>();
	
	//Blocks
	public static Block BLOCK_GRASS;
	public static Block BLOCK_DIRT;
	public static Block BLOCK_AIR;
	public static Block BLOCK_STONE;
	public static Block BLOCK_IRONORE;
	public static Block BLOCK_BEDROCK;
	public static Block BLOCK_SAPLING;
	public static Block BLOCK_WOOD;
	public static Block BLOCK_LEAVES;
	public static Block TEST;
	
	//Items
	public static Item ITEM_PICKAXE;
	public static Item ITEM_DIRT;
	public static Item ITEM_GRASS;
	public static Item ITEM_GRASS_SEEDS;
	public static Item ITEM_STONE;
	public static Item ITEM_IRONORE;
	public static Item ITEM_WOODLOG;
	public static Item ITEM_IRONCHUNK;
	public static Item ITEM_SAPLING;
	
	//Recipes
	public static CraftingRecipe RECIPE_GRASS_1;
	public static CraftingRecipe RECIPE_GRASS_2;
	public static CraftingRecipe RECIPE_GRASS_3;
	public static CraftingRecipe RECIPE_GRASS_4;
	public static CraftingRecipe RECIPE_GRASS_5;
	public static CraftingRecipe RECIPE_GRASS_6;
	
	public static void populate(){
		
		//Blocks
		BLOCK_GRASS = new Block("Grass", 0, new int[][] { {1, 3, 3, 3}, {100, 50, 50, 50} }, true, 20);
		BLOCK_DIRT = new Block("Dirt", 3, new int[][] { {1}, {100} }, true, 20);
		BLOCK_AIR = new Block("Air", 2, new int[][] {{-1}, {-1} }, true, 0);
		BLOCK_STONE = new Block("Stone", 11, new int[][] { {4}, {100} }, true, 50);
		BLOCK_IRONORE = new Block("Iron Ore", 6, new int[][] { {7, 7, 7, 7}, {100, 50, 50, 50} }, true, 80);
		BLOCK_BEDROCK = new Block("Bedrock", 1, new int[][] {{-1},{-1}}, true, 0);
		BLOCK_SAPLING = new Block("Sapling", 13, new int[][] {{8}, {100}}, false, 5);
		BLOCK_WOOD = new Block("Wood", 14, new int[][] { {6}, {100}}, false, 30);
		BLOCK_LEAVES = new Block("Leaves", 7, new int[][] { {8, 8}, {75, 50}}, false, 5);
		
		//Items
		ITEM_PICKAXE = new Item(0, "Pickaxe", 12, 2.0f, null);
		ITEM_DIRT = new Item(1, "Dirt", 3, 0.0f, BLOCK_DIRT);
		ITEM_GRASS = new Item(2, "Grass", 0, 0.0f, BLOCK_GRASS);
		ITEM_GRASS_SEEDS = new Item(3, "Grass Seeds", 4, 0.0f, null);
		ITEM_STONE = new Item(4, "Stone", 11, 0.0f, BLOCK_STONE);
		ITEM_IRONORE = new Item(5, "Iron ore", 6, 0.0f, BLOCK_IRONORE);
		ITEM_WOODLOG = new Item(6, "Wood Log", 10, 0.0f, null);
		ITEM_IRONCHUNK = new Item(7, "Iron Ore", 8, 0.0f, null);
		ITEM_SAPLING = new Item(8, "Sapling", 13, 0.0f, BLOCK_SAPLING);
		
		//Recipes
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
