package main;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.TextureImpl;

import com.jmr.wrapper.client.Client;

import packets.PlayerPacket;

public class Game extends BasicGame {
	
	public static float cameraOffsetX, cameraOffsetY;
	
	public static Player myPlayer;
	
	public static int hotbarPositionX = 25, hotbarPositionY = 10;
	public static int inventoryPositionX = 10, inventoryPositionY = 250;
	public static int craftingUIPositionX, craftingUIPositionY;
	
	public static int Frames = 0;
	public static int FramesPerSecond = 0;
	
	// Key booleans
	public static boolean KEY_A_DOWN;
	public static boolean KEY_D_DOWN;
	public static boolean MOUSE_BUTTON1_DOWN;
	
	//Current mouse coordinates - set in update
	public static int mouseX; 
	public static int mouseY;
	
	public static AppGameContainer appgc;
	public static Input input;
	
	public SpriteSheet sprites;
	public static int SPRITESHEET_WIDTH;
	
	public static Game current;
	public static Map currentMap;
	public static MainMenu mainMenu = new MainMenu();
	public static Client client;
	public boolean mapLoaded = false;
	
	public java.util.Map<Integer, PlayerPacket> players = new HashMap<Integer, PlayerPacket>();
	public ArrayList<Integer> playerIDS = new ArrayList<Integer>();
	
	public static enum GameState{
		MainMenu,
		Game
	};
	public static GameState currentGameState = GameState.MainMenu;
	
	//Day & Night Cycle variables
	int msCycle = 480000; //Day and Night cycle are 8 minutes each
	int currentTimeUntilNextCycle = 480000;
	Color currentColor;
	Color dayColor;
	Color nightColor;
	
	public Game(String name) {
		super(name);
		current = this; //The current static instance of Game used by other classes
	}
	
	public void startMultiplayer(){
		
		client = new Client("localhost", 2667, 2667);
		client.setListener(new ClientListener());
		client.connect();
		
		if(client.isConnected()){
			client.getServerConnection().sendUdp(new PlayerPacket(myPlayer)); //The client sends the server the player       
		}
	}
	
	public void startSinglePlayer(){
		
		mapLoaded = true;
		currentMap = new Map(16, 32); //Generates a new map
		currentMap.mapEndCoordinate = Tile.tileSize * currentMap.getWidth();
		currentMap.mapBottonCoordinate = Tile.tileSize*currentMap.getHeight();
		
	}
	
