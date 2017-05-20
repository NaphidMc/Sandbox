package com.sandbox.client.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import com.sandbox.client.Database;
import com.sandbox.client.Game;
import com.sandbox.client.utils.Logger;

public class Map {
	
	public Chunk[] chunks;
	public ArrayList<Chunk> loadedChunks;
	private static int mapWidth, mapHeight;
	public static int chunkSize = 16;	// How large each chunk is in width
	public float playerXAtChunkReload; 	// The player's x position when refreshLoadedChunks was last called
	public int mapEndCoordinate; 		// X - Coordinate of the right edge of the map
	public int mapBottonCoordinate; 	// Y - Coordinate of the bottom edge of the map
	// Holds all the parallaxes for the map and their layers
	// FORMAT: <Layer, Parallax>
	public TreeMap<Integer, Parallax> parallaxes = new TreeMap<Integer, Parallax>();
	
	// Map generator assistant class
	public class MapHillTile {
		public int x, y;
		public boolean topTile;
		
		public MapHillTile(int x, int y, boolean topTile){
			this.x = x;
			this.y = y;
			this.topTile = topTile;
		}
	}
	
	// Map Generator Assistant class
	public class MapHill {
		
		public int height;
		public int width;
		public int peakPositionX;
		public int groundLevel;
		public ArrayList<MapHillTile> hillTiles = new ArrayList<MapHillTile>();
		
		public MapHill(int height, int width, int peakPositionX, int groundLevel){
			this.height = height;
			this.width = width;
			this.groundLevel = groundLevel;
			this.peakPositionX = peakPositionX;
			generateHillTiles();
		}
		
		public void generateHillTiles(){
			int startX = peakPositionX - width;
			int endX = peakPositionX + width;
			int step = (height)/(peakPositionX - startX);
			
			int currentHeight = 0;
			boolean ascending = true;
			for(int i = 0; i <= endX - startX; i++){
				
				if(i == 0){
					hillTiles.add(new MapHillTile(startX, groundLevel - 1, true));
				}
				else if(i == ((endX - startX) - 1)){
					hillTiles.add(new MapHillTile(endX, groundLevel - 1, true));
				}
				else{
					for(int k = 0; k < currentHeight; k++) {
						if(k == currentHeight - 1){
							hillTiles.add(new MapHillTile(startX + i, groundLevel - k - 1, true));
						}
						else{
							hillTiles.add(new MapHillTile(startX + i, groundLevel - k - 1, false));
						}
						
						if(k == height - 1){
							ascending = false;
						}
					}
				}
				
				if(ascending){
					
					if(peakPositionX - i - startX != 0)
						step = (height - currentHeight)/((peakPositionX - i) - startX);
					else if(peakPositionX - i - startX == 0){
						step = 1;
					}
				} else {
					step = (currentHeight - groundLevel)/(endX - (startX - i) + 1);
					
					if(step >= 0) {
						step = -1;
					}
				}
				currentHeight += step;

			}
		}
	}
	
	public Map() { }
	
	public Map(int mapWidth, int mapHeight){
		
		Map.mapWidth = mapWidth;
		Map.mapHeight = mapHeight;
		
		Date start = null;
		Date finish = null;
		long timeElapsed = 0;
		
		chunks = new Chunk[(int) Math.ceil(mapWidth/chunkSize)];
		loadedChunks = new ArrayList<Chunk>();
		for(int i = 0; i < chunks.length; i++){
			chunks[i] = new Chunk(chunkSize);
			chunks[i].chunkIndex = i;
			loadedChunks.add(chunks[i]);
		}
		
		Logger.log("Generating map...");
		start = new Date();
		generateTiles(7, 13, 2, 5, 2, 4, 12, 1, 13, .4f, 10, .70f); // This confusing mess does most of the generation
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		Logger.log("Done! (" + timeElapsed + "ms)");
		
		Logger.log("Fixing grass blocks... ");
		start = new Date();
		fixGrassBlocks(); // This function makes it so grass blocks can't have blocks on top of them
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		Logger.log("Done! (" + timeElapsed + "ms)");
		
		Logger.log("Placing trees... ");
		start = new Date();
		generateTrees(2f); // Generates the trees; the parameter is tree density
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		Logger.log("Done! (" + timeElapsed + "ms)");
		
		Logger.log("Growing trees... ");
		start = new Date();
		growTrees(); // Adds leaves and stems to the trees
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		Logger.log("Done! (" + timeElapsed + "ms)");
		
		Logger.log("Calculating light levels... ");
		start = new Date();
		calculateLightLevels();
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		Logger.log("Done! (" + timeElapsed + "ms)");
		
		refreshLoadedChunks();
		
		addParallaxes();
		
		Game.mapLoaded = true;
	}
	
