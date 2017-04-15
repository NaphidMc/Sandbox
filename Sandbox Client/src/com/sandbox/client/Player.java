package com.sandbox.client;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

import com.sandbox.client.item.Item;
import com.sandbox.client.item.ItemStack;
import com.sandbox.client.map.Parallax;
import com.sandbox.client.map.Tile;
import com.sandbox.client.utils.Logger;

public class Player {
	
	public int ID = -1; // Unique ID used for multiplayer
	// jumpVelocity at 45.0f allows for a two block-high jump
	public float x, y, velocityX, moveSpeed = 500, jumpVelocity = -850;
	double velocityY;  
	
	// Variables for the player's hitbox
	private int collisionRectOffsetX, collisionRectOffsetY;
	public int width, height;
	public Rectangle collisionRect;
	
	// How far away the player can mine (in pixels)
	public int miningDistance = 4 * Tile.tileSize;
	
	// Hotbar
	public ArrayList<InventorySlot> hotbar = new ArrayList<InventorySlot>();
	public int selectedHotbarSlot = 0;
	public static int numberOfHotbarSlots = 9;
	public Item selectedItem;
	
	// Inventory
	public ArrayList<InventorySlot> inventory = new ArrayList<InventorySlot>();
	public int inventoryRows = 5;
	public int inventoryColumns = 8;
	public boolean inventoryOpen = false;
	public ItemStack cursorItem = null;					// Holds the item the player has picked up in his/her inventory
	public InventorySlot pickedUpItemOriginSlot = null; // The slot where cursorItem originally came from

	// Crafting table
	public ArrayList<InventorySlot> craftingTable = new ArrayList<InventorySlot>();
	// The output of the crafting table (Stuff appears when a valid recipe is inputted)
	public InventorySlot craftingTableOutput = new InventorySlot();	
	
	// Other stats
	private float health;			// Player's current health
	private float maxHealth = 100;  // Player's maximum health
	private float healthRegen = 5;  // The rate at which the player regenerates health every second
	public double respawnTimer;     // The time until the player respawns (it's > 0 if the player is dead and <= 0 if the player is alive)
	
	public double deltaT = 0d;
	
	/**
	 * Player constructor
	 * @param startPositionX - Start position for the player (In pixels; world coordinates)
	 * @param startPositionY - Start position for the player (In pixels; world coordinates)
	 */
	public Player(int startPositionX, int startPositionY) {
		Logger.log("Creating new player at: " + "(" + startPositionX + "," + startPositionY + ")");
		x = startPositionX;
		y = startPositionY;
		
		width = (int) (Tile.tileSize * .8);
		height = Tile.tileSize;
		
		collisionRect = new Rectangle((int)x + collisionRectOffsetX, (int)y + collisionRectOffsetY, width, height);
		
		// Sets up the crafting table by adding slots to it
		for(int i = 0; i < 9; i++){
			craftingTable.add(new InventorySlot());
		}
		craftingTableOutput.isNotCraftingTableOutput = false;
		
		health = maxHealth;
		
		// Adds the player's hotbar slots and adds starting items
		for(int i = 0; i < Player.numberOfHotbarSlots; i++){
			hotbar.add(new InventorySlot());
		}
		hotbar.get(0).itemStack = new ItemStack(Database.ITEM_PICKAXE, 1);
		hotbar.get(1).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		selectedItem = hotbar.get(0).itemStack.item; 	// By default the player selects the first hotbar slot
		
		// Sets up player inventory and adds starting items
		for(int i = 0; i <= inventoryRows * inventoryColumns; i++){
			inventory.add(new InventorySlot());
		}
		inventory.get(0).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		
	}
	
	/**
	 * The setter for health. Also makes sure that health never goes above max health and
	 * when health is at or below zero the player dies
	 * @param amount - The amount to add; Can be negative
	 */
	public void addHealth(float amount){
		health += amount;
		if(health <= 0){
			die();
		} else if(health > maxHealth){
			health = maxHealth;
		}
	}
	
	/**
	 * Sets the respawn timer indicating the player is dead
	 */
	public void die() {
		respawnTimer = 10;
	}
	
	/**
	 * Resets max health and places the player at a spawn point
	 */
	public void respawn() {
		teleportTo(400, 0);
		health = maxHealth;
	}
	