	/**
	 * Renders tiles and entities
	 * @param gc - The GameContainer object
	 * @param g - The current Graphics object
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		//If the map isn't done loading and your not in the main menu
		if(!mapLoaded && currentGameState != GameState.MainMenu){ 
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);
			return;
		}
		
		if(currentGameState == GameState.MainMenu){
			mainMenu.render(gc, g);
			return;
		}
		
		//Draws the sky with an appropriate color
		g.setColor(currentColor);
		g.fillRect(0, 0, appgc.getWidth(), appgc.getHeight());
		
		//Loops through all the tiles and draws them
		sprites.startUse();
		int mapIndex = 0;
		for (int i = 0; i < currentMap.getHeight(); i++) {
			for (int j = 0; j < currentMap.getWidth(); j++) {
				try {
					//Updates tile coordinates
					currentMap.tiles[mapIndex].x = Tile.tileSize * j;
					currentMap.tiles[mapIndex].y = Tile.tileSize * i;
					
					//If the tile is 'air' it simply isn't drawn
					if (currentMap.tiles[mapIndex].block.equals(Database.BLOCK_AIR)) {
						mapIndex++;
						continue;
					
					} else {	
						//Before drawing a tile, it checks if it is visible
						if(Tile.tileSize * j + (int)cameraOffsetX > -Tile.tileSize + 0 && Tile.tileSize * j + (int)cameraOffsetX < 800 && Tile.tileSize * i - (int)cameraOffsetY > 0 -Tile.tileSize && Tile.tileSize * i - (int)cameraOffsetY < 600){

							//Finally, this draws the tile
							sprites.renderInUse(0 + Tile.tileSize * j + (int)cameraOffsetX, 0 + Tile.tileSize * i - (int)cameraOffsetY, currentMap.tiles[mapIndex].texture%SPRITESHEET_WIDTH, currentMap.tiles[mapIndex].texture/SPRITESHEET_WIDTH);
						}
					}
					mapIndex++;
					
				} catch (Exception e) { }
				
			}
		}

		//Draws other players
		for(java.util.Map.Entry<Integer, PlayerPacket> entry : players.entrySet()){
			PlayerPacket pp = entry.getValue();
			sprites.renderInUse((int)pp.x + (int)cameraOffsetX, (int)pp.y - (int)cameraOffsetY, 1, 2);
		}
		
		// Draws your player
		sprites.renderInUse((int)myPlayer.x + (int)cameraOffsetX, (int)myPlayer.y - (int)cameraOffsetY, 1, 2);
		
		sprites.endUse();
		
		drawUI(g); 
		
		TextureImpl.bindNone(); //Fixes the FPS counter in the top left
	}

	/** Draws all UI elements
	 * 
	 * @param g - The graphics object to draw with
	 */
	public void drawUI(Graphics g){
		
		//If any of the large ui menus is open, then it just draws those
		if(myPlayer.inventoryOpen){
			
			g.setColor(new Color(0, 0, 0, .35f));
			g.fillRect(0, 0, appgc.getWidth(), appgc.getHeight());
		}
		
		//Draws the hotbar slots and colors the selected one
		for(int i = 0; i < myPlayer.hotbar.size(); i++){
			myPlayer.hotbar.get(i).x = hotbarPositionX + 80 * i;
			myPlayer.hotbar.get(i).y = hotbarPositionY;
			
			if(i == myPlayer.selectedHotbarSlot){
				g.setColor(new Color(1f, 1f, 1f, .35f));
				g.fillRect(myPlayer.hotbar.get(i).x, myPlayer.hotbar.get(i).y, InventorySlot.inventorySlotSize + 10, InventorySlot.inventorySlotSize + 10);
			}
			else{
				g.setColor(new Color(0f, 0f, 0f, .35f));
				g.fillRect(myPlayer.hotbar.get(i).x, hotbarPositionY, InventorySlot.inventorySlotSize + 10, InventorySlot.inventorySlotSize + 10);
			}
		}
		
		
		sprites.startUse();
		for(int i = 0; i < myPlayer.hotbar.size(); i++){
			if(myPlayer.hotbar.get(i).itemStack.item != null){
				sprites.renderInUse(hotbarPositionX + 80 * i + 5, hotbarPositionY + 5, myPlayer.hotbar.get(i).itemStack.item.icon%SPRITESHEET_WIDTH, myPlayer.hotbar.get(i).itemStack.item.icon/SPRITESHEET_WIDTH);
			}
		}
		sprites.endUse();
		
		for(int i = 0; i < myPlayer.hotbar.size(); i++){
			if(myPlayer.hotbar.get(i).itemStack.item != null){
				g.setColor(Color.white);
				g.drawString("" + myPlayer.hotbar.get(i).itemStack.quantity, hotbarPositionX + 80 * i + 60, hotbarPositionY + (int)(1.15f*Item.IconSize));
			}
		}
		
		//String to show currently selected item
		String tempItemName = "none";
		if(myPlayer.selectedItem != null)
			tempItemName = myPlayer.selectedItem.Name;
		g.drawString("Selected item: " + tempItemName, 25, 100);
		
		if(!myPlayer.inventoryOpen){
			myPlayer.pickedUpItem = null;
		}
		
		sprites.startUse();
		//Draws the inventory of the player
		if(myPlayer.inventoryOpen){
			
			//inventory
			int currentInventorySlot = 0;
			for(int i = 0; i < myPlayer.inventoryRows; i++){
				for(int k = 0; k < myPlayer.inventoryColumns; k++){
					sprites.renderInUse(inventoryPositionX + InventorySlot.inventorySlotSize * k, inventoryPositionY + InventorySlot.inventorySlotSize * i, 3, 3);
					
					myPlayer.inventory.get(currentInventorySlot).x = inventoryPositionX + InventorySlot.inventorySlotSize * k;
					myPlayer.inventory.get(currentInventorySlot).y = inventoryPositionY + InventorySlot.inventorySlotSize * i;
					
					if(myPlayer.inventory.get(currentInventorySlot).itemStack.item != null){
						//displays the item's icon in the inventory
						sprites.renderInUse(inventoryPositionX + InventorySlot.inventorySlotSize * k, inventoryPositionY + InventorySlot.inventorySlotSize * i, myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon%SPRITESHEET_WIDTH, myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon/SPRITESHEET_WIDTH);     
						
					}
					currentInventorySlot++;
				}
			}
			
			//Crafting table
			int currentCraftingTableIndex = 0;
			int x = 0, y = 0;
			for(int i = 0; i < 9; i++){
					
					myPlayer.craftingTable.get(currentCraftingTableIndex).x = craftingUIPositionX + x * InventorySlot.inventorySlotSize;
					myPlayer.craftingTable.get(currentCraftingTableIndex).y = craftingUIPositionY + y * InventorySlot.inventorySlotSize;
					
					sprites.renderInUse(myPlayer.craftingTable.get(currentCraftingTableIndex).x, myPlayer.craftingTable.get(currentCraftingTableIndex).y, 3, 3);
					
					//Draws the items in the crafting table
					if(myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.item != null){
						sprites.renderInUse(myPlayer.craftingTable.get(currentCraftingTableIndex).x, myPlayer.craftingTable.get(currentCraftingTableIndex).y, myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.item.icon%SPRITESHEET_WIDTH, myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.item.icon/SPRITESHEET_WIDTH); 
					}
					
					currentCraftingTableIndex++;
					
					x++;
					if(x % 3 == 0){
						y++;
						x = 0;
					}
			}
			
			myPlayer.craftingTableOutput.x = myPlayer.craftingTable.get(4).x;
			myPlayer.craftingTableOutput.y = craftingUIPositionY + 3 * InventorySlot.inventorySlotSize;
			
			//Draws the output square
			sprites.renderInUse(myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * InventorySlot.inventorySlotSize, 3, 3);
			
			if(myPlayer.craftingTableOutput.itemStack.item != null){
				sprites.renderInUse(myPlayer.craftingTable.get(4).x, craftingUIPositionY + 3 * InventorySlot.inventorySlotSize, myPlayer.craftingTableOutput.itemStack.item.icon%SPRITESHEET_WIDTH, myPlayer.craftingTableOutput.itemStack.item.icon/SPRITESHEET_WIDTH);
			}
			
			if(myPlayer.pickedUpItem != null){
				sprites.renderInUse(mouseX, mouseY, myPlayer.pickedUpItem.item.icon%4, myPlayer.pickedUpItem.item.icon/4);
			}

		}
		
		sprites.endUse();
		
		if(myPlayer.inventoryOpen){
		int currentInventorySlot = 0;
			for(int i = 0; i < myPlayer.inventoryRows; i++){
				for(int k = 0; k < myPlayer.inventoryColumns; k++){
					//displays the item's quantity in the inventory
					g.drawString("" + myPlayer.inventory.get(currentInventorySlot).itemStack.quantity, inventoryPositionX + InventorySlot.inventorySlotSize * k, inventoryPositionY + InventorySlot.inventorySlotSize * i);
					currentInventorySlot++;
				}
			}
			
			//Draws the quantity string for crafting table output
			if(myPlayer.craftingTableOutput.itemStack.item != null){
				g.drawString("" + myPlayer.craftingTableOutput.itemStack.quantity, craftingUIPositionX + 4 * InventorySlot.inventorySlotSize, craftingUIPositionY + InventorySlot.inventorySlotSize);
			}
			
			//Draws the quantity string for picked up items
			if(myPlayer.pickedUpItem != null){
				g.drawString("" + myPlayer.pickedUpItem.quantity, mouseX, mouseY);
			}
			
			//Loops and draws quantity strings for the crafting table
			for(int i = 0; i < 9; i++){
				if(myPlayer.craftingTable.get(i).itemStack.item != null)
					g.drawString("" + myPlayer.craftingTable.get(i).itemStack.quantity, myPlayer.craftingTable.get(i).x, myPlayer.craftingTable.get(i).y);
			}
		}
		
		//Health Bar
		//Background
		g.setColor(Color.gray);
		g.fillRect(600, 550, 100,15);
		
		//Foreground
		g.setColor(Color.magenta);		
		g.fillRect(600, 550, 100 * (float)(myPlayer.getHealth()/myPlayer.getMaxHealth()), 15);
		
		
		
	}
	
