package com.sandbox.client;
import java.net.InetAddress;
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
import com.sandbox.client.item.Item;
import com.sandbox.client.item.ItemStack;
import com.sandbox.client.map.Map;
import com.sandbox.client.map.Tile;
import com.sandbox.client.network.ClientListener;
import com.sandbox.client.network.PlayerPacket;
import com.sandbox.client.rendering.EntityRenderer;
import com.sandbox.client.rendering.TileRenderer;
import com.sandbox.client.rendering.UIRenderer;
import com.sandbox.client.utils.Logger;

public class Game extends BasicGame {
	
	public static float cameraOffsetX, cameraOffsetY;	// The amount all tiles and such are offset by (in pixels)
	
	public static SpriteSheet spritesheet; // The spritesheet object (set in init)
	public static int SPRITESHEET_WIDTH;   // How many sprites there are in each row in the spritesheet
	
	// Public static object instances:
	public static Game current;				// Current instance of Game
	public static Map currentMap;			// The current Map being displayed
	public static Client client;			// The client in use; null in single player
	public static Player myPlayer;			// The player that is controlled
	public static AppGameContainer appgc; 	// The appGameContainer that holds Game
	public static Input input;				// An instance of the input class; Set in init
	public static MainMenu mainMenu = new MainMenu();
	
	// A list of all players and IDS
	public static java.util.Map<Integer, PlayerPacket> players = new HashMap<Integer, PlayerPacket>();
	public static ArrayList<Integer> playerIDS = new ArrayList<Integer>();
	
	// Current Game status
	public static enum GameState{
		MainMenu,
		Game
	};
	public static GameState currentGameState = GameState.MainMenu;
	
	public boolean mapLoaded = false; 	// Set to true in ClientListener when the last map packet is received
	
	// Day & Night Cycle variables
	int dayLength = 480000; // Day and night cycle are 8 minutes each
	int currentTimeUntilNextCycle = 480000;
	Color currentSkyColor;
	Color dayColor;
	Color nightColor;
	
	public Game(String name) {
		super(name);
		current = this; // The current static instance of Game used by other classes
	}
	
	/**
	 * Initializes a multiplayer game by connecting to a server
	 */
	public void startMultiplayer(){
		
		try{
			client = new Client(InetAddress.getByName("www.naphid.com").getHostAddress(), 6756, 6756);
			client.setListener(new ClientListener());
			client.connect();
		} catch (Exception e){
			System.err.println("Could not connect to server (Make sure the server is running)");
			System.exit(0);
		}
		
		if(client.isConnected()){
			client.getServerConnection().sendTcp(new PlayerPacket(myPlayer)); // The client sends the server the player       
		} else{
			System.err.println("Could not send initial player packet!!");	// This should not happen
			System.exit(0);
		}
	}
	
	/**
	 * Starts a single player game by creating a map
	 */
	public void startSinglePlayer(){
		
		currentGameState = GameState.Game;
		currentMap = new Map(64, 24); // Generates a new map
		currentMap.mapEndCoordinate = Tile.tileSize * Map.getWidth();
		currentMap.mapBottonCoordinate = Tile.tileSize * Map.getHeight();
	}
	