	/**
	 * Adds parallaxes to the map
	 */
	public void addParallaxes() {
		parallaxes.put(new Integer(0), new Parallax(0, 0, .05, .02));
		parallaxes.put(new Integer(1), new Parallax(0, 1, .15, .03));
		parallaxes.put(new Integer(2), new Parallax(0, 2, .25, .04));
	}
	
	/**
	 * Reevaluates which chunks are loaded
	 */
	public void refreshLoadedChunks(){
		
		playerXAtChunkReload = Game.myPlayer.x;
		
		loadedChunks.clear();
		
		for(int i = 0; i < chunks.length; i++){
			float chunkPos = (i) * chunkSize * Tile.tileSize + chunkSize/2 * Tile.tileSize;
    
			if(Math.abs(chunkPos - Game.myPlayer.x) < 1250){
				loadedChunks.add(chunks[i]);  
			}
		}
	}
	
	/**
	 * Returns the width in tiles (NOT pixels) of the map
	 */
	public static int getWidth(){
		return mapWidth;
	}
	
	/**
	 * Returns the height in tiles (NOT pixels) of the map
	 */
	public static int getHeight(){
		return mapHeight;
	}
	
	public void setWidth(int value){
		Map.mapWidth = value;
	}
	
	public void setHeight(int value){
		Map.mapHeight = value;
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
	public void generateTiles(int groundLevel, int hills, int minHillHeight, int maxHillHeight, int minHillWidth, int maxHillWidth, int stoneDepth, int stoneTransition, int ironDepth, float ironFrequencyMultiplier, int coalDepth, float coalFrequencyMultiplier){
		
		int height = 0;
		int width = 0;
		int tileIndex = 0;
		int clampedWidth = 0;
		
		MapHill[] mapHills = new MapHill[hills];
		
		for(int i = 0; i < mapHills.length; i++){
			int currentWidth = ThreadLocalRandom.current().nextInt(minHillWidth, maxHillWidth + 1);
			int currentHeight = ThreadLocalRandom.current().nextInt(minHillHeight, maxHillHeight + 1);
			int peakPosition = ThreadLocalRandom.current().nextInt(0, mapWidth - currentWidth + 3);
			
			mapHills[i] = new MapHill(currentWidth, currentHeight, peakPosition + 1, groundLevel);
		}
		
		
		A: for(int i = 0; i < mapWidth * mapHeight; i++){
			
			tileIndex = clampedWidth + height * chunkSize;
			
			int currentX = Tile.tileSize * width;
			int currentY = Tile.tileSize * height;
			
			// Checks if the current tile is part of a hill
			for(int k = 0; k < mapHills.length; k++){
				
				for(int j = 0; j < mapHills[k].hillTiles.size(); j++){
					
					if(mapHills[k].hillTiles.get(j).x == width && mapHills[k].hillTiles.get(j).y == height){
						
						if(mapHills[k].hillTiles.get(j).topTile){
							//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
							chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.GRASS));
						} else{
							//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
							chunks[(width/(chunkSize))].tiles[tileIndex] = ( new Tile(currentX, currentY, Database.DIRT));
						}
						
						clampedWidth++;
						if(clampedWidth >= chunkSize)
							clampedWidth = 0;
						
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
				//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_AIR);
				chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.AIR));
			} else if(height == groundLevel){
				// Top grass layer
				//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
				chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.GRASS));
			} else {
				if(height != mapHeight - 1) {
					// Most generation stuff goes here
					int random = ThreadLocalRandom.current().nextInt(1, 101);
					
					if(!(height <= stoneDepth) && !(height >= stoneDepth - stoneTransition)) {
						// Dirt layer
						
						//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
						chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.DIRT));
					} else if(height <= stoneDepth && height >= stoneDepth - stoneTransition) {    
						// Stone transitional layer
						
						if(random <= 55){
							//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_STONE);
							chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.STONE));
						}
						else{
							//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
							chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.DIRT));
						}
					} else if(height > stoneDepth) {
						// Stone layer
						
						ArrayList<Integer> chances = new ArrayList<Integer>();
						
						// Iron
						float ironChance = 0.0f;
						if(height > ironDepth){
							ironChance = (height - ironDepth) * ironFrequencyMultiplier * 10;
							
							if(ironChance > 10){
								ironChance = 10;
							}
						}
						random = ThreadLocalRandom.current().nextInt(0, (int) (ironChance + 1));
						chances.add(new Integer(random));
						
						// Coal
						float coalChance = 0.0f;
						if(height > coalDepth) {
							coalChance = (height - coalDepth) * coalFrequencyMultiplier * 10;
							
							if(coalChance > 10) {
								coalChance = 10;
							}
						}
						random = ThreadLocalRandom.current().nextInt(0, (int) (coalChance + 1));
						chances.add(new Integer(random));
						
						// Stone
						float stoneChance = (ironChance + coalChance)/2;
						random = ThreadLocalRandom.current().nextInt(0, (int) (stoneChance + 1));
						chances.add(new Integer(random));
						
						int highestNumber = Collections.max(chances);
						
						if(highestNumber == ironChance) {
							chunks[(width/(chunkSize))].tiles[tileIndex] = new Tile(currentX, currentY, Database.IRONORE);
						} else if(highestNumber == coalChance) {
							chunks[(width/(chunkSize))].tiles[tileIndex] = new Tile(currentX, currentY, Database.COALORE);
						} else {
							chunks[(width/(chunkSize))].tiles[tileIndex] = new Tile(currentX, currentY, Database.STONE);
						}
					} 
				}
				else {
					chunks[(width/(chunkSize))].tiles[tileIndex] = new Tile(currentX, currentY, Database.BEDROCK);
				}
			}
			
			clampedWidth++;
			if(clampedWidth >= chunkSize)
				clampedWidth = 0;
			
			width++;
			if((i + 1)%mapWidth == 0){
				height++;
				width = 0;
			}
			
			if(chunks[(width/(chunkSize))].tiles[tileIndex] == null) {
				//tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
				chunks[(width/(chunkSize))].tiles[tileIndex] = (new Tile(currentX, currentY, Database.DIRT));
			}
			
		}
	}
	
	public void calculateLightLevels(){
		
		HashMap<String, Tile> tileCache = new HashMap<String, Tile>();
		
		for(int i = 0; i < loadedChunks.size(); i++){
			for(int j = 0; j < loadedChunks.get(i).tiles.length; j++){
				loadedChunks.get(i).tiles[j].lightLevel = 0f;
			}
		}
		
		for(int i = 0; i < loadedChunks.size(); i++){
			for(int k = 0; k < chunks[i].tiles.length; k++){
				
				Tile above = null, below = null, right = null, left = null;
				
				if((above = tileCache.get(loadedChunks.get(i).tiles[k].x + "," + (loadedChunks.get(i).tiles[k].y - Tile.tileSize))) == null){
					above = getTileAtCoordinates(loadedChunks.get(i).tiles[k].x, loadedChunks.get(i).tiles[k].y - Tile.tileSize);
					tileCache.put(loadedChunks.get(i).tiles[k].x + "," + (loadedChunks.get(i).tiles[k].y - Tile.tileSize), above);
				}
				if((below = tileCache.get(loadedChunks.get(i).tiles[k].x + "," + (loadedChunks.get(i).tiles[k].y + Tile.tileSize))) == null){
					below = getTileAtCoordinates(loadedChunks.get(i).tiles[k].x, loadedChunks.get(i).tiles[k].y + Tile.tileSize); 
					tileCache.put(loadedChunks.get(i).tiles[k].x + "," + (loadedChunks.get(i).tiles[k].y + Tile.tileSize), below);
				}
				if((right = tileCache.get((loadedChunks.get(i).tiles[k].x + Tile.tileSize) + "," + loadedChunks.get(i).tiles[k].y)) == null){
					right = getTileAtCoordinates(loadedChunks.get(i).tiles[k].x + Tile.tileSize, loadedChunks.get(i).tiles[k].y); 
					tileCache.put((loadedChunks.get(i).tiles[k].x + Tile.tileSize) + "," + loadedChunks.get(i).tiles[k].y, right);
				}
				if((left = tileCache.get((loadedChunks.get(i).tiles[k].x - Tile.tileSize) + "," + loadedChunks.get(i).tiles[k].y)) == null){
					left = getTileAtCoordinates(loadedChunks.get(i).tiles[k].x - Tile.tileSize, loadedChunks.get(i).tiles[k].y);
					tileCache.put((loadedChunks.get(i).tiles[k].x - Tile.tileSize) + "," + loadedChunks.get(i).tiles[k].y, left);
				}
				
				if(loadedChunks.get(i).tiles[k].type.equals(Database.AIR) || loadedChunks.get(i).tiles[k].type.equals(Database.WOOD) || loadedChunks.get(i).tiles[k].type.equals(Database.LEAVES)){
					loadedChunks.get(i).tiles[k].lightLevel = 2.0f;
				}
			
			if(above != null) {
				above.lightLevel += loadedChunks.get(i).tiles[k].lightLevel/4f;
			}
			if(below != null) {
				below.lightLevel += loadedChunks.get(i).tiles[k].lightLevel/4f;
			}
			if(right != null) {
				right.lightLevel += loadedChunks.get(i).tiles[k].lightLevel/4f;
			}
			if(left != null) {
				left.lightLevel += loadedChunks.get(i).tiles[k].lightLevel/4f;
			}
				
			}
		}
	}
	
	/**
	 * Checks if any grass blocks have a block above them,
	 * and if they do, the grass block is changed to dirt
	 */
	public void fixGrassBlocks() {
		for(int i = 0; i < loadedChunks.size(); i++){
			for(int k = 0; k < loadedChunks.get(i).tiles.length; k++){
				if(loadedChunks.get(i).tiles[k].type == Database.GRASS) {
					TileType block = getTileAtCoordinates(loadedChunks.get(i).tiles[k].x, (int) (loadedChunks.get(i).tiles[k].y - Tile.tileSize)).type;
					if(block != null) {
						if(block != Database.AIR && block.solid == true) {     
							loadedChunks.get(i).tiles[k].setTileType(Database.DIRT);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Places saplings all over the map
	 * @param treeDensity - The frequency of trees
	 */
	public void generateTrees(float treeDensity){
		
		float treeChance = (5*treeDensity);
		
		for(int k = 0; k < chunks.length; k++){
			for(int i = 0; i < chunks[k].tiles.length; i++){
				if(chunks[k].tiles[i].type == Database.GRASS){
					int random = ThreadLocalRandom.current().nextInt(1, 101);
					
					if(random < treeChance){
						// Make a tree
						getTileAtCoordinates(chunks[k].tiles[i].x, chunks[k].tiles[i].y - Tile.tileSize).setTileType(Database.SAPLING);
					} 
				}
			}
		}
	}
	
	/**
	 * Replaces all saplings with fully grown trees
	 */
	public void growTrees(){
		int treeGrowChance = 101;
		
		for(int j = 0; j < chunks.length; j++){
			TileLoop: for(int i = 0; i < chunks[j].tiles.length; i++){
				if(chunks[j].tiles[i].type == Database.SAPLING){
					int rand = ThreadLocalRandom.current().nextInt(1, 101);
					if(rand <= treeGrowChance){
						int stemHeight = ThreadLocalRandom.current().nextInt(1, 5);
						
						// Grow tree
						chunks[j].tiles[i].setTileType(Database.WOOD);
						
						int maxStemHeight = 0;
						for(int k = 1; k < stemHeight; k++){
							Tile tile = getTileAtCoordinates(chunks[j].tiles[i].x, chunks[j].tiles[i].y - Tile.tileSize*k);
							if(tile != null){
								if(tile.type == Database.AIR){
									tile.setTileType(Database.WOOD);
								} else {
									continue TileLoop;
								}
							}
							
							maxStemHeight = k;
						}
						
						// Leaves
						Tile temp;
						
						temp = getTileAtCoordinates(chunks[j].tiles[i].x - Tile.tileSize, chunks[j].tiles[i].y - maxStemHeight * Tile.tileSize);
						if(temp != null){
							if(temp.type == Database.AIR){
								temp.setTileType(Database.LEAVES);
							}
						}
						
						temp = getTileAtCoordinates(chunks[j].tiles[i].x, chunks[j].tiles[i].y - maxStemHeight * Tile.tileSize);
						if(temp != null){
							if(temp.type == Database.AIR){
								temp.setTileType(Database.LEAVES);
							}
						}
						
						temp = getTileAtCoordinates(chunks[j].tiles[i].x + Tile.tileSize, chunks[j].tiles[i].y - maxStemHeight * Tile.tileSize);
						if(temp != null){
							if(temp.type == Database.AIR){
								temp.setTileType(Database.LEAVES);
							}
						}
						
						temp = getTileAtCoordinates(chunks[j].tiles[i].x, chunks[j].tiles[i].y - maxStemHeight * Tile.tileSize - Tile.tileSize);
						if(temp != null){
							if(temp.type == Database.AIR){
								temp.setTileType(Database.LEAVES);
							}
						}
					}
				}
			}
		}
	}
	

	// Called when the player clicks on a block with something other than a mining tool or block
	public void specialTileInteraction(int x, int y){
		Tile tile = getTileAtCoordinates(x, y);
		
		// Makes sure the tile exists
		if(tile != null){
			// All special actions that are done with grass blocks
			if(Game.myPlayer.selectedItem == Database.ITEM_GRASS_SEEDS){
				if(tile.type == Database.DIRT){
					// Makes sure the tile above is air
					if(getTileAtCoordinates(x, y - Tile.tileSize).type == Database.AIR){
						tile.setTileType(Database.GRASS);
						Game.myPlayer.removeItem(Database.ITEM_GRASS_SEEDS, 1);
					}
				}
			}
		}
	}
	
	// Called when the player clicks on a block with a mining tool
	public void removeTileAtCoordinates(int x, int y){
		
		double dist = Math.sqrt(Math.pow(((y + Game.cameraOffsetY) - Game.myPlayer.y), 2) + Math.pow(((x + Game.cameraOffsetX) - Game.myPlayer.x), 2));
		
		// If the block is too far away, ignore the request
		if(dist > Game.myPlayer.miningDistance){
			return;
		}
		
		Tile tile = getTileAtCoordinates(x, y); // Gets the tile at mouse position
		
		if(tile != null && tile.type != Database.BEDROCK){
			
			// Takes away from the tile's health
			tile.health -= Game.myPlayer.selectedItem.miningPower;
			
			if(tile.health <= 0){
				
				// Gives the player block drops
				if(tile.type.itemDropIDs[0][0] != -1){
					for(int i = 0; i < tile.type.itemDropIDs[0].length; i++){
						
						int rand = ThreadLocalRandom.current().nextInt(1, 100);
						if(rand < tile.type.itemDropIDs[1][i]){
							Game.myPlayer.addItem(tile.type.itemDropIDs[0][i], 1);
						}
						
					}
				}
				
				tile.type = Database.AIR;
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
		
		for(int i = 0; i < loadedChunks.size(); i++){
			for(int k = 0; k < loadedChunks.get(i).tiles.length; k++){
				java.awt.Rectangle tileRect = new java.awt.Rectangle(loadedChunks.get(i).tiles[k].x, loadedChunks.get(i).tiles[k].y, Tile.tileSize, Tile.tileSize);
				
				if(tileRect.contains(x, y)){
					return loadedChunks.get(i).tiles[k];
				}
			}
		}
		
		return null;
	}
	
}
