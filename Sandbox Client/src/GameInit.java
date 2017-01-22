
import java.awt.EventQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

public class GameInit extends ScalableGame {
	
	public GameInit(org.newdawn.slick.Game held, int normalWidth, int normalHeight, boolean maintainAspect) {
		super(held, normalWidth, normalHeight, maintainAspect);
	}
	
	public static Game g;
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				
				Database.Populate();
				
				AppGameContainer appgc = null;
				g = new Game("Sandbox");

				try {
					GameInit gi = new GameInit(g, 800, 600, true);
					appgc = new AppGameContainer(gi);
					appgc.setDisplayMode(appgc.getScreenWidth(), appgc.getScreenHeight(), true);
					appgc.setFullscreen(true);
					Game.appgc = appgc;
					appgc.start();
					
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/*@Override
	public void keyPressed(KeyEvent e) {
		
		else if(e.getKeyCode() == KeyEvent.VK_I && Game.myPlayer.pickedUpItem == null){
			Game.myPlayer.inventoryOpen = !Game.myPlayer.inventoryOpen;
			
			if(Game.myPlayer.pickedUpItem != null && Game.myPlayer.pickedUpItemOriginSlot != null){
				Game.myPlayer.pickedUpItemOriginSlot.itemStack = Game.myPlayer.pickedUpItem;
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_D) {
			g.KEY_D_DOWN = false;
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			g.KEY_A_DOWN = false;
		} else if(e.getKeyCode() == KeyEvent.VK_0){
			Game.myPlayer.setSelectedHotbarSlot(9);
		} else if(e.getKeyCode() == KeyEvent.VK_1){
			Game.myPlayer.setSelectedHotbarSlot(0);
		} else if(e.getKeyCode() == KeyEvent.VK_2){
			Game.myPlayer.setSelectedHotbarSlot(1);
		} else if(e.getKeyCode() == KeyEvent.VK_3){
			Game.myPlayer.setSelectedHotbarSlot(2);
		} else if(e.getKeyCode() == KeyEvent.VK_4){
			Game.myPlayer.setSelectedHotbarSlot(3);
		} else if(e.getKeyCode() == KeyEvent.VK_5){
			Game.myPlayer.setSelectedHotbarSlot(4);
		} else if(e.getKeyCode() == KeyEvent.VK_6){
			Game.myPlayer.setSelectedHotbarSlot(5);
		} else if(e.getKeyCode() == KeyEvent.VK_7){
			Game.myPlayer.setSelectedHotbarSlot(6);
		} else if(e.getKeyCode() == KeyEvent.VK_8){
			Game.myPlayer.setSelectedHotbarSlot(7);
		} else if(e.getKeyCode() == KeyEvent.VK_9){
			Game.myPlayer.setSelectedHotbarSlot(8);
		}
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public void mousePressed(MouseEvent e) {
		
		if(e.getButton() == MouseEvent.BUTTON1){
			g.MOUSE_BUTTON1_DOWN = true;
			
			//Inventory management logic involving the mouse
			if(Game.myPlayer.inventoryOpen){ //first checks if the inventory is open
				
				//Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(e.getX(), e.getY());
				
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
		if(e.getButton() == MouseEvent.BUTTON3){
			
			//Inventory logic - For right clicking; basically, right clicking on a slot with an item selected
			//adds one to the slots count if it's empty or is the same item
			if(Game.myPlayer.inventoryOpen){
				//Gets the inventory slot the mouse is over
				InventorySlot slot = InventorySlot.getInventorySlotAtPosition(e.getX(), e.getY());
				
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
	
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			g.MOUSE_BUTTON1_DOWN = false;
		}
	}*/
}
