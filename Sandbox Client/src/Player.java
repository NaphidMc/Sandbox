import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Player implements ActionListener {
	
	int ID = -1;
	public int x, y, velocityX, velocityY, moveSpeed = 10;
	private int collisionRectOffsetX = 0, collisionRectOffsetY = 10;
	public int width = 100, height = 200;
	public int miningDistance = 4 * Tile.tileSize;
	
	public Rectangle collisionRect;
	
	Image[] animationStages = new Image[1]; //unused for now
	Timer animationTimer;
	int animationFrame;
	
	public ArrayList<InventorySlot> hotbar = new ArrayList<InventorySlot>();
	public int selectedHotbarSlot = 0;
	public static int numberOfHotbarSlots = 10;
	public Item selectedItem;
	
	public ArrayList<InventorySlot> inventory = new ArrayList<InventorySlot>();
	public int inventoryRows = 5;
	public int inventoryColumns = 8;
	public boolean inventoryOpen = false;
	public ItemStack pickedUpItem = null;
	public InventorySlot pickedUpItemOriginSlot = null;

	public ArrayList<InventorySlot> craftingTable = new ArrayList<InventorySlot>();
	public InventorySlot craftingTableOutput = new InventorySlot();
	
	
	public Player(int startPositionX, int startPositionY) {
		System.out.println("Creating new player...");
		
		x = startPositionX;
		y = startPositionY;
		
		width = Tile.tileSize;
		height = Tile.tileSize*2;
		
		collisionRect = new Rectangle(x + collisionRectOffsetX - Game.cameraOffsetX, y + collisionRectOffsetY - Game.cameraOffsetY, width, height);
		
		animationTimer = new Timer(250, (ActionListener)this);
		animationTimer.setActionCommand("Animation Tick");
		animationTimer.setRepeats(true);
		animationTimer.start();
		
		try{
			animationStages[0] = ImageIO.read(new File("resources/player_1.png"));
		}
		catch (Exception e){
			System.out.println("Could not find animation files...using default textures");
			
			try{
				for(int i = 0; i < animationStages.length; i++){
					animationStages[i] = ImageIO.read(new File("resources/default.png"));
				}
			}
			catch (Exception e1){
				System.out.println("Default textures could not be found :(");
				//Animation stages are now null
			}
		}
		
		//Sets up crafting table
		for(int i = 0; i < 9; i++){
			craftingTable.add(new InventorySlot());
		}
		craftingTableOutput.isNotCraftingTableOutput = false;
	}
		
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Animation Tick")){
			animationFrame++;
			
			if(animationFrame >= animationStages.length){
				animationFrame = 0;
			}
		}
	}
	
	
	public Image getPlayerSprite(){
		return animationStages[animationFrame];
	}
	
	public void setSelectedHotbarSlot(int slot){
		selectedHotbarSlot = slot;
		selectedItem = hotbar.get(slot).itemStack.item;
	}
	
	public void MoveRight() {
		if(x + moveSpeed < Game.mapEndCoordinate - Tile.tileSize){
			if(!tileRightToPlayer()){
				x += moveSpeed;
				
				if(Game.cameraOffsetX + GameInit.frame.getWidth() < Game.mapEndCoordinate && x >= GameInit.frame.getWidth()/2) {
					Game.cameraOffsetX += moveSpeed;
				}
			}
		}
	}
	
	public void MoveLeft(){
		if(x - moveSpeed > 0){
			if(!tileLeftToPlayer()){
				x -= moveSpeed;
				
				if(Game.cameraOffsetX > 0 && Game.mapEndCoordinate - x >= GameInit.frame.getWidth()/2){
					Game.cameraOffsetX -= moveSpeed;
				}
			}
		}
	}
	
	public void Jump(){
		if(tileUnderPlayer()){
			velocityY = 30;
		}
	}
	
	public boolean tileUnderPlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int i = 0; i < Game.map.length; i++) {
			if(Game.map[i].block.solid == false)
				continue;
			
			double dist = Math.sqrt(Math.pow(Game.map[i].x - playerRect.x, 2) + Math.pow(Game.map[i].y - playerRect.y, 2));
			
			if(dist > 150)
				continue;
			
			Rectangle tileRect = new Rectangle(Game.map[i].x, Game.map[i].y, Tile.tileSize, Tile.tileSize);
			if(playerRect.intersects(tileRect) && Game.map[i].block != Database.BLOCK_AIR && tileRect.getMaxY() < playerRect.getMaxY() && tileRect.getMinY() > playerRect.getMinY() && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2     
				|| tileRect.intersects(playerRect) && Game.map[i].block != Database.BLOCK_AIR && tileRect.getMaxY() < playerRect.getMaxY() && tileRect.getMinY() > playerRect.getMinY() && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean tileRightToPlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int i = 0; i < Game.map.length; i++) {
			if(Game.map[i].block.solid == false)
				continue;
			
			double dist = Math.sqrt(Math.pow(Game.map[i].x - playerRect.x, 2) + Math.pow(Game.map[i].y - playerRect.y, 2));
			
			if(dist > 150)
				continue;
			
			Rectangle tileRect = new Rectangle(Game.map[i].x, Game.map[i].y, Tile.tileSize, Tile.tileSize);
			if(playerRect.intersects(tileRect) && Game.map[i].block != Database.BLOCK_AIR && (tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) >= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2         
				|| tileRect.intersects(playerRect) && Game.map[i].block != Database.BLOCK_AIR && (tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) >= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2){
				return true;
			}
			
		}
		
		return false;
	}
	
	public boolean tileLeftToPlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int i = 0; i < Game.map.length; i++) {
			if(Game.map[i].block.solid == false)
				continue;
			
			double dist = Math.sqrt(Math.pow(Game.map[i].x - playerRect.x, 2) + Math.pow(Game.map[i].y - playerRect.y, 2));
			
			if(dist > 150)
				continue;
			
			Rectangle tileRect = new Rectangle(Game.map[i].x, Game.map[i].y, Tile.tileSize, Tile.tileSize);
			if(playerRect.intersects(tileRect) && Game.map[i].block != Database.BLOCK_AIR && Math.abs(tileRect.x - playerRect.x) <= playerRect.width && (tileRect.x - playerRect.x) <= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2         
				|| tileRect.intersects(playerRect) && Game.map[i].block != Database.BLOCK_AIR && Math.abs(tileRect.x - playerRect.x) <= playerRect.width  && (tileRect.x - playerRect.x) <= 0 && Math.abs(tileRect.y - playerRect.y) <= Tile.tileSize/2){
				return true;
			}
			
		}
		
		return false;
	}
	
	public boolean tileAbovePlayer(){
		Rectangle playerRect = collisionRect;
		
		for(int i = 0; i < Game.map.length; i++) {
			if(Game.map[i].block.solid == false)
				continue;
			
			double dist = Math.sqrt(Math.pow(Game.map[i].x - playerRect.x, 2) + Math.pow(Game.map[i].y - playerRect.y, 2));
			
			if(dist > 150)
				continue;
			
			Rectangle tileRect = new Rectangle(Game.map[i].x, Game.map[i].y, Tile.tileSize, Tile.tileSize);
			if(playerRect.intersects(tileRect) && Game.map[i].block != Database.BLOCK_AIR && (tileRect.getMinY() - playerRect.getMinY()) <= 0 && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2
				|| tileRect.intersects(playerRect) && Game.map[i].block != Database.BLOCK_AIR && (tileRect.getMinY() - playerRect.getMinY()) <= 0 && Math.abs(playerRect.x - tileRect.x) <= Tile.tileSize/2){
				return true;
			}
		}
		
		return false;
	}
	
	public void addItem(Item item, int quantity){
		//Tries to add it to the hotbar first
		
		//Attempts to add to existing stack
		for(int i = 0; i < hotbar.size(); i++){
			if(hotbar.get(i).itemStack == null || hotbar.get(i).itemStack.item == null)
				continue;
			
			if(hotbar.get(i).itemStack.item.ID == item.ID){
				hotbar.get(i).itemStack.quantity += quantity;
				return;
			}
		}
		
		//Otherwise, it adds a new stack to the hotbar
		for(int i = 0; i < hotbar.size(); i++){
			if(hotbar.get(i).itemStack.item == null){
				//Hotbar slot is empty! add new itemstack
				hotbar.get(i).itemStack = new ItemStack(item, quantity);
				return;
			}
		}
	}
	
	public void addItem(int id, int quantity){
		addItem(Item.getItemByID(id), quantity);
	}
	
	public void removeItem(Item item, int quantity){

		//First tries to remove from inventory and then hotbar
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
	
	public void Update() {
		
		if(!tileUnderPlayer()){
			velocityY -= 2;
		}
		else{
			if(velocityY < 0)
				velocityY = 0;
		}
		
		if(velocityY > 0 && tileAbovePlayer()){
			velocityY = 0;
		}
		
		if(velocityX != 0 || velocityY != 0){
			x -= velocityX;
			y -= velocityY;
			
			if(Game.cameraOffsetY + GameInit.frame.getHeight() < Game.mapBottonCoordinate + 1.4*Tile.tileSize && velocityY < 0){
				Game.cameraOffsetY -= velocityY;
			}
			else if(Game.mapBottonCoordinate - y > GameInit.frame.getHeight()/2 - 100 && velocityY > 0){
				Game.cameraOffsetY -= velocityY;
			}
		}
		
		collisionRect = new Rectangle(x + collisionRectOffsetX - Game.cameraOffsetX, y + collisionRectOffsetY - Game.cameraOffsetY, width, height);
	}
}
