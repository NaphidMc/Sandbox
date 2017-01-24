import java.awt.Rectangle;
import java.util.ArrayList;

public class Player {
	
	int ID = -1;
	public float x, y, velocityX, velocityY, moveSpeed = 350f, jumpVelocity = 2.0f;
	private int collisionRectOffsetX = Tile.tileSize * 0, collisionRectOffsetY = 10;
	public int width, height;
	public int miningDistance = 4 * Tile.tileSize;
	
	public Rectangle collisionRect;
	
	public ArrayList<InventorySlot> hotbar = new ArrayList<InventorySlot>();
	public int selectedHotbarSlot = 0;
	public static int numberOfHotbarSlots = 9;
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
		height = Tile.tileSize * 2;
		
		collisionRect = new Rectangle((int)x + collisionRectOffsetX, (int)y + collisionRectOffsetY, width, height);
		
		//Sets up crafting table
		for(int i = 0; i < 9; i++){
			craftingTable.add(new InventorySlot());
		}
		craftingTableOutput.isNotCraftingTableOutput = false;
	}
	
	public void setSelectedHotbarSlot(int slot){
		selectedHotbarSlot = slot;
		selectedItem = hotbar.get(slot).itemStack.item;
	}
	
	public void MoveRight(int delta) {
		if(x + moveSpeed * delta/1000f < Game.mapEndCoordinate - Tile.tileSize){
			if(!tileRightToPlayer()){
				x += moveSpeed * delta/1000f;
				
				if(-Game.cameraOffsetX + 800 < Game.mapEndCoordinate && x >= 400) {
					Game.cameraOffsetX -= moveSpeed * delta/1000f;
				}
			}
		}
	}
	
	public void MoveLeft(int delta){
		if(x - moveSpeed * delta/1000f > 0){
			if(!tileLeftToPlayer()){
				x -= moveSpeed * delta/1000f;
				
				if(-Game.cameraOffsetX > 0 && Game.mapEndCoordinate - x >= 400){
					Game.cameraOffsetX += moveSpeed * delta/1000f;
				}
			}
		}
	}
	
	public void Jump(){
		if(tileUnderPlayer()){
			velocityY += jumpVelocity;
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
	
	public void Update(int delta) {
		
		if(!tileUnderPlayer()){
			velocityY -= 10 * delta/1000f;
		}
		else{
			if(velocityY < 0){
				velocityY = 0;
			}
		}
		
		if(velocityY > 0 && tileAbovePlayer()){
			velocityY = 0;
		}
		
		if(velocityX != 0 || velocityY != 0){
			x -= velocityX;
			y -= velocityY;
			
			if(Game.cameraOffsetY + 600 < Game.mapBottonCoordinate && velocityY < 0){
				Game.cameraOffsetY -= velocityY;
			} 
			else if(Game.mapBottonCoordinate - y > 600/2 - 100 && velocityY > 0){
				Game.cameraOffsetY -= velocityY;
			}
		}
		
		collisionRect = new Rectangle((int)x + collisionRectOffsetX, (int)y + collisionRectOffsetY, width, height);
	}
}
