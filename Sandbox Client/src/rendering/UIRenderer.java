package rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.sandbox.client.Game;
import com.sandbox.client.Input;
import com.sandbox.client.network.PlayerPacket;

/**
 * Renders all user interface items
 * @author Nathan
 *
 */
public class UIRenderer {
	
	// **UI Positioning numbers** \\
	public static int inventorySlotSize = 68;
	
	private static int hotbarPositionX = 25, hotbarPositionY = 10;
	private static int inventoryPositionX = 10, inventoryPositionY = 250;
	private static int craftingUIPositionX = 575, craftingUIPositionY = 250;
	private static int itemIconSize = inventorySlotSize * 3/4;
	
	public static void renderUI(Graphics g) {
		renderInventory(g);
		renderHotbar(g);
		renderHealthBar(g);
		renderTextualInfo(g);
	}
	
	/**
	 * 	Draws the hotbar slots and highlights the selected slot
	 * @param g - Graphics object
	 */
	public static void renderHotbar(Graphics g) {
			
		// Loops through all hotbar slots
		for(int i = 0; i < Game.myPlayer.hotbar.size(); i++){
			// Sets hotbar slot positions
			Game.myPlayer.hotbar.get(i).x = hotbarPositionX + 80 * i;
			Game.myPlayer.hotbar.get(i).y = hotbarPositionY;
			
			if(i == Game.myPlayer.selectedHotbarSlot){
				g.setColor(new Color(1f, 1f, 1f, .35f));
				g.fillRect(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);
			}
			else{
				g.setColor(new Color(0f, 0f, 0f, .35f));
				g.fillRect(Game.myPlayer.hotbar.get(i).x, hotbarPositionY, inventorySlotSize + 10, inventorySlotSize + 10);
			}
		}
		
		// Draws item icons in the hotbar
		Game.spritesheet.startUse();
		for(int i = 0; i < Game.myPlayer.hotbar.size(); i++){
			if(Game.myPlayer.hotbar.get(i).itemStack.item != null){
				Game.spritesheet.renderInUse(hotbarPositionX + 80 * i + 5, hotbarPositionY + 5, Game.myPlayer.hotbar.get(i).itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.hotbar.get(i).itemStack.item.icon/Game.SPRITESHEET_WIDTH);
			}
		}
		Game.spritesheet.endUse();
		
		// Draws item quantities in the hotbar
		for(int i = 0; i < Game.myPlayer.hotbar.size(); i++){
			if(Game.myPlayer.hotbar.get(i).itemStack.item != null){
				g.setColor(Color.white);
				g.drawString("" + Game.myPlayer.hotbar.get(i).itemStack.quantity, hotbarPositionX + 80 * i + 60, hotbarPositionY + (int)(1.15f*itemIconSize));
			}
		}
		
	}
	
	/**
	 * Renders the player's inventory + crafting area (If it's open)
	 * @param g - Graphics object
	 */
	public static void renderInventory(Graphics g) {
		if(Game.myPlayer.inventoryOpen == false)
			return;
		
		// Draws a semi-transparent gray overlay to indicate the inventory is open
		g.setColor(new Color(0, 0, 0, .35f));
		g.fillRect(0, 0, 800, 600);
		
		Game.spritesheet.startUse();
		if(Game.myPlayer.inventoryOpen){
			
			int currentInventorySlot = 0;	// The current slot index
			for(int i = 0; i < Game.myPlayer.inventoryRows; i++){
				for(int k = 0; k < Game.myPlayer.inventoryColumns; k++){
					Game.spritesheet.renderInUse(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, 3, 3);
					
					Game.myPlayer.inventory.get(currentInventorySlot).x = inventoryPositionX + inventorySlotSize * k;
					Game.myPlayer.inventory.get(currentInventorySlot).y = inventoryPositionY + inventorySlotSize * i;
					
					if(Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item != null){
						// Displays the item's icon in the inventory
						Game.spritesheet.renderInUse(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon/Game.SPRITESHEET_WIDTH);     
						
					}
					currentInventorySlot++;
				}
			}
			
			// **Crafting table**\\
			
			int x = 0, y = 0; // x, y position in slots (not pixels!!) of the current slot
			for(int i = 0; i < 9; i++){
					
					Game.myPlayer.craftingTable.get(i).x = craftingUIPositionX + x * inventorySlotSize;
					Game.myPlayer.craftingTable.get(i).y = craftingUIPositionY + y * inventorySlotSize;
					
					Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(i).x, Game.myPlayer.craftingTable.get(i).y, 3, 3);
					
					// Draws the items in the crafting table
					if(Game.myPlayer.craftingTable.get(i).itemStack.item != null){
						Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(i).x, Game.myPlayer.craftingTable.get(i).y, Game.myPlayer.craftingTable.get(i).itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.craftingTable.get(i).itemStack.item.icon/Game.SPRITESHEET_WIDTH); 
					}
					
					x++;
					if(x % 3 == 0){
						y++;
						x = 0;
					}
			}
			
