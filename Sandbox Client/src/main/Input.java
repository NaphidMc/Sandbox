package main;
import java.util.concurrent.ThreadLocalRandom;

public class Input {
	
	public void keyPressed(int key, char c){
		
		switch(c){
			case 'a':
				Game.KEY_A_DOWN = true;
				break;
			case 'd':
				Game.KEY_D_DOWN = true;
				break;
			case ' ':
				Game.myPlayer.Jump();
				break;
			case '1':
				Game.myPlayer.setSelectedHotbarSlot(0);
				break;
			case '2':
				Game.myPlayer.setSelectedHotbarSlot(1);
				break;
			case '3':
				Game.myPlayer.setSelectedHotbarSlot(2);				
				break;
			case '4':
				Game.myPlayer.setSelectedHotbarSlot(3);				
				break;
			case '5':
				Game.myPlayer.setSelectedHotbarSlot(4);				
				break;
			case '6':
				Game.myPlayer.setSelectedHotbarSlot(5);				
				break;
			case '7':
				Game.myPlayer.setSelectedHotbarSlot(6);				
				break;
			case '8':
				Game.myPlayer.setSelectedHotbarSlot(7);				
				break;			
			case '9':
				Game.myPlayer.setSelectedHotbarSlot(8);				
				break;
			case 'i':
			case 'I':
				if(Game.myPlayer.pickedUpItem == null){
					Game.myPlayer.inventoryOpen = !Game.myPlayer.inventoryOpen;
					
					if(Game.myPlayer.pickedUpItem != null && Game.myPlayer.pickedUpItemOriginSlot != null){
						Game.myPlayer.pickedUpItemOriginSlot.itemStack = Game.myPlayer.pickedUpItem;
					}
				}
				break;
		} 
		
		if(key == 1){ //Esc
			System.exit(0);
		}
		
	}
	
	public void keyReleased(int key, char c){
		if(c == 'a'){
			Game.KEY_A_DOWN = false;
		} else if(c == 'd'){
			Game.KEY_D_DOWN = false;
		}
	}
	
	public void mouseClicked(int button, int x, int y, int clickCount){
		
	}
	
