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
import com.sandbox.client.map.Map;
import com.sandbox.client.map.Tile;
import com.sandbox.client.network.ClientListener;
import com.sandbox.client.network.PlayerPacket;
import com.sandbox.client.rendering.EntityRenderer;
import com.sandbox.client.rendering.EnvironmentRenderer;
import com.sandbox.client.rendering.TileRenderer;
import com.sandbox.client.rendering.UIRenderer;
import com.sandbox.client.utils.Logger;

public class Game extends BasicGame {

	public static float cameraOffsetX, cameraOffsetY; // The amount all tiles
														// and such are offset
														// by (in pixels)
	public static final double GRAVITY = 650d;

	public static SpriteSheet spritesheet; // The spritesheet object for tiles +
											// items + etc... (set in init)
	public static SpriteSheet parallaxsheet; // The spritesheet object for
												// parallaxes
	public static int SPRITESHEET_WIDTH; // How many sprites there are in each
											// row in the spritesheet

	// Public static object instances:
	public static Game current; // Current instance of Game
	public static Map currentMap; // The current Map being displayed
	public static Client client; // The client in use; null in single player
	public static Player myPlayer; // The player that is controlled
	public static AppGameContainer appgc; // The appGameContainer that holds
											// Game
	public static Input input; // An instance of the input class; Set in init
	public static MainMenu mainMenu = new MainMenu();

	// A list of all players and IDS
	public static java.util.Map<Integer, PlayerPacket> players = new HashMap<Integer, PlayerPacket>();
	public static ArrayList<Integer> playerIDS = new ArrayList<Integer>();

	// Current Game status
	public static enum GameState {
		MainMenu, Game
	};

	public static GameState currentGameState = GameState.MainMenu;

	public static boolean mapLoaded = false; // Set to true in ClientListener
												// when the last map packet is
												// received

	// Day & Night Cycle variables
	int dayLength = 480000; // Day and night cycle are 8 minutes each
	int currentTimeUntilNextCycle = 480000;
	Color currentSkyColor;
	Color dayColor;
	Color nightColor;

	public Game(String name) {
		super(name);
		current = this; // The current static instance of Game used by other
						// classes
	}

	/**
	 * Initializes a multiplayer game by connecting to a server
	 */
	public void startMultiplayer() {

		try {
			client = new Client(InetAddress.getByName("www.naphid.com").getHostAddress(), 6756, 6756);
			client.setListener(new ClientListener());
			client.connect();
		} catch (Exception e) {
			System.err.println("Could not connect to server (Make sure the server is running)");
			System.exit(0);
		}

		if (client.isConnected()) {
			client.getServerConnection().sendTcp(new PlayerPacket(myPlayer)); // The
																				// client
																				// sends
																				// the
																				// server
																				// the
																				// player
		} else {
			System.err.println("Could not send initial player packet!!"); // This
																			// should
																			// not
																			// happen
			System.exit(0);
		}
	}

	/**
	 * Starts a single player game by creating a map
	 */
	public void startSinglePlayer() {

		currentGameState = GameState.Game;
		currentMap = new Map(64, 24); // Generates a new map
		currentMap.mapEndCoordinate = Tile.tileSize * Map.getWidth();
		currentMap.mapBottonCoordinate = Tile.tileSize * Map.getHeight();
	}