	// Moves the player and the camera to a location
	public void teleportTo(int x, int y) {
		this.x = x;
		this.y = y;
		
		Game.cameraOffsetX = this.x - 400;
		Game.cameraOffsetY = this.y - 300;
	}
	
	/**
	 * Getter for health
	 * @return Player's health
	 */
	public float getHealth(){
		return health;
	}
	
	/**
	 * Moves the player, taking collisions into account
	 * @param deltaX The change in x position
	 * @param deltaY The change in y position
	 */
	public void translate(double deltaX, double deltaY) {
		int steps = 3;
		double stepX = deltaX/steps;
		double stepY = deltaY/steps;
		
		boolean xpositive = deltaX > 0 ? true : false;
		boolean ypositive = deltaY > 0 ? true : false;
		
		if(deltaX != 0) {
			for(int i = 0; i < steps; i++) {
				deltaX = (int) stepX;
				if(deltaX == 0) {
					deltaX += xpositive == true ? 1 : -1;
				}
				
				collisionRect.translate((int) deltaX, 0);
				
				if(Game.currentMap.collision(collisionRect)) {
					collisionRect.translate(-(int) deltaX, 0);
				} else {
					x += (int) deltaX;
					if(deltaX < 0 && -deltaX + Game.cameraOffsetX < 0) {
						if(Math.abs(x + Game.cameraOffsetX) < 400) {
							Game.cameraOffsetX -= (int) deltaX;
						
							// Updates parallax positions
							for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
								entry.getValue().x -= entry.getValue().speedX * deltaX; 
							}
						}
					} else if(deltaX > 0 && -Game.cameraOffsetX + 800 + deltaX < Game.currentMap.mapEndCoordinate) {
						if(x - Game.cameraOffsetX >= 400) {
							Game.cameraOffsetX -= (int) deltaX;
							
							// Updates parallax positions
							for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
								entry.getValue().x -= entry.getValue().speedX * deltaX; 
							}
						}
					}
				}
			}
		}
		
		if(deltaY != 0) {
			for(int i = 0; i < steps; i++) {
				
				deltaY = (int) stepY;
				if(deltaY == 0) {
					deltaY += ypositive ? 1 : -1;
				}
				
				collisionRect.translate(0, (int) deltaY);
				
				if(Game.currentMap.collision(collisionRect)) {
					collisionRect.translate(0, -(int) deltaY);
					velocityY = 0;
				} else {
					y += deltaY;
					
					if(Game.currentMap.mapBottonCoordinate - y > 600/2 - 100 && Game.cameraOffsetY + 600 < Game.currentMap.mapBottonCoordinate){
						Game.cameraOffsetY += deltaY;
					}
					
					// Updates parallax positions
					for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
						entry.getValue().y -= entry.getValue().speedY * deltaY; 
					}
				}
			}
		}
	}
	
	/**
	 * Getter for max health
	 * @return Player's max health
	 */
	public float getMaxHealth(){
		return maxHealth;
	}
	
	/**
	 * Sets the currently selected hotbar slot
	 * @param slot - The index of the slot to select. Range 0 to 8
	 */
	public void setSelectedHotbarSlot(int slot){
		selectedHotbarSlot = slot;
		selectedItem = hotbar.get(slot).itemStack.item;
	}
	
	/**
	 * Moves the player right
	 * @param deltaT - Milliseconds since last frame (Obtained from update in Game.java)
	 */
	public void moveRight(int deltaT) {
		if(respawnTimer > 0)
			return;
		
		if(x + moveSpeed * deltaT/1000f < Game.currentMap.mapEndCoordinate - Tile.tileSize){
			translate(moveSpeed * deltaT/1000f, 0);
		}
	}
	
	/**
	 * Moves the player left
	 * @param deltaT - Milliseconds since last frame (Obtained from update in Game.java)
	 */
	public void moveLeft(int deltaT) {
		if(respawnTimer > 0)
			return;
		
		if(x - moveSpeed * deltaT/1000f > 0){
			translate(-moveSpeed * deltaT/1000f, 0);
		}
	}
	
	/*
	 * Increases the Player's velocity to make him/her jump
	 */
	public void jump() {
		collisionRect.translate(0, 5);
		if(Game.currentMap.collision(collisionRect)) {
			velocityY += jumpVelocity;
		}
		
		collisionRect.translate(0, -5);
	}
	
	/**
	 * Adds an item to the Player's inventory. First attempts to place it in the hotbar
	 * and then the inventory.
	 * @param item The item to add
	 * @param quantity How much of it to add
	 */
	public void addItem(Item item, int quantity){
		// Tries to add it to the hotbar first
		// Attempts to add to existing stack
		for(int i = 0; i < hotbar.size(); i++) {
			if(hotbar.get(i).itemStack == null || hotbar.get(i).itemStack.item == null)
				continue;
			
			if(hotbar.get(i).itemStack.item.ID == item.ID) {
				hotbar.get(i).itemStack.quantity += quantity;
				return;
			}
		}
		
		// Otherwise, it adds a new stack to the hotbar
		for(int i = 0; i < hotbar.size(); i++) {
			if(hotbar.get(i).itemStack == null || hotbar.get(i).itemStack.item == null){
				// Hotbar slot is empty! add new itemstack
				hotbar.get(i).itemStack = new ItemStack(item, quantity);
				return;
			}
		}
		
		// Otherwise, the item is added to the Player's inventory
		
		// Attempts to find a stack to add to 
		for(int i = 0; i < inventory.size(); i++) {
			if(inventory.get(i).itemStack == null || hotbar.get(i).itemStack.item == null) {
				continue;
			}
			
			if(hotbar.get(i).itemStack.item.ID == item.ID) {
				inventory.get(i).itemStack.quantity += quantity;
				return;
			}
		}
		
		// Adds a new stack if there is space
		for(int i = 0; i < inventory.size(); i++) {
			if(inventory.get(i).itemStack == null || inventory.get(i).itemStack.item == null) {
				// Slot is empty, add new itemstack
				inventory.get(i).itemStack = new ItemStack(item, quantity);
				return;
			}
		}
	}
	
	/**
	 * Adds an item to the player's inventory
	 * @param id The ID of the item to add (Set in Database.java)
	 * @param quantity The amount to add
	 */
	public void addItem(int id, int quantity){
		addItem(Item.getItemByID(id), quantity); 
	}
	
	/**
	 * Removes an item from the Player's inventory or hotbar
	 * @param item The item to remove
	 * @param quantity How much of it to remove
	 */
	public void removeItem(Item item, int quantity){
		
		// First tries to remove from the inventory and then the hotbar
		// Idk why more games don't do it like this
		for(int i = 0; i < inventory.size(); i++){
			if(inventory.get(i).itemStack.item == null)
				continue;
			
			if(inventory.get(i).itemStack.item.ID == item.ID){
				if(inventory.get(i).itemStack.quantity - quantity > 0){
					inventory.get(i).itemStack.quantity--;
					return;
				}
				else{
					inventory.remove(i);
					return;
				}
			}
		}
		
		for(int i = 0; i < hotbar.size(); i++){
			
			if(hotbar.get(i).itemStack == null || hotbar.get(i).itemStack.item == null)
				continue;
			
			if(hotbar.get(i).itemStack.item.ID == item.ID){
				if(hotbar.get(i).itemStack.quantity - quantity > 0){
					hotbar.get(i).itemStack.quantity -= quantity;
					return;
				}
				else{
					hotbar.get(i).itemStack = new ItemStack(null, 0);
					selectedItem = hotbar.get(i).itemStack.item;
					return;
				}
			}
		}
	}
	
	public void update(int deltaT) {
		this.deltaT = deltaT;
		
		collisionRect = new Rectangle((int)x + collisionRectOffsetX, (int)y + collisionRectOffsetY, width, height);
		if(Game.currentMap == null)
			return;
		
		// If the player is not waiting to respawn
		if(respawnTimer <= 0)
			addHealth(healthRegen * (deltaT/100000f));
		
		velocityY = velocityY + Game.GRAVITY * deltaT/1000d;
		translate(0, velocityY * deltaT/1000d + .5d * Game.GRAVITY * deltaT/500d);
		
		// Respawn timer
		if(health <= 0){
			respawnTimer -= deltaT/1000d;
			if(respawnTimer <= 0){
				respawn();
			}
		}
	}
}