	/**
	 * Called when the program launches. Sets up the map, places the player, loads the textures, etc...
	 */
	@Override
	public void init(GameContainer container) throws SlickException {
		
		//Makes a new Input class
		input = new Input();
		
		//Configures tile size: divide 800 by a lower number for more tiles and vise versa for less
		Tile.tileSize = 800/12;
		
		//Loads the sprite sheet
		try{
			Image src = new Image("resources/spritesheet.png");
			SPRITESHEET_WIDTH = src.getWidth()/128;
			sprites = new SpriteSheet(src.getScaledCopy(Tile.tileSize * src.getWidth()/128, Tile.tileSize * src.getHeight()/128), Tile.tileSize, Tile.tileSize);

		} catch (SlickException e) {
			System.err.println("FAILED TO LOAD SPRITES");
			System.exit(0);
		}
		sprites.setFilter(Image.FILTER_NEAREST); //Setting the filter to nearest solves some weird graphical issues
		
		myPlayer = new Player(400, 0); //Instantiates the player at the given coordinates
		
		craftingUIPositionX = 575;
		craftingUIPositionY = 250;
		cameraOffsetX = 0;
		cameraOffsetY = -300;
		
		//Adds the player's hotbar slots and adds starting items
		for(int i = 0; i < Player.numberOfHotbarSlots; i++){
			myPlayer.hotbar.add(new InventorySlot());
		}
		myPlayer.hotbar.get(0).itemStack = new ItemStack(Database.ITEM_PICKAXE, 1);
		myPlayer.hotbar.get(1).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		myPlayer.selectedItem = myPlayer.hotbar.get(0).itemStack.item;
		
		//Sets up player inventory and adds starting items
		for(int i = 0; i <= myPlayer.inventoryRows*myPlayer.inventoryColumns; i++){
			myPlayer.inventory.add(new InventorySlot());
		}
		myPlayer.inventory.get(0).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		
		//Colors setup
		dayColor = new Color(100, 149, 237);
		nightColor = new Color(0, 51, 102);
		currentColor = dayColor;
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
		if(currentGameState == GameState.MainMenu || !mapLoaded)
			return;
		
		//Updates mouse coords
		mouseX = container.getInput().getMouseX();
		mouseY = container.getInput().getMouseY();
		
		//Updates players
		myPlayer.Update(delta);
		
		//**Input**\\
		if(KEY_A_DOWN){
			myPlayer.MoveLeft(delta);
		} else if(KEY_D_DOWN){
			myPlayer.MoveRight(delta);
		}
		
		if(MOUSE_BUTTON1_DOWN){
			input.mouseButtonHeld(0, mouseX, mouseY);
		}
		//*End Input*\\
		
		//Night and Day Cycle
		currentTimeUntilNextCycle -= delta;
		if(currentTimeUntilNextCycle<0){
			
			currentTimeUntilNextCycle=msCycle;
			
			if(currentColor == dayColor){
				currentColor = nightColor;
			} else if(currentColor == nightColor){
				currentColor = dayColor;
			}
		}
		
		if(myPlayer.ID != -1)
			client.getServerConnection().sendUdp(new PlayerPacket(myPlayer)); //The client sends the server the player  

	}
	
	//Input methods are sent to  the Input class to keep Game class cleaner
	@Override 
	public void keyPressed(int key, char c){
		if(mapLoaded)
			input.keyPressed(key, c);
	}
	
	@Override 
	public void keyReleased(int key, char c){
		if(mapLoaded)
			input.keyReleased(key, c);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		if(mapLoaded)
			input.mouseClicked(button, x, y, clickCount);
	}
	
	@Override
	public void mousePressed(int button, int x, int y){
		
		if(currentGameState == GameState.MainMenu){
			mainMenu.mousePressed(button, x, y);
			return;
		}
		
		if(mapLoaded)
			input.mousePressed(button, x, y);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y){
		if(button == 0){
			MOUSE_BUTTON1_DOWN = false;
		}
	}
}
