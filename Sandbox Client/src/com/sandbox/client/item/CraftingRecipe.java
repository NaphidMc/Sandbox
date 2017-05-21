package com.sandbox.client.item;

import com.sandbox.client.Database;
import com.sandbox.client.Game;

public class CraftingRecipe {
	
	public Item[] input;
	public ItemStack output;
	
	public CraftingRecipe(Item[] input, ItemStack output) {
		this.output = new ItemStack(output.item, output.quantity);
		
		this.input = new Item[input.length];
		
		for(int i = 0; i < input.length; i++){
			this.input[i] = input[i];
		}
		
		Database.craftingRecipes.add(this);
	}
	
	public static ItemStack checkCraftingTable() {
		Item[] craftingTableItems = new Item[Game.myPlayer.craftingTable.size()];
		
		for(int i = 0; i < Game.myPlayer.craftingTable.size(); i++){
			craftingTableItems[i] = Game.myPlayer.craftingTable.get(i).itemStack.item;
		}

		A : for(int i = 0; i < Database.craftingRecipes.size(); i++){
				for(int k = 0; k < 9; k++) {
					if(craftingTableItems[k] ==  Database.craftingRecipes.get(i).input[k]){
						continue;
					} else {
						continue A;
					}
				}
			
			return Database.craftingRecipes.get(i).output;
		}
		
		return new ItemStack(null, 0);
	}
}
