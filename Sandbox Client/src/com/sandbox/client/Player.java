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
	public float x, y, velocityX, velocityY, moveSpeed = 350f, jumpVelocity = 50.0f;  
	
	// Variables for the player's hitbox
	private int collisionRectOffsetX = Tile.tileSize * 0, collisionRectOffsetY = 10;
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
	
	/**
	 * Player constructor
	 * @param startPositionX - Start position for the player (In pixels; world coordinates)
	 * @param startPositionY - Start position for the player (In pixels; world coordinates)
	 */
	public Player(int startPositionX, int startPositionY) {
		Logger.log("Creating new player at: " + "(" + startPositionX + "," + startPositionY + ")");
		x = startPositionX;
		y = startPositionY;
		
		width = Tile.tileSize;
		height = Tile.tileSize * 2;
		
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
			if(!tileRightToPlayer()){
				x += moveSpeed * deltaT/1000f;
				
				if(-Game.cameraOffsetX + 800 < Game.currentMap.mapEndCoordinate && x >= 400) {
					Game.cameraOffsetX -= moveSpeed * deltaT/1000f;
					// Updates parallax positions
					for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
						entry.getValue().x -= entry.getValue().speedX * deltaT/1000f; 
					}
				}
			}
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
			if(!tileLeftToPlayer()){
				x -= moveSpeed * deltaT/1000f;
				
				if(-Game.cameraOffsetX > 0 && Game.currentMap.mapEndCoordinate - x >= 400){
					Game.cameraOffsetX += moveSpeed * deltaT/1000f;
					// Updates parallax positions
					for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
						entry.getValue().x += entry.getValue().speedX * deltaT/1000f; 
					}
				}
			}
		}
	}
	
	/*
	 * Increases the Player's velocity to make him/her jump
	 */
	public void jump() {
		if(tileUnderPlayer() && respawnTimer <= 0){
			velocityY += jumpVelocity;
		}
	}
	
	/**
	 * @return True if the player is colliding with a tile below him/her
	 */
	public boolean tileUnderPlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int k = 0; k < Game.currentMap.chunks.length; k++){
			for(int i = 0; i < Game.currentMap.chunks[k].tiles.length; i++) {
				if(Game.currentMap.chunks[k].tiles[i].type.solid == false)
					continue;
				
				double dist = Math.sqrt(Math.pow(Game.currentMap.chunks[k].tiles[i].x - playerRect.x, 2) + Math.pow(Game.currentMap.chunks[k].tiles[i].y - playerRect.y, 2));
				
				if(dist > 150)
					continue;
				
				Rectangle tileRect = new Rectangle(Game.currentMap.chunks[k].tiles[i].x, Game.currentMap.chunks[k].tiles[i].y, Tile.tileSize, Tile.tileSize);
				if(playerRect.intersects(tileRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && tileRect.getMaxY() < playerRect.getMaxY() && tileRect.getMinY() > playerRect.getMinY() && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2     
					|| tileRect.intersects(playerRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && tileRect.getMaxY() < playerRect.getMaxY() && tileRect.getMinY() > playerRect.getMinY() && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @return True if the player is colliding with a tile to the right of him/her
	 */
	public boolean tileRightToPlayer() {
		Rectangle playerRect = collisionRect;
		
		for(int k = 0; k < Game.currentMap.chunks.length; k++){
			for(int i = 0; i < Game.currentMap.chunks[k].tiles.length; i++) {
				if(Game.currentMap.chunks[k].tiles[i].type.solid == false)
					continue;
				
				double dist = Math.sqrt(Math.pow(Game.currentMap.chunks[k].tiles[i].x - playerRect.x, 2) + Math.pow(Game.currentMap.chunks[k].tiles[i].y - playerRect.y, 2));
				
				if(dist > 150)
					continue;
				
				Rectangle tileRect = new Rectangle(Game.currentMap.chunks[k].tiles[i].x, Game.currentMap.chunks[k].tiles[i].y, Tile.tileSize, Tile.tileSize);
				if(playerRect.intersects(tileRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && (tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) >= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2         
					|| tileRect.intersects(playerRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && (tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) >= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2){
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	/**
	 * @return True if the player is colliding with a tile to the left of him/her
	 */
	public boolean tileLeftToPlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int k = 0; k < Game.currentMap.chunks.length; k++){
			for(int i = 0; i < Game.currentMap.chunks[k].tiles.length; i++) {
				if(Game.currentMap.chunks[k].tiles[i].type.solid == false)
					continue;
				
				double dist = Math.sqrt(Math.pow(Game.currentMap.chunks[k].tiles[i].x - playerRect.x, 2) + Math.pow(Game.currentMap.chunks[k].tiles[i].y - playerRect.y, 2));
				
				if(dist > 150)
					continue;
				
				Rectangle tileRect = new Rectangle(Game.currentMap.chunks[k].tiles[i].x, Game.currentMap.chunks[k].tiles[i].y, Tile.tileSize, Tile.tileSize);
				if(playerRect.intersects(tileRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && Math.abs(tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) <= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2         
					|| tileRect.intersects(playerRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && Math.abs(tileRect.x - playerRect.x) <= playerRect.width  && (tileRect.x - playerRect.x) <= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2){
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	/**
	 * @return True if the player is colliding with something above him/her
	 */
	public boolean tileAbovePlayer() {
		Rectangle playerRect = collisionRect;
		
		for(int k = 0; k < Game.currentMap.chunks.length; k++){
			for(int i = 0; i < Game.currentMap.chunks[k].tiles.length; i++) {
				if(Game.currentMap.chunks[k].tiles[i].type.solid == false)
					continue;
				
				double dist = Math.sqrt(Math.pow(Game.currentMap.chunks[k].tiles[i].x - playerRect.x, 2) + Math.pow(Game.currentMap.chunks[k].tiles[i].y - playerRect.y, 2));
				
				if(dist > 150)
					continue;
				
				Rectangle tileRect = new Rectangle(Game.currentMap.chunks[k].tiles[i].x, Game.currentMap.chunks[k].tiles[i].y, Tile.tileSize, Tile.tileSize);
				if(playerRect.intersects(tileRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && (tileRect.getMinY() - playerRect.getMinY()) <= 0 && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2
					|| tileRect.intersects(playerRect) && !Game.currentMap.chunks[k].tiles[i].type.equals(Database.AIR) && (tileRect.getMinY() - playerRect.getMinY()) <= 0 && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2){
					return true;
				}
			}
		}
		
		return false;
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
		
		if(Game.currentMap == null)
			return;
		
		// If the player is not waiting to respawn
		if(respawnTimer <= 0)
			addHealth(healthRegen * (deltaT/100000f));
		
		if(!tileUnderPlayer()){
			velocityY -= 10 * deltaT/100f;
		}
		else{
			if(velocityY < 0){
				if(velocityY < -65f){   // four block fall damage
					addHealth(velocityY/4f);
				}
				velocityY = 0;
			}
		}
		
		if(velocityY > 0 && tileAbovePlayer()){
			velocityY = 0;
		}
		
		if(velocityX != 0 || velocityY != 0){
			x -= velocityX * deltaT/100f;
			y -= velocityY * deltaT/100f;
			
			if(Game.cameraOffsetY + 600 < Game.currentMap.mapBottonCoordinate && velocityY < 0){
				Game.cameraOffsetY -= velocityY * deltaT/100f;
				// Updates parallax positions
				for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
					entry.getValue().y -= entry.getValue().speedY * deltaT/1000f; 
				}
			} 
			else if(Game.currentMap.mapBottonCoordinate - y > 600/2 - 100 && velocityY > 0){
				Game.cameraOffsetY -= velocityY * deltaT/100f;
				// Updates parallax positions
				for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
					entry.getValue().y += entry.getValue().speedY * deltaT/1000f; 
				}
			}
		}
		
		collisionRect = new Rectangle((int)x + collisionRectOffsetX, (int)y + collisionRectOffsetY, width, height);
	
		
		//respawn timer
		if(health <= 0){
			respawnTimer -= deltaT/1000d;
			if(respawnTimer <= 0){
				respawn();
			}
		}
	}
}