	public void mousePressed(int button, int x, int y){
		if(button == 0){
			Game.MOUSE_BUTTON1_DOWN = true;
			
			Tile t = null; //A temporary tile variable
			
			//Checks if the player has a block selected in the hotbar
			if(Game.myPlayer.selectedItem.block != null && (t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null && t.block == Database.BLOCK_AIR && !Game.myPlayer.inventoryOpen){
				
				t.setBlock(Game.myPlayer.selectedItem.block); //Places the block
				Game.myPlayer.removeItem(Game.myPlayer.selectedItem, 1); //Removes 1 of the blocks from the inventory
			}
			
			//If the player is not holding a block or mining tool, the method specialTileInteraction in game checks if 
			//anything can be done with the tool in hand (Example: Grass Seeds)
			else if(Game.myPlayer.selectedItem.MiningPower == 0 && Game.myPlayer.selectedItem.block == null && (t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null){
				Game.currentMap.specialTileInteraction(t.x, t.y);
			}
			
			// ** Inventory management logic involving the mouse **
			if(Game.myPlayer.inventoryOpen){ //first checks if the inventory is open
				
				//Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(x, y);
				
				//Makes sure the slot actually exists
				if(slot != null && slot.itemStack != null){
					
					//If you dont have an item picked up, the current slot's itemstack is now picked up
					if(Game.myPlayer.pickedUpItem == null && slot.itemStack.item != null){
						Game.myPlayer.pickedUpItem = new ItemStack(slot.itemStack.item, slot.itemStack.quantity);
						slot.itemStack = new ItemStack(null, 0);
						
						//If you picked something up from the crafting table output slot, the recipe ingredients are taken
						if(slot.isNotCraftingTableOutput == false){
							for(int i = 0; i < Game.myPlayer.craftingTable.size(); i++){
								if(Game.myPlayer.craftingTable.get(i).itemStack.item != null){
									Game.myPlayer.craftingTable.get(i).itemStack.quantity--;
									
									if(Game.myPlayer.craftingTable.get(i).itemStack.quantity <= 0){
										Game.myPlayer.craftingTable.get(i).itemStack = new ItemStack(null, 0);
									}
								}
							}
						}
					}
					else if(Game.myPlayer.pickedUpItem != null && slot.isNotCraftingTableOutput){ //If you click on a slot with an item already picked up
						
						//If you click on a slot with the same item, the quantities are just added
						if(slot.itemStack.item == Game.myPlayer.pickedUpItem.item){
							slot.itemStack.quantity += Game.myPlayer.pickedUpItem.quantity;
							Game.myPlayer.pickedUpItem = null;
						} 
						
						//If you click on an empty slot, the itemstack picked up goes there
						else if(slot.itemStack.item == null){ 
							slot.itemStack = new ItemStack(Game.myPlayer.pickedUpItem.item, Game.myPlayer.pickedUpItem.quantity);
							Game.myPlayer.pickedUpItem = null;
						}
						
						//if you click on a slot with a different item, the stack you're holding is swapped with the one your mouse is over
						else if(slot.itemStack.item != null && slot.itemStack.item != Game.myPlayer.pickedUpItem.item){
							ItemStack tempItemStack = new ItemStack(Game.myPlayer.pickedUpItem.item, Game.myPlayer.pickedUpItem.quantity);
							Game.myPlayer.pickedUpItem = slot.itemStack;
							slot.itemStack = tempItemStack;
						}
						
					} else if(Game.myPlayer.pickedUpItem != null && slot.isNotCraftingTableOutput == false){
						if(Game.myPlayer.pickedUpItem.item == slot.itemStack.item){
							Game.myPlayer.pickedUpItem.quantity += slot.itemStack.quantity;
							
							//If you picked something up from the crafting table output slot, the recipe ingredients are taken
							if(slot.isNotCraftingTableOutput == false){
								for(int i = 0; i < Game.myPlayer.craftingTable.size(); i++){
									if(Game.myPlayer.craftingTable.get(i).itemStack.item != null){
										Game.myPlayer.craftingTable.get(i).itemStack.quantity--;
										
										if(Game.myPlayer.craftingTable.get(i).itemStack.quantity <= 0){
											Game.myPlayer.craftingTable.get(i).itemStack = new ItemStack(null, 0);
										}
									}
								}
							}
						}
					}
					
					Game.myPlayer.craftingTableOutput.itemStack = CraftingRecipe.checkCraftingTable();
				}
			}
		}
		
		if(button == 1){
			
			//Inventory logic - For right clicking; basically, right clicking on a slot with an item selected
			//adds one to the slots count if it's empty or is the same item
			if(Game.myPlayer.inventoryOpen){
				//Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(x, y);
				
				if(slot != null && slot.itemStack != null && slot.isNotCraftingTableOutput){
					
					//If you are holding an item
					if(Game.myPlayer.pickedUpItem != null){
						
						//If there is an item in the slot your over, if its the same item the quantity is increased by 1
						if(slot.itemStack.item != null){
							if(slot.itemStack.item == Game.myPlayer.pickedUpItem.item){
								slot.itemStack.quantity++;
								Game.myPlayer.pickedUpItem.quantity--;
								
								if(Game.myPlayer.pickedUpItem.quantity <= 0){
									Game.myPlayer.pickedUpItem = null;
								}
								
							}
						}
						//If you right click over an empty slot, it adds one to the slot of your held item
						else if(slot.itemStack.item == null){
							slot.itemStack = new ItemStack(Game.myPlayer.pickedUpItem.item, 1);
							Game.myPlayer.pickedUpItem.quantity--;
							
							if(Game.myPlayer.pickedUpItem.quantity <= 0){
								Game.myPlayer.pickedUpItem = null;
							}
						}
						
						//Checks if you have entered a valid crafting recipe
						Game.myPlayer.craftingTableOutput.itemStack = CraftingRecipe.checkCraftingTable();
					} 
				}
			}
		}
		
	}
	
	public void mouseButtonHeld(int button, int x, int y){
		
		if(Game.currentMap == null)
			return;
		
		//Left click
		if(button == 0){
			Tile t = null;
			if(((t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null) && Game.myPlayer.selectedItem != null && !Game.myPlayer.inventoryOpen){
					
				if(t.block != Database.BLOCK_BEDROCK){
					t.health -= Game.myPlayer.selectedItem.MiningPower;
					if(t.health <= 0){
						
						//Gives the player the block's drops
						int rand = 0;
						for(int i = 0; i < t.block.itemDropIDs[0].length; i++){
							rand = ThreadLocalRandom.current().nextInt(1, 100); //Gets a random number
							
							if(rand < t.block.itemDropIDs[1][i]){ //Checks the drop chance to see if the player got it
								Game.myPlayer.addItem(t.block.itemDropIDs[0][i], 1); //Adds the item
							}
						}
						
						t.setBlock(Database.BLOCK_AIR); //Finally, removes the block
					}
				}
			}
		}
	}
}
