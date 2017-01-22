import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.Timer;

public class Game extends Canvas implements ActionListener {
	
	private static final long serialVersionUID = -7148828155226649547L;
	
	private Timer repaintTimer;
	private Timer inputTimer;
	
	public static int mapWidth;
	public static int mapHeight;
	public static int cameraOffsetX, cameraOffsetY;
	
	public static Tile[] map;
	public static Player myPlayer;
	
	//Drawing objects
	public Canvas canvas;
	public BufferStrategy strategy = null;
	private Graphics2D graphics;
	private Graphics2D backgroundGraphics;
	private BufferedImage background;
	
	public static int hotbarPositionX = 100, hotbarPositionY = 10;
	public static int inventoryPositionX = 100, inventoryPositionY = 250;
	public static int craftingUIPositionX, craftingUIPositionY;
	
	public static int Frames = 0;
	public static int FramesPerSecond = 0;
	
	public static int mapEndCoordinate;
	public static int mapBottonCoordinate;
	
	// Key booleans
	public boolean KEY_A_DOWN, KEY_D_DOWN, MOUSE_BUTTON1_DOWN;
	
	int mouseX, mouseY;

	//public static GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	public Game(GraphicsConfiguration config) {
		super(config);
		
		myPlayer = new Player(2500, 20);
		
		mapWidth = 100;
		mapHeight = 32;
		
		mapEndCoordinate = Tile.tileSize*mapWidth;
		mapBottonCoordinate = Tile.tileSize*mapHeight;
		
		craftingUIPositionX = (int)(GameInit.frame.getWidth()/2);
		craftingUIPositionY = 250;
		
		map = new Tile[mapWidth*mapHeight];
		generateMap(7, 13, 2, 5, 2, 4, 12, 1, 13, .5f);
		fixGrassBlocks();
		generateTrees(2f);
		growTrees();
		
		cameraOffsetX = -GameInit.frame.getWidth()/2 + myPlayer.x;
		cameraOffsetY = -GameInit.frame.getHeight()/2 + 100;
		
		setSize(GameInit.frame.getWidth(), GameInit.frame.getHeight());
		
		background = config.createCompatibleImage(GameInit.frame.getWidth(), GameInit.frame.getHeight());
		backgroundGraphics = (Graphics2D)background.getGraphics();
		
		GameInit.frame.add(this);
		createBufferStrategy(2);
		setFocusable(false);
		
		do{
			strategy = getBufferStrategy();
		} while (strategy == null);
		
		//Sets up the myPlayer.Hotbar
		for(int i = 0; i < Player.numberOfHotbarSlots; i++){
			myPlayer.hotbar.add(new InventorySlot());
		}
		myPlayer.hotbar.get(0).itemStack = new ItemStack(Database.ITEM_PICKAXE, 1);
		myPlayer.hotbar.get(1).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		myPlayer.selectedItem = myPlayer.hotbar.get(0).itemStack.item;
		
		//Sets up player inventory
		for(int i = 0; i <= myPlayer.inventoryRows*myPlayer.inventoryColumns; i++){
			myPlayer.inventory.add(new InventorySlot());
		}
		myPlayer.inventory.get(0).itemStack = new ItemStack(Database.ITEM_DIRT, 1);
		
		repaintTimer = new Timer(0, (ActionListener) this);
		repaintTimer.setRepeats(true);
		repaintTimer.setActionCommand("repaint");
		repaintTimer.start();
		
		inputTimer = new Timer(15, (ActionListener)this);
		inputTimer.setRepeats(true);
		inputTimer.setActionCommand("input");
		inputTimer.start();
		
		TimerTask frameTicker = new TimerTask(){
			public void run(){
				FramesPerSecond = Frames;
				Frames = 0;
			}
		};
		
