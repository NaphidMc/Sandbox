import java.util.concurrent.ThreadLocalRandom;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.TextureImpl;

public class Game extends BasicGame {
	
	public static int mapWidth;
	public static int mapHeight;
	public static float cameraOffsetX, cameraOffsetY;
	
	public static Tile[] map;
	public static Player myPlayer;
	
	public static int hotbarPositionX = 25, hotbarPositionY = 10;
	public static int inventoryPositionX = 10, inventoryPositionY = 250;
	public static int craftingUIPositionX, craftingUIPositionY;
	
	public static int Frames = 0;
	public static int FramesPerSecond = 0;
	
	public static int mapEndCoordinate;
	public static int mapBottonCoordinate;
	
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
	
	/**
	 * 
	 * @param groundLevel - How many blocks of air there are above the base ground height
	 * @param hills - How many hills there are
	 * @param minHillHeight - How low hills can be? I think...
	 * @param maxHillHeight - How high the hills can become
	 * @param minHillWidth - The minimum width of hills
	 * @param maxHillWidth - The widest hills can get
	 * @param stoneDepth - How deep you have to go to see stone
	 * @param stoneTransition - How deep until the stone/dirt layer appears
	 * @param ironDepth - How deep you have to go to see iron
	 * @param ironFrequencyMultiplier - The frequency of iron deposits
	 */
	public void generateMap(int groundLevel, int hills, int minHillHeight, int maxHillHeight, int minHillWidth, int maxHillWidth, int stoneDepth, int stoneTransition, int ironDepth, float ironFrequencyMultiplier){
		int height = 0;
		int width = 0;
		
		MapHill[] mapHills = new MapHill[hills];
		
		for(int i = 0; i < mapHills.length; i++){
			int currentWidth = ThreadLocalRandom.current().nextInt(minHillWidth, maxHillWidth + 1);
			int currentHeight = ThreadLocalRandom.current().nextInt(minHillHeight, maxHillHeight + 1);
			int peakPosition = ThreadLocalRandom.current().nextInt(0, mapWidth - currentWidth + 3);
			
			mapHills[i] = new MapHill(currentWidth, currentHeight, peakPosition + 1, groundLevel);
		}
		
		
		A: for(int i = 0; i < mapWidth*mapHeight; i++){
			
			int currentX = Tile.tileSize*width - (int)cameraOffsetX;
			int currentY = Tile.tileSize*height - (int)cameraOffsetY;
			
			//System.out.println("Current X: " + currentX);
			//System.out.println(Tile.TileSize + "*" + width + "-" + CameraOffsetX);
			
			//Checks if the current tile is part of a hill
			for(int k = 0; k < mapHills.length; k++){
				
				for(int j = 0; j < mapHills[k].hillTiles.size(); j++){
					//System.out.println(mapHills[k].hillTiles.get(j).x + "," + mapHills[k].hillTiles.get(j).y);
					if(mapHills[k].hillTiles.get(j).x == width && mapHills[k].hillTiles.get(j).y == height){

						if(mapHills[k].hillTiles.get(j).topTile){
							map[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
						}else{
							map[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
						}
						
						width++;
						if((i + 1)%mapWidth == 0){
							height++;
							width = 0;
						}
						
						continue A;
					}
				}
			}
			
			
			if(height < groundLevel){
				map[i] = new Tile(currentX, currentY, Database.BLOCK_AIR);
			}else if(height == groundLevel){
				//Top grass layer
				map[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
			}else{
				if(height != mapHeight - 1){
					//Most generation stuff goes here
					int random = ThreadLocalRandom.current().nextInt(1, 101);
					
					if(!(height <= stoneDepth) && !(height >= stoneDepth - stoneTransition)) {
						//Dirt layer
						
						map[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
					} else if(height <= stoneDepth && height >= stoneDepth - stoneTransition) {    
						//Stone transitional layer
						
						if(random <= 55){
							map[i] = new Tile(currentX, currentY, Database.BLOCK_STONE);
						}
						else{
							map[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
						}
					} else if(height > stoneDepth){
						//Stone layer
						
						random = ThreadLocalRandom.current().nextInt(0, 101);
						
						float ironChance = 0.0f;
						if(height > ironDepth){
							ironChance = (height - ironDepth)*ironFrequencyMultiplier*10;
							
							if(ironChance > 10){
								ironChance = 10;
							}
						}
						
						if(random < ironChance){
							map[i] = new Tile(currentX, currentY, Database.BLOCK_IRONORE);
						}else{
						map[i] = new Tile(currentX, currentY, Database.BLOCK_STONE);
						}
					}
				}
				else{
					map[i] = new Tile(currentX, currentY, Database.BLOCK_BEDROCK);
				}
			}
			
			width++;
			if((i + 1)%mapWidth == 0){
				height++;
				width = 0;
			}
			
			if(map[i] == null)
			{
				map[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
			}
		}
	}
	
	public void fixGrassBlocks() {
		for(int i = 0; i < map.length; i++){
			if(map[i].block == Database.BLOCK_GRASS) {
				Block block = getTileAtCoordinates(map[i].x, map[i].y - Tile.tileSize).block;
				if(block != null){
					if(block != Database.BLOCK_AIR && block.solid == true) {     
						map[i].setBlock(Database.BLOCK_DIRT);
					}
				}
			}
		}
	}
	
	public void generateTrees(float treeDensity){
		
		float treeChance = (5*treeDensity);
		for(int i = 0; i < map.length; i++){
			if(map[i].block == Database.BLOCK_GRASS){
				int random = ThreadLocalRandom.current().nextInt(1, 101);
				
				if(random < treeChance){
					//Make a tree
					getTileAtCoordinates(map[i].x, map[i].y - Tile.tileSize).setBlock(Database.BLOCK_SAPLING);
				} else{
					//treeChance += (5*treeDensity);
				}
			}
		}
	}
	
	public void growTrees(){
		System.out.println("Growing trees...");
		int treeGrowChance = 101;
		TileLoop: for(int i = 0; i < map.length; i++){
			if(map[i].block == Database.BLOCK_SAPLING){
				int rand = ThreadLocalRandom.current().nextInt(1, 101);
				if(rand <= treeGrowChance){
					int stemHeight = ThreadLocalRandom.current().nextInt(1, 5);
					
					//Grow tree
					map[i].setBlock(Database.BLOCK_WOOD);
					
					int maxStemHeight = 0;
					for(int k = 1; k < stemHeight; k++){
						Tile tile = getTileAtCoordinates(map[i].x, map[i].y - Tile.tileSize*k);
						if(tile != null){
							if(tile.block == Database.BLOCK_AIR){
								tile.setBlock(Database.BLOCK_WOOD);
							} else {
								continue TileLoop;
							}
						}
						
						maxStemHeight = k;
					}
					
					//Leaves
					Tile temp;
					
					temp = getTileAtCoordinates(map[i].x - Tile.tileSize, map[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(map[i].x, map[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(map[i].x + Tile.tileSize, map[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(map[i].x, map[i].y - maxStemHeight * Tile.tileSize - Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
				}
			}
		}
	}
	
	//Called when the player clicks on a block with something other than a mining tool or block
	public void specialTileInteraction(int x, int y){
		Tile tile = getTileAtCoordinates(x, y);
		
		//Makes sure the tile exists
		if(tile != null){
			//All special actions that are done with grass blocks
			if(myPlayer.selectedItem == Database.ITEM_GRASS_SEEDS){
				if(tile.block == Database.BLOCK_DIRT){
					//Makes sure the tile above is air
					if(getTileAtCoordinates(x, y - Tile.tileSize).block == Database.BLOCK_AIR){
						tile.setBlock(Database.BLOCK_GRASS);
						myPlayer.removeItem(Database.ITEM_GRASS_SEEDS, 1);
					}
				}
			}
		}
	}
	
	//Called when the player clicks on a block with a mining tool
	public void removeTileAtCoordinates(int x, int y){
		
		double dist = Math.sqrt(Math.pow(((y + cameraOffsetY) - myPlayer.y), 2) + Math.pow(((x + cameraOffsetX) - myPlayer.x), 2));
		
		//If the block is too far away, ignore the request
		if(dist > myPlayer.miningDistance){
			return;
		}
		
		Tile tile = getTileAtCoordinates(x, y); //Gets the tile at mouse position
		
		if(tile != null && tile.block != Database.BLOCK_BEDROCK){
			
			//Takes away from the tile's health
			tile.health -= myPlayer.selectedItem.MiningPower;
			
			if(tile.health <= 0){
				
				//Gives the player block drops
				if(tile.block.itemDropIDs[0][0] != -1){
					for(int i = 0; i < tile.block.itemDropIDs[0].length; i++){
						
						int rand = ThreadLocalRandom.current().nextInt(1, 100);
						if(rand < tile.block.itemDropIDs[1][i]){
							myPlayer.addItem(tile.block.itemDropIDs[0][i], 1);
						}
						
					}
				}
				
				tile.block = Database.BLOCK_AIR;
			}
		}
	}
	
	//Called when the player clicks on a block with a block selected in the myPlayer.Hotbar
	public void placeBlockAtCoordinates(int x, int y){
		Tile tile = getTileAtCoordinates(x, y);
		if(tile != null){
			if(tile.block == Database.BLOCK_AIR){
				tile.setBlock(myPlayer.selectedItem.block);
				myPlayer.removeItem(myPlayer.selectedItem, 1);
				fixGrassBlocks();
			} 
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return The tile at the coordinates, returns null if it does not exist
	 */
	public Tile getTileAtCoordinates(int x, int y){
		for(int i = 0; i < map.length; i++){
			java.awt.Rectangle tileRect = new java.awt.Rectangle(map[i].x, map[i].y, Tile.tileSize, Tile.tileSize);
			
			if(tileRect.contains(x, y)){
				return map[i];
			}
		}
		
		return null;
	}

	/**
	 * Renders tiles and entities
	 * @param gc - The GameContainer object
	 * @param g - The current Graphics object
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
		//Draws the sky with an appropriate color
		g.setColor(currentColor);
		g.fillRect(0, 0, appgc.getWidth(), appgc.getHeight());
		
		//Loops through all the tiles and draws them
		sprites.startUse();
		int mapIndex = 0;
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				try {
					//Updates tile coordinates
					map[mapIndex].x = Tile.tileSize * j;
					map[mapIndex].y = Tile.tileSize * i;
					
					//If the tile is 'air' it simply isn't drawn
					if (map[mapIndex].block == Database.BLOCK_AIR) {
						mapIndex++;
						continue;
					
					} else {	
						//Before drawing a tile, it checks if it is visible
						if(Tile.tileSize * j + (int)cameraOffsetX > -Tile.tileSize + 0 && Tile.tileSize * j + (int)cameraOffsetX < 800 && Tile.tileSize * i - (int)cameraOffsetY > 0 -Tile.tileSize && Tile.tileSize * i - (int)cameraOffsetY < 600){

							//Finally, this draws the tile
							sprites.renderInUse(0 + Tile.tileSize * j + (int)cameraOffsetX, 0 + Tile.tileSize * i - (int)cameraOffsetY, map[mapIndex].texture%4, map[mapIndex].texture/4);
							
						}
					}
					mapIndex++;
				} catch (Exception e) { }
				
			}
		}

		// Draws the player
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
		
		mapWidth = 100;
		mapHeight = 32;
		
		mapEndCoordinate = Tile.tileSize * mapWidth;
		mapBottonCoordinate = Tile.tileSize*mapHeight;
		
		craftingUIPositionX = 575;
		craftingUIPositionY = 250;
		
		//Below are all the map generation stuff
		map = new Tile[mapWidth*mapHeight];
		generateMap(7, 13, 2, 5, 2, 4, 12, 1, 13, .5f); //This confusing mess does most of the generation
		fixGrassBlocks(); //This function makes it so grass blocks can't have blocks on top of them
		generateTrees(2f); //Generates the trees; the parameter is tree density
		growTrees(); //Adds leaves and stems to the trees
		
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
		dayColor=new Color(100, 149, 237);
		nightColor=new Color(0,51,102);
		currentColor=dayColor;
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
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
		currentTimeUntilNextCycle-=delta;
		if(currentTimeUntilNextCycle<0){
			
			currentTimeUntilNextCycle=msCycle;
			
			if(currentColor == dayColor){
				currentColor = nightColor;
			} else if(currentColor == nightColor){
				currentColor = dayColor;
			}
		}
		
	}
	
	//Input methods are sent to the Input class to keep Game class cleaner
	@Override 
	public void keyPressed(int key, char c){
		input.keyPressed(key, c);
	}
	
	@Override 
	public void keyReleased(int key, char c){
		input.keyReleased(key, c);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount){
		input.mouseClicked(button, x, y, clickCount);
	}
	
	@Override
	public void mousePressed(int button, int x, int y){
		input.mousePressed(button, x, y);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y){
		if(button == 0){
			MOUSE_BUTTON1_DOWN = false;
		}
	}
}
