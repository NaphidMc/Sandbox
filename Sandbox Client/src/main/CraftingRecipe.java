package main;

public class CraftingRecipe {
	
	public Item[] input;
	public ItemStack output;
	
	public CraftingRecipe(Item[] input, ItemStack output) {
		this.output = new ItemStack(output.item, output.quantity);
		
		this.input = new Item[input.length];
		
		for(int i = 0; i < input.length; i++){
			this.input[i] = input[i];
		}
	}
	
	public static ItemStack checkCraftingTable(){
		Item[] craftingTableItems = new Item[Game.myPlayer.craftingTable.size()];
		
		for(int i = 0; i < Game.myPlayer.craftingTable.size(); i++){
			craftingTableItems[i] = Game.myPlayer.craftingTable.get(i).itemStack.item;
			
		}

		A : for(int i = 0; i < Database.craftingRecipes.size(); i++){
			
				for(int k = 0; k < 9; k++){
					if(craftingTableItems[k] == null && Database.craftingRecipes.get(i).input[k] == null){
						continue;
					} else if(craftingTableItems[k] == null && Database.craftingRecipes.get(i).input[k] != null){
						//System.out.println("Crafting table item is null while recipe item is not");
						continue A;
					} else if(craftingTableItems[k] != null && Database.craftingRecipes.get(i).input[k] == null){
						//System.out.println("Recipe item is null while crafting table item is not");
						continue A;
					}
					
					if(craftingTableItems[k].ID != Database.craftingRecipes.get(i).input[k].ID){
						//System.out.println("IDS don't match: " + craftingTableItems[k].ID + " " + Database.craftingRecipes.get(i).input[k].ID);
						continue A;
					}
				}
			
			return Database.craftingRecipes.get(i).output;
		}
		
		return new ItemStack(null, 0);
	}
}