		//Timer t = new Timer();
		java.util.Timer t = new java.util.Timer();
		t.scheduleAtFixedRate(frameTicker, 1000, 1000);
	}
	
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
			
			int currentX = Tile.tileSize*width - cameraOffsetX;
			int currentY = Tile.tileSize*height - cameraOffsetY;
			
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
	
	private Graphics2D getBuffer(){
		if(graphics == null){
			try{
				graphics = (Graphics2D) strategy.getDrawGraphics();
			} catch (Exception e) { return null; }
		}
		
		return graphics;
	}
	
	public void renderGraphics(Graphics2D g2d){
		
		Frames++;
		
		//If any of the large ui menus is open, then it just draws those
		if(myPlayer.inventoryOpen){
			g2d.setColor(Color.gray);
			g2d.fillRect(0, 0, GameInit.frame.getWidth(), GameInit.frame.getHeight());
			
			drawUI(g2d);
			return;
		}
		
		g2d.setColor(new Color(100, 149, 237));
		g2d.fillRect(0, 0, GameInit.frame.getWidth(), GameInit.frame.getHeight());
		
		// Draws tiles
		int mapIndex = 0;
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				try {
					map[mapIndex].x = 0 + Tile.tileSize * j - cameraOffsetX;
					map[mapIndex].y = 0 + Tile.tileSize * i - cameraOffsetY;
					
					//System.out.println("Real coordinate: " + Map[mapIndex].x);
					if (map[mapIndex].block == Database.BLOCK_AIR) {
						mapIndex++;
						continue;
					
					} else {
						if (new Rectangle(0, 0, GameInit.frame.getWidth() + cameraOffsetX + Tile.tileSize, GameInit.frame.getHeight() + cameraOffsetY + Tile.tileSize).contains(new Rectangle(
								0 + Tile.tileSize * j, 0 + Tile.tileSize * i, Tile.tileSize, Tile.tileSize))) {
							
							g2d.drawImage(map[mapIndex].texture, 0 + Tile.tileSize * j - cameraOffsetX, 0 + Tile.tileSize * i - cameraOffsetY, null);
							
							if(map[mapIndex].health < map[mapIndex].block.health){
								g2d.setColor(Color.WHITE);
								Font healthFont = new Font("serif", Font.PLAIN, 48);
								g2d.setFont(healthFont);
								
								g2d.drawString(map[mapIndex].health + "", 0 + Tile.tileSize * j - cameraOffsetX, 0 + Tile.tileSize * i - cameraOffsetY + Tile.tileSize/2);
							}
						}
					}
					mapIndex++;
				} catch (Exception e) { }
				
			}
		}

		// Draws the player
		Player p = myPlayer;
		//g2d.drawRect(p.CollisionRect.x, p.CollisionRect.y, p.CollisionRect.width, p.CollisionRect.height);
		g2d.drawImage(p.getPlayerSprite().getScaledInstance(Tile.tileSize, Tile.tileSize, 1), p.x - cameraOffsetX, p.y - cameraOffsetY, null);
		
		drawUI(g2d);
	}
	
	public void drawUI(Graphics2D g2d){
	
		//**Draws UI:
		
		//myPlayer.Hotbar:
		for(int i = 0; i < myPlayer.hotbar.size(); i++){
			myPlayer.hotbar.get(i).x = hotbarPositionX + InventorySlot.inventorySlotSize * i;
			myPlayer.hotbar.get(i).y = hotbarPositionY;
			
			if(i == myPlayer.selectedHotbarSlot){
				g2d.drawImage(InventorySlot.selectedTexture, hotbarPositionX + InventorySlot.inventorySlotSize * i, hotbarPositionY, null);
			}
			else{
				g2d.drawImage(InventorySlot.texture, hotbarPositionX + InventorySlot.inventorySlotSize * i, hotbarPositionY, null);
			}
			
			if(myPlayer.hotbar.get(i).itemStack.item != null){
				g2d.drawImage(myPlayer.hotbar.get(i).itemStack.item.icon, hotbarPositionX + InventorySlot.inventorySlotSize * i + (int)(.15f*Item.IconSize), hotbarPositionY + (int)(.15f*Item.IconSize), null);
				
				Font f = new Font("serif", Font.PLAIN, 26);
				g2d.setFont(f);
				g2d.setColor(Color.WHITE);
				g2d.drawString("" + myPlayer.hotbar.get(i).itemStack.quantity, hotbarPositionX + InventorySlot.inventorySlotSize * i + (int)(.95f*Item.IconSize), hotbarPositionY + (int)(1.15f*Item.IconSize));
			}
		}
		
		//String to show currently selected item
		Font f = new Font("serif", Font.PLAIN, 54);
		g2d.setFont(f);
		g2d.setColor(Color.WHITE);
		String tempItemName = "none";
		if(myPlayer.selectedItem != null)
			tempItemName = myPlayer.selectedItem.Name;
		g2d.drawString("Selected item: " + tempItemName, 25, (int)(GameInit.frame.getHeight()/1.12));
		
		Font f1 = new Font("serif", Font.PLAIN, 36);
		g2d.setFont(f1);
		g2d.drawString("fps: " + FramesPerSecond, 10, 150);
		
		if(!myPlayer.inventoryOpen){
			myPlayer.pickedUpItem = null;
		}
		
		//Draws the inventory of the player
		if(myPlayer.inventoryOpen){
			
			//inventory
			int currentInventorySlot = 0;
			for(int i = 0; i < myPlayer.inventoryRows; i++){
				for(int k = 0; k < myPlayer.inventoryColumns; k++){
					g2d.drawImage(InventorySlot.texture, inventoryPositionX + InventorySlot.inventorySlotSize * k, inventoryPositionY + InventorySlot.inventorySlotSize * i, null);
					
					myPlayer.inventory.get(currentInventorySlot).x = inventoryPositionX + InventorySlot.inventorySlotSize * k;
					myPlayer.inventory.get(currentInventorySlot).y = inventoryPositionY + InventorySlot.inventorySlotSize * i;
					
					if(myPlayer.inventory.get(currentInventorySlot).itemStack.item != null){
						//displays the item's icon in the inventory
						g2d.drawImage(myPlayer.inventory.get(currentInventorySlot).itemStack.item.icon, inventoryPositionX + InventorySlot.inventorySlotSize * k + (int)(.15*InventorySlot.inventorySlotSize), inventoryPositionY + InventorySlot.inventorySlotSize * i + (int)(.15*InventorySlot.inventorySlotSize), null);     
						
						//displays the item's quantity in the inventory
						Font f2 = new Font("serif", Font.PLAIN, 26);
						g2d.setFont(f2);
						g2d.setColor(Color.WHITE);
						g2d.drawString("" + myPlayer.inventory.get(currentInventorySlot).itemStack.quantity, inventoryPositionX + InventorySlot.inventorySlotSize * k + (int)(.95f*Item.IconSize), inventoryPositionY + InventorySlot.inventorySlotSize * i + (int)(1.15f*Item.IconSize));
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
					
					g2d.drawImage(InventorySlot.texture, myPlayer.craftingTable.get(currentCraftingTableIndex).x, myPlayer.craftingTable.get(currentCraftingTableIndex).y, null);
					
					if(myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.item != null){
						g2d.drawImage(myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.item.icon, myPlayer.craftingTable.get(currentCraftingTableIndex).x + (int)(.15*InventorySlot.inventorySlotSize), myPlayer.craftingTable.get(currentCraftingTableIndex).y + (int)(.15*InventorySlot.inventorySlotSize), null); 
					
						g2d.drawString("" + myPlayer.craftingTable.get(currentCraftingTableIndex).itemStack.quantity, myPlayer.craftingTable.get(currentCraftingTableIndex).x + (int)(.95f*Item.IconSize), myPlayer.craftingTable.get(currentCraftingTableIndex).y + (int)(1.15f*Item.IconSize));
					}
					
					currentCraftingTableIndex++;
					
					x++;
					if(x % 3 == 0){
						y++;
						x = 0;
					}
			}
			
			//Draws the output square
			myPlayer.craftingTableOutput.x = craftingUIPositionX + 4 * InventorySlot.inventorySlotSize;
			myPlayer.craftingTableOutput.y = craftingUIPositionY + InventorySlot.inventorySlotSize;
			
			g2d.drawImage(InventorySlot.texture, craftingUIPositionX + 4 * InventorySlot.inventorySlotSize, craftingUIPositionY + InventorySlot.inventorySlotSize, null);
			
			if(myPlayer.craftingTableOutput.itemStack.item != null){
				g2d.drawImage(myPlayer.craftingTableOutput.itemStack.item.icon, craftingUIPositionX + 4 * InventorySlot.inventorySlotSize + (int)(.15*InventorySlot.inventorySlotSize), craftingUIPositionY + InventorySlot.inventorySlotSize + (int)(.15f*Item.IconSize), null);
			
				g2d.drawString("" + myPlayer.craftingTableOutput.itemStack.quantity, craftingUIPositionX + 4 * InventorySlot.inventorySlotSize  + (int)(.95f*Item.IconSize), craftingUIPositionY + InventorySlot.inventorySlotSize + (int)(1.15f*Item.IconSize));
			}
			
			if(myPlayer.pickedUpItem != null){
				g2d.drawImage(myPlayer.pickedUpItem.item.icon, mouseX, mouseY, null);
				g2d.drawString("" + myPlayer.pickedUpItem.quantity, mouseX + (int)(.95 * Item.IconSize), mouseY + (int)(1.15 * Item.IconSize));
			}
			
		}
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("repaint")) {
			
			Graphics2D bg = getBuffer();
			
			renderGraphics(backgroundGraphics);
			
			bg.drawImage(background, 0, 0, null);
			
			bg.dispose();
			
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
			
			
			graphics.dispose();
			graphics = null;
			
		} else if(e.getActionCommand().equals("input")){
			
			mouseX = MouseInfo.getPointerInfo().getLocation().x;
			mouseY = MouseInfo.getPointerInfo().getLocation().y;
			
			//This way, you cannot move while the inventory screen is open
			if(myPlayer.inventoryOpen)
				return;
			
			if (KEY_A_DOWN) {
				myPlayer.MoveLeft();
			} else if (KEY_D_DOWN) {
				myPlayer.MoveRight();
			}
			
			//Mouse
			//Left click
			if(MOUSE_BUTTON1_DOWN){
				if(myPlayer.selectedItem != null){
					if(myPlayer.selectedItem.block != null){ //If the player is holding a block
						placeBlockAtCoordinates(mouseX, mouseY);
					}
					//if the player is holding something to break blocks
					else if(myPlayer.selectedItem.MiningPower > 0) {      
						removeTileAtCoordinates(mouseX, mouseY);
					}
					//Special cases
					else{
						specialTileInteraction(mouseX, mouseY);
					}
				}
			}
			
			myPlayer.Update();
		}
	}
	
	//Called when the player clicks on a block with something other than a mining tool or block
	public void specialTileInteraction(int x, int y){
		Tile tile = getTileAtCoordinates(x, y);
		
		if(tile != null){
			if(tile.block == Database.BLOCK_DIRT){
				if(myPlayer.selectedItem == Database.ITEM_GRASS_SEEDS && getTileAtCoordinates(x, y - Tile.tileSize).block == Database.BLOCK_AIR){
					tile.setBlock(Database.BLOCK_GRASS);
					myPlayer.removeItem(Database.ITEM_GRASS_SEEDS, 1);
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
	
	public Tile getTileAtCoordinates(int x, int y){
		for(int i = 0; i < map.length; i++){
			Rectangle tileRect = new Rectangle(map[i].x, map[i].y, Tile.tileSize, Tile.tileSize);
			
			if(tileRect.contains(x, y)){
				return map[i];
			}
		}
		
		return null;
	}
}