	/**
	 * Renders tiles and entities (players)
	 * 
	 * @param gc
	 *            - The GameContainer object
	 * @param g
	 *            - The current Graphics object
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {

		// If the main menu is open, render that instead
		if (currentGameState == GameState.MainMenu) {
			mainMenu.render(gc, g);
			return;
		}

		EnvironmentRenderer.renderSky(g, currentSkyColor);
		TileRenderer.renderTiles(g);
		EntityRenderer.renderPlayers(g);
		UIRenderer.renderUI(g);
		// g.fillRect(myPlayer.collisionRect.x + cameraOffsetX,
		// myPlayer.collisionRect.y - cameraOffsetY,
		// myPlayer.collisionRect.width, myPlayer.collisionRect.height);

		TextureImpl.bindNone(); // Fixes the FPS counter in the top left
	}

	/**
	 * Called when the program launches. Sets up the map, places the player,
	 * loads the textures, etc...
	 */
	@Override
	public void init(GameContainer container) throws SlickException {

		Logger.initializeLogger();
		Database.populate();

		// Configures tile size: divide 800 by a lower number for more tiles and
		// vise versa for less
		Tile.tileSize = 800 / 12;

		// Loads the sprite sheet
		try {
			Image src = new Image("resources/spritesheet.png");
			SPRITESHEET_WIDTH = src.getWidth() / 128; // Sets the number of
														// sprites per row
			spritesheet = new SpriteSheet(
					src.getScaledCopy(Tile.tileSize * src.getWidth() / 128, Tile.tileSize * src.getHeight() / 128),
					Tile.tileSize, Tile.tileSize);

			Image src2 = new Image("resources/parallaxes.png");
			parallaxsheet = new SpriteSheet(src2, 1600, 400);
		} catch (SlickException e) {
			System.err.println("FAILED TO LOAD SPRITES");
			System.exit(0);
		}
		spritesheet.setFilter(Image.FILTER_NEAREST); // Setting the filter to
														// nearest solves some
														// weird graphical
														// issues
		parallaxsheet.setFilter(Image.FILTER_NEAREST);

		myPlayer = new Player(400, 0); // Instantiates the player at the given
										// coordinates

		cameraOffsetX = 0;
		cameraOffsetY = -300;

		// Night and day Colors setup
		dayColor = new Color(100, 149, 237);
		nightColor = new Color(0, 51, 102);
		currentSkyColor = dayColor;
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {

		// The game is not updated if the map is loading or the main menu is
		// open
		if (currentGameState == GameState.MainMenu || !mapLoaded)
			return;

		// Updates mouse coords
		Input.mouseX = container.getInput().getMouseX();
		Input.mouseY = container.getInput().getMouseY();

		// Makes sure that the player doesn't have an item picked up by their
		// cursor when the inventory is closed
		if (!Game.myPlayer.inventoryOpen) {
			Game.myPlayer.cursorItem = null;
		}

		// Updates players
		if (delta <= 100) // If delta is too high, the player will go through
							// blocks
			myPlayer.update(delta);

		// **Input** \\
		if (Input.KEY_A_DOWN) {
			myPlayer.moveLeft(delta);
		} else if (Input.KEY_D_DOWN) {
			myPlayer.moveRight(delta);
		}

		if (Input.MOUSE_BUTTON1_DOWN) {
			Input.mouseButtonHeld(0, Input.mouseX, Input.mouseY);
		}
		// *End Input*\\

		// Night and Day Cycle
		currentTimeUntilNextCycle -= delta;
		if (currentTimeUntilNextCycle < 0) {

			currentTimeUntilNextCycle = dayLength;

			if (currentSkyColor == dayColor) {
				currentSkyColor = nightColor;
			} else if (currentSkyColor == nightColor) {
				currentSkyColor = dayColor;
			}
		}

		if (myPlayer.ID != -1 && client.getServerConnection().getSocket().isClosed() == false)
			client.getServerConnection().sendTcp(new PlayerPacket(myPlayer)); // The
																				// client
																				// sends
																				// the
																				// server
																				// the
																				// player

		// If the player has moved a significant amount, the game redetermines
		// which chunks to load
		if (Math.abs(myPlayer.x - currentMap.playerXAtChunkReload) > 250)
			currentMap.refreshLoadedChunks();
	}

	/**
	 * Input events are sent to Input.java to separate input from the main game
	 * class
	 */
	@Override
	public void keyPressed(int key, char c) {
		Input.keyPressed(key, c);
	}

	@Override
	public void keyReleased(int key, char c) {
		Input.keyReleased(key, c);
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		Input.mouseClicked(button, x, y, clickCount);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
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
