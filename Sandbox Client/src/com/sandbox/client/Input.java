package com.sandbox.client;
import java.util.concurrent.ThreadLocalRandom;

import com.sandbox.client.item.CraftingRecipe;
import com.sandbox.client.item.ItemStack;
import com.sandbox.client.map.Tile;

public class Input {

	//  Key booleans
	public static boolean KEY_A_DOWN;
	public static boolean KEY_D_DOWN;
	public static boolean MOUSE_BUTTON1_DOWN;
	
	// Current mouse coordinates (updated in Game.update())
	public static int mouseX, mouseY;
	
	// Don't instantiate input--there can only be one
	private Input() { }
	
	public static void keyPressed(int key, char c){
		
		switch(c){
			case 'a':
				KEY_A_DOWN = true;
				break;
			case 'd':
				KEY_D_DOWN = true;
				break;
			case ' ':
				Game.myPlayer.jump();
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
				if(Game.myPlayer.cursorItem == null){
					Game.myPlayer.inventoryOpen = !Game.myPlayer.inventoryOpen;
					
					if(Game.myPlayer.cursorItem != null && Game.myPlayer.pickedUpItemOriginSlot != null){
						Game.myPlayer.pickedUpItemOriginSlot.itemStack = Game.myPlayer.cursorItem;
					}
				}
				break;
		} 
		
		// Escape
		if(key == 1){ 
			Game.quit();
		}
		
	}
	
	public static void keyReleased(int key, char c){
		if(c == 'a'){
			KEY_A_DOWN = false;
		} else if(c == 'd'){
			KEY_D_DOWN = false;
		}
	}
	
	public static void mouseClicked(int button, int x, int y, int clickCount){
		
	}
	
