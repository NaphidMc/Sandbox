package com.sandbox.client.rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.sandbox.client.Database;
import com.sandbox.client.Game;
import com.sandbox.client.Input;
import com.sandbox.client.map.Tile;
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
	private static int inventoryPositionX = 10, inventoryPositionY = 200;
	private static int craftingUIPositionX = 575, craftingUIPositionY = 250;
	private static int itemIconSize = inventorySlotSize * 3/4;
	
	public static void renderUI(Graphics g) {
		if(Game.myPlayer.inventoryOpen) {
			// Draws a semi-transparent gray overlay to indicate the inventory is open
			g.setColor(new Color(.388f, .333f, .270f, 1f));
			g.fillRect(0, 0, 800, 600);
		}
		
		renderHotbar(g);
		renderInventory(g);
		if(!Game.myPlayer.inventoryOpen) {
			renderHealthBar(g);
			renderTextualInfo(g);
		}
	}
	
	/**
	 * 	Draws the hotbar slots and highlights the selected slot
	 * @param g - Graphics object
	 */
	public static void renderHotbar(Graphics g) {
			
		// Loops through all hotbar slots
		if(!Game.myPlayer.inventoryOpen) {
			for(int i = 0; i < Game.myPlayer.hotbar.size(); i++){
				// Sets hotbar slot positions
				Game.myPlayer.hotbar.get(i).x = hotbarPositionX + 80 * i;
				Game.myPlayer.hotbar.get(i).y = hotbarPositionY;
				
				if(i == Game.myPlayer.selectedHotbarSlot) {
					g.setColor(new Color(1f, 1f, 1f, .35f));
					g.fillRect(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);
				}
				else{
					g.setColor(new Color(0f, 0f, 0f, .35f));
					g.fillRect(Game.myPlayer.hotbar.get(i).x, hotbarPositionY, inventorySlotSize + 10, inventorySlotSize + 10);
				}
			}
		}
		// Hotbar looks different when the inventory is open
		else {
			for(int i = 0; i < Game.myPlayer.hotbar.size(); i++) {
				// Sets hotbar slot positions
				Game.myPlayer.hotbar.get(i).x = hotbarPositionX + 80 * i;
				Game.myPlayer.hotbar.get(i).y = hotbarPositionY;
				
				java.awt.Rectangle rect = new java.awt.Rectangle(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);
				
				if(rect.contains(Input.mouseX, Input.mouseY) && Input.MOUSE_BUTTON1_DOWN)
					Game.spritesheet.getSubImage(15, 3).draw(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);	
				else if(rect.contains(Input.mouseX, Input.mouseY) && !Input.MOUSE_BUTTON1_DOWN)
					Game.spritesheet.getSubImage(15, 1).draw(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);	
				else
					Game.spritesheet.getSubImage(15, 2).draw(Game.myPlayer.hotbar.get(i).x, Game.myPlayer.hotbar.get(i).y, inventorySlotSize + 10, inventorySlotSize + 10);				
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
		
		Game.spritesheet.startUse();
		if(Game.myPlayer.inventoryOpen){
			
			int currentInventorySlot = 0;	// The current slot index
			for(int i = 0; i < Game.myPlayer.inventoryRows; i++){
				for(int k = 0; k < Game.myPlayer.inventoryColumns; k++) {
					java.awt.Rectangle rect = new java.awt.Rectangle(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, Tile.tileSize, Tile.tileSize);
					
					// Draws the slot, and highlights it if the mouse is over it
					if(rect.contains(Input.mouseX, Input.mouseY) && Input.MOUSE_BUTTON1_DOWN)
						Game.spritesheet.renderInUse(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, 15, 3);	
					else if(rect.contains(Input.mouseX, Input.mouseY) && !Input.MOUSE_BUTTON1_DOWN)
						Game.spritesheet.renderInUse(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, 15, 1);
					else
						Game.spritesheet.renderInUse(inventoryPositionX + inventorySlotSize * k, inventoryPositionY + inventorySlotSize * i, 15, 2);
					
					Game.myPlayer.inventory.get(currentInventorySlot).x = inventoryPositionX + inventorySlotSize * k;
					Game.myPlayer.inventory.get(currentInventorySlot).y = inventoryPositionY + inventorySlotSize * i;
					
					if(Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item != null){
						// Displays the item's icon in the inventory
						Game.spritesheet.endUse();
						Game.spritesheet.getSubImage(Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon/Game.SPRITESHEET_WIDTH).draw(inventoryPositionX + inventorySlotSize * k + 4, inventoryPositionY + inventorySlotSize * i + 4, Tile.tileSize - 8, Tile.tileSize - 8);     
						Game.spritesheet.startUse();
					}
					currentInventorySlot++;
				}
			}
			
			// **Crafting table**\\
			
			int x = 0, y = 0; // x, y position in slots (not pixels!!) of the current slot
			for(int i = 0; i < 9; i++){
				
					Game.myPlayer.craftingTable.get(i).x = craftingUIPositionX + x * inventorySlotSize;
					Game.myPlayer.craftingTable.get(i).y = craftingUIPositionY + y * inventorySlotSize;
					
					java.awt.Rectangle rect = new java.awt.Rectangle(Game.myPlayer.craftingTable.get(i).x, Game.myPlayer.craftingTable.get(i).y, Tile.tileSize, Tile.tileSize);
					
					// Draws the slot, and highlights it if the mouse is over it
					if(rect.contains(Input.mouseX, Input.mouseY) && Input.MOUSE_BUTTON1_DOWN)
						Game.spritesheet.renderInUse(rect.x, rect.y, 15, 3);	
					else if(rect.contains(Input.mouseX, Input.mouseY) && !Input.MOUSE_BUTTON1_DOWN)
						Game.spritesheet.renderInUse(rect.x, rect.y, 15, 1);
					else
						Game.spritesheet.renderInUse(rect.x, rect.y, 15, 2);
					
					// Draws the items in the crafting table
					if(Game.myPlayer.craftingTable.get(i).itemStack.item != null){
						Game.spritesheet.endUse();
						Game.spritesheet.getSubImage(Game.myPlayer.craftingTable.get(i).itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.craftingTable.get(i).itemStack.item.icon/Game.SPRITESHEET_WIDTH).draw(rect.x + 4, rect.y + 4, Tile.tileSize - 8, Tile.tileSize - 8);     
						Game.spritesheet.startUse();
					}
					
					x++;
					if(x % 3 == 0){
						y++;
						x = 0;
					}
			}
			
			Game.myPlayer.craftingTableOutput.x = Game.myPlayer.craftingTable.get(4).x; // The output's x is the same as the middle slot's
			Game.myPlayer.craftingTableOutput.y = craftingUIPositionY + 3 * inventorySlotSize;
			
			// Draws the output square
			Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * inventorySlotSize, 15, 2);
			
			// Draws the output item
			if(Game.myPlayer.craftingTableOutput.itemStack.item != null){
				Game.spritesheet.renderInUse(Game.myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * inventorySlotSize, Game.myPlayer.craftingTableOutput.itemStack.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.craftingTableOutput.itemStack.item.icon/Game.SPRITESHEET_WIDTH);
			}
		}
		
		Game.spritesheet.endUse();
		
		if(Game.myPlayer.inventoryOpen) {
			int currentInventorySlot = 0;	// Holds the index of the current inventory slot
			g.setColor(Color.white);
			for(int i = 0; i < Game.myPlayer.inventoryRows; i++) {
				for(int k = 0; k < Game.myPlayer.inventoryColumns; k++) {
					// displays the item's quantity in the inventory
					g.drawString("" + Game.myPlayer.inventory.get(currentInventorySlot).itemStack.quantity, inventoryPositionX + inventorySlotSize * k + 55, inventoryPositionY + inventorySlotSize * i + 49);
					currentInventorySlot++;
				}
			}
			
			// Draws the quantity string for crafting table output
			if(Game.myPlayer.craftingTableOutput.itemStack.item != null){
				g.drawString("" + Game.myPlayer.craftingTableOutput.itemStack.quantity, craftingUIPositionX + 4 * inventorySlotSize + 55, craftingUIPositionY + inventorySlotSize + 50);
			}
			
			
			// Loops through and draws quantity strings for the crafting table
			for(int i = 0; i < 9; i++){
				if(Game.myPlayer.craftingTable.get(i).itemStack.item != null)
					g.drawString("" + Game.myPlayer.craftingTable.get(i).itemStack.quantity, Game.myPlayer.craftingTable.get(i).x + 55, Game.myPlayer.craftingTable.get(i).y + 50);
			}
		}
		
		// Player's mouse item
		Game.spritesheet.startUse();
		// Draws the item that the player picked up with the mouse
		if(Game.myPlayer.cursorItem != null){
			Game.spritesheet.renderInUse(Input.mouseX - Tile.tileSize/2, Input.mouseY - Tile.tileSize/2, Game.myPlayer.cursorItem.item.icon%Game.SPRITESHEET_WIDTH, Game.myPlayer.cursorItem.item.icon/Game.SPRITESHEET_WIDTH);
		}
		Game.spritesheet.endUse();
		
		// Draws the quantity string for picked up items
		if(Game.myPlayer.cursorItem != null) {
			g.drawString("" + Game.myPlayer.cursorItem.quantity, Input.mouseX + 20, Input.mouseY + 20);
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