			Game.myPlayer.craftingTableOutput.x = Game.myPlayer.craftingTable.get(4).x; 	// The output's x is the same as the middle slot's
			Game.myPlayer.craftingTableOutput.y = craftingUIPositionY + 3 * inventorySlotSize;
			
			// Draws the output square
			Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * inventorySlotSize, 3, 3);
			
			// Draws the output item
			if(Game.myPlayer.craftingTableOutput.itemStack.item != null){
				Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * inventorySlotSize, Game.myPlayer.craftingTableOutput.itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.craftingTableOutput.itemStack.item.icon/Game.SPRITESHEET_WIDTH);
			}
			
			// Draws the item that the player picked up with the mouse
			if(Game.myPlayer.pickedUpItem != null){
				Game.spritesheet.renderInUse(Input.mouseX, Input.mouseY, Game.myPlayer.pickedUpItem.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.pickedUpItem.item.icon/Game.SPRITESHEET_WIDTH);
			}

		}
		
		Game.spritesheet.endUse();
		
		if(Game.myPlayer.inventoryOpen){
			int currentInventorySlot = 0;	// Holds the index of the current inventory slot
			for(int i = 0; i < Game.myPlayer.inventoryRows; i++){
				for(int k = 0; k < Game.myPlayer.inventoryColumns; k++){
					// displays the item's quantity in the inventory
					g.drawString("" + Game.myPlayer.inventory.get(currentInventorySlot).itemStack.quantity, inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i);
					currentInventorySlot++;
				}
			}
			
			// Draws the quantity string for crafting table output
			if(Game.myPlayer.craftingTableOutput.itemStack.item != null){
				g.drawString("" + Game.myPlayer.craftingTableOutput.itemStack.quantity, craftingUIPositionX + 4 * inventorySlotSize, craftingUIPositionY + inventorySlotSize);
			}
			
			// Draws the quantity string for picked up items
			if(Game.myPlayer.pickedUpItem != null){
				g.drawString("" + Game.myPlayer.pickedUpItem.quantity, Input.mouseX, Input.mouseY);
			}
			
			// Loops through and draws quantity strings for the crafting table
			for(int i = 0; i < 9; i++){
				if(Game.myPlayer.craftingTable.get(i).itemStack.item != null)
					g.drawString("" + Game.myPlayer.craftingTable.get(i).itemStack.quantity, Game.myPlayer.craftingTable.get(i).x, Game.myPlayer.craftingTable.get(i).y);
			}
		}
	}
	
	/**
	 * Renders the player's health bar
	 * @param g - Graphics object
	 */
	public static void renderHealthBar(Graphics g) {
		// Background
		g.setColor(Color.gray);
		g.fillRect(600, 550, 100,15);
		
		// Foreground
		g.setColor(Color.magenta);		
		g.fillRect(600, 550, 100 * (float)(Game.myPlayer.getHealth()/Game.myPlayer.getMaxHealth()), 15);
		
	}
	
	/**
	 * Renders textual info such as currently selected item, respawn counter, etc...
	 * @param g - Graphics object
	 */
	public static void renderTextualInfo(Graphics g) {
		// String to show currently selected item
		String tempItemName = "none";
		if(Game.myPlayer.selectedItem != null)
			tempItemName = Game.myPlayer.selectedItem.name;
		g.drawString("Selected item: " + tempItemName, 25, 100);
		
		// Draws other player's name tags. For now name tags are just player + playerID
		for(java.util.Map.Entry<Integer, PlayerPacket> entry : Game.players.entrySet()){
			PlayerPacket pp = entry.getValue();
			g.drawString("player" + pp.id, pp.x + Game.cameraOffsetX, pp.y - 15 - Game.cameraOffsetY);
		}
		
		if(Game.myPlayer.respawnTimer > 0){
			g.setColor(Color.red);
			g.drawString("You have died. You will respawn in " + ((int)Game.myPlayer.respawnTimer) +"  seconds.", 100, 150);
		}
	}
}