	public static void mousePressed(int button, int x, int y){
		if(button == 0){
			MOUSE_BUTTON1_DOWN = true;
			
			Tile t = null; // A temporary tile variable
			
			// Checks if the player has a block selected in the hotbar
			if(Game.myPlayer.selectedItem != null && Game.myPlayer.selectedItem.block != null && (t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null && t.type == Database.AIR && !Game.myPlayer.inventoryOpen){
				Game.currentMap.fixGrassBlocks();
				t.setTileType(Game.myPlayer.selectedItem.block); // Places the block
				Game.myPlayer.removeItem(Game.myPlayer.selectedItem, 1); // Removes 1 of the blocks from the inventory
			}
			
			// If the player is not holding a block or mining tool, the method specialTileInteraction in game checks if 
			// anything can be done with the tool in hand (Example: Grass Seeds)
			else if(Game.myPlayer.selectedItem != null && Game.myPlayer.selectedItem.miningPower == 0 && Game.myPlayer.selectedItem.block == null && (t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null){
				Game.currentMap.specialTileInteraction(t.x, t.y);
			}
			
			//  ** Inventory management logic involving the mouse **
			if(Game.myPlayer.inventoryOpen){ // first checks if the inventory is open
				
				// Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(x, y);
				
				// Makes sure the slot actually exists
				if(slot != null && slot.itemStack != null){
					
					// If you dont have an item picked up, the current slot's itemstack is now picked up
					if(Game.myPlayer.cursorItem == null && slot.itemStack.item != null){
						Game.myPlayer.cursorItem = new ItemStack(slot.itemStack.item, slot.itemStack.quantity);
						slot.itemStack = new ItemStack(null, 0);
						
						// If you picked something up from the crafting table output slot, the recipe ingredients are taken
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
					else if(Game.myPlayer.cursorItem != null && slot.isNotCraftingTableOutput){ // If you click on a slot with an item already picked up
						
						// If you click on a slot with the same item, the quantities are just added
						if(slot.itemStack.item == Game.myPlayer.cursorItem.item){
							slot.itemStack.quantity += Game.myPlayer.cursorItem.quantity;
							Game.myPlayer.cursorItem = null;
						} 
						
						// If you click on an empty slot, the itemstack picked up goes there
						else if(slot.itemStack.item == null){ 
							slot.itemStack = new ItemStack(Game.myPlayer.cursorItem.item, Game.myPlayer.cursorItem.quantity);
							Game.myPlayer.cursorItem = null;
						}
						
						// if you click on a slot with a different item, the stack you're holding is swapped with the one your mouse is over
						else if(slot.itemStack.item != null && slot.itemStack.item != Game.myPlayer.cursorItem.item){
							ItemStack tempItemStack = new ItemStack(Game.myPlayer.cursorItem.item, Game.myPlayer.cursorItem.quantity);
							Game.myPlayer.cursorItem = slot.itemStack;
							slot.itemStack = tempItemStack;
						}
						
					} else if(Game.myPlayer.cursorItem != null && slot.isNotCraftingTableOutput == false){
						if(Game.myPlayer.cursorItem.item == slot.itemStack.item){
							Game.myPlayer.cursorItem.quantity += slot.itemStack.quantity;
							
							// If you picked something up from the crafting table output slot, the recipe ingredients are taken
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
			
			// Inventory logic - For right clicking; basically, right clicking on a slot with an item selected
			// adds one to the slots count if it's empty or is the same item
			if(Game.myPlayer.inventoryOpen){
				// Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(x, y);
				
				if(slot != null && slot.itemStack != null && slot.isNotCraftingTableOutput){
					
					// If you are holding an item
					if(Game.myPlayer.cursorItem != null){
						
						// If there is an item in the slot your over, if its the same item the quantity is increased by 1
						if(slot.itemStack.item != null){
							if(slot.itemStack.item == Game.myPlayer.cursorItem.item){
								slot.itemStack.quantity++;
								Game.myPlayer.cursorItem.quantity--;
								
								if(Game.myPlayer.cursorItem.quantity <= 0){
									Game.myPlayer.cursorItem = null;
								}
								
							}
						}
						// If you right click over an empty slot, it adds one to the slot of your held item
						else if(slot.itemStack.item == null){
							slot.itemStack = new ItemStack(Game.myPlayer.cursorItem.item, 1);
							Game.myPlayer.cursorItem.quantity--;
							
							if(Game.myPlayer.cursorItem.quantity <= 0){
								Game.myPlayer.cursorItem = null;
							}
						}
						
						// Checks if you have entered a valid crafting recipe
						Game.myPlayer.craftingTableOutput.itemStack = CraftingRecipe.checkCraftingTable();
					} 
				}
			}
		}
		
	}
	
	public static void mouseReleased(int button, int x, int y) {
		// Left mouse button
		if(button == 0) {
			MOUSE_BUTTON1_DOWN = false;
		}
	}
	
	public static void mouseButtonHeld(int button, int x, int y){
		
		if(Game.currentMap == null)
			return;
		
		// Left click
		if(button == 0){
			Tile t = null;
			if(((t = Game.currentMap.getTileAtCoordinates(x - (int)Game.cameraOffsetX, y + (int)Game.cameraOffsetY)) != null) && Game.myPlayer.selectedItem != null && !Game.myPlayer.inventoryOpen){
					
				if(t.type != Database.BEDROCK){
					t.health -= Game.myPlayer.selectedItem.miningPower;
					if(t.health <= 0){
						
						// Gives the player the block's drops
						int rand = 0;
						for(int i = 0; i < t.type.itemDropIDs[0].length; i++){
							rand = ThreadLocalRandom.current().nextInt(1, 100); // Gets a random number
							
							if(rand < t.type.itemDropIDs[1][i]){ // Checks the drop chance to see if the player got it
								Game.myPlayer.addItem(t.type.itemDropIDs[0][i], 1); // Adds the item
							}
						}
						
						t.setTileType(Database.AIR); // Finally, removes the block
					}
				}
			}
		}
	}
}