	/**
	 * Renders tiles and entities (players)
	 * @param gc - The GameContainer object
	 * @param g - The current Graphics object
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		// If the map isn't done loading and your not in the main menu draw a black screen
		if(!mapLoaded && currentGameState != GameState.MainMenu){ 
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);
			return;
		}
		
		// If the main menu is open, render that insead
		if(currentGameState == GameState.MainMenu) {
			mainMenu.render(gc, g);
			return;
		}
		
		// Draws the sky
		g.setColor(currentSkyColor);
		g.fillRect(0, 0, appgc.getWidth(), appgc.getHeight());
		
		TileRenderer.renderTiles(g);
		EntityRenderer.renderPlayers(g);
		UIRenderer.renderUI(g);
		
		TextureImpl.bindNone(); // Fixes the FPS counter in the top left
	}
	
	/**
	 * Called when the program launches. Sets up the map, places the player, loads the textures, etc...
	 */
	@Override
	public void init(GameContainer container) throws SlickException {
		
		Logger.initializeLogger();
		Database.populate();
		
		// Configures tile size: divide 800 by a lower number for more tiles and vise versa for less
		Tile.tileSize = 800/12;
		
		// Loads the sprite sheet
		try{
			Image src = new Image("resources/spritesheet.png");
			SPRITESHEET_WIDTH = src.getWidth()/128;	// Sets the number of sprites per row
			spritesheet = new SpriteSheet(src.getScaledCopy(Tile.tileSize * src.getWidth()/128, Tile.tileSize * src.getHeight()/128), Tile.tileSize, Tile.tileSize);
			
		} catch (SlickException e) {
			System.err.println("FAILED TO LOAD SPRITES");
			System.exit(0);
		}
		spritesheet.setFilter(Image.FILTER_NEAREST); // Setting the filter to nearest solves some weird graphical issues
		
		myPlayer = new Player(400, 0); // Instantiates the player at the given coordinates
		
		cameraOffsetX = 0;
		cameraOffsetY = -300;
		
		// Adds the player's hotbar slots and adds starting items
		for(int i = 0; i < Player.numberOfHotbarSlots; i++){
			myPlayer.hotbar.add(new InventorySlot());
		}
		myPlayer.hotbar.get(0).itemStack = new ItemStack(Database.ITEM_PICKAXE, 1);
		myPlayer.hotbar.get(1).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		myPlayer.selectedItem = myPlayer.hotbar.get(0).itemStack.item; 	// By default the player selects the first hotbar slot
		
		// Sets up player inventory and adds starting items
		for(int i = 0; i <= myPlayer.inventoryRows * myPlayer.inventoryColumns; i++){
			myPlayer.inventory.add(new InventorySlot());
		}
		myPlayer.inventory.get(0).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		
		// Night and day Colors setup
		dayColor = new Color(100, 149, 237);
		nightColor = new Color(0, 51, 102);
		currentSkyColor = dayColor;
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
		// The game is not updated if the map is loading or the main menu is open
		if(currentGameState == GameState.MainMenu || !mapLoaded)
			return;
		
		// Updates mouse coords
		Input.mouseX = container.getInput().getMouseX();
		Input.mouseY = container.getInput().getMouseY();
		
		// Makes sure that the player doesn't have an item picked up by their cursor when the inventory is closed
		if(!Game.myPlayer.inventoryOpen){
			Game.myPlayer.cursorItem = null;
		}
		
		// Updates players
		if(delta <= 100)
			myPlayer.update(delta);
		
		// **Input**\\
		if(Input.KEY_A_DOWN){
			myPlayer.moveLeft(delta);
		} else if(Input.KEY_D_DOWN){
			myPlayer.moveRight(delta);
		}
		
		if(Input.MOUSE_BUTTON1_DOWN){
			Input.mouseButtonHeld(0, Input.mouseX, Input.mouseY);
		}
		// *End Input*\\
		
		// Night and Day Cycle
		currentTimeUntilNextCycle -= delta;
		if(currentTimeUntilNextCycle < 0){
			
			currentTimeUntilNextCycle=dayLength;
			
			if(currentSkyColor == dayColor){
				currentSkyColor = nightColor;
			} else if(currentSkyColor == nightColor){
				currentSkyColor = dayColor;
			}
		}
		
		if(myPlayer.ID != -1 && client.getServerConnection().getSocket().isClosed() == false)
			client.getServerConnection().sendTcp(new PlayerPacket(myPlayer)); // The client sends the server the player  
		
		// If the player has moved a significant amount, the game redetermines which chunks to load
		if(Math.abs(myPlayer.x - currentMap.playerXAtChunkReload) > 250)
			currentMap.refreshLoadedChunks();
	}
	
	// Input methods are sent to  the Input class to keep Game class cleaner
	@Override 
	public void keyPressed(int key, char c){
		if(mapLoaded)
			Input.keyPressed(key, c);
		
		if(c == 'p') 
			myPlayer.addHealth(-50);
	}
	
	@Override 
	public void keyReleased(int key, char c){
		if(mapLoaded)
			Input.keyReleased(key, c);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		if(mapLoaded)
			Input.mouseClicked(button, x, y, clickCount);
	}
	
	@Override
	public void mousePressed(int button, int x, int y){
		
		// If the main menu is opened, mouse events are sent straight to the MainMenu Class
		if(currentGameState == GameState.MainMenu){
			mainMenu.mousePressed(button, x, y);
			return;
		}
		
		// Mouse input is only submitted once the map is loaded
		if(mapLoaded)
			Input.mousePressed(button, x, y);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		Input.mouseReleased(button, x, y);
	}
	
	// Exits the game
	public static void quit() {
		Logger.log("Exiting game...");
		Logger.freeResources();
		System.exit(0);
	}
}
