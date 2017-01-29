package main;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
	
	public Tile[] tiles;
	public int mapEndCoordinate;
	public int mapBottonCoordinate;
	private int mapWidth, mapHeight;
	
	public Map() { }
	
	public Map(int mapWidth, int mapHeight){
		
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;

		tiles = new Tile[mapWidth * mapHeight];
		
		Date start = null;
		Date finish = null;
		long timeElapsed = 0;
		
		System.out.print("Generating tiles... ");
		start = new Date();
		generateTiles(7, 13, 2, 5, 2, 4, 12, 1, 13, .5f); // This confusing mess does most of the generation
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		System.out.print("Done! (" + timeElapsed + "ms)\n");
		
		System.out.print("Fixing grass blocks... ");
		start = new Date();
		fixGrassBlocks(); // This function makes it so grass blocks can't have blocks on top of them
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		System.out.print("Done! (" + timeElapsed + "ms)\n");
		
		System.out.print("Placing trees... ");
		start = new Date();
		generateTrees(2f); // Generates the trees; the parameter is tree density
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		System.out.print("Done! (" + timeElapsed + "ms)\n");
		
		System.out.print("Growing trees... ");
		start = new Date();
		growTrees(); // Adds leaves and stems to the trees
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		System.out.print("Done! (" + timeElapsed + "ms)\n");
		
		System.out.print("Calculating light levels... ");
		start = new Date();
		calculateLightLevels();
		finish = new Date();
		timeElapsed = finish.getTime() - start.getTime();
		System.out.print("Done! (" + timeElapsed + "ms)\n");
	}
	
	/**
	 * Returns the width in tiles of the map
	 */
	public int getWidth(){
		return mapWidth;
	}
	
	/**
	 * Returns the height in tiles of the map
	 */
	public int getHeight(){
		return mapHeight;
	}
	
	public void setWidth(int value){
		this.mapWidth = value;
	}
	
	public void setHeight(int value){
		this.mapHeight = value;
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
	public void generateTiles(int groundLevel, int hills, int minHillHeight, int maxHillHeight, int minHillWidth, int maxHillWidth, int stoneDepth, int stoneTransition, int ironDepth, float ironFrequencyMultiplier){
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
			
			int currentX = Tile.tileSize * width;
			int currentY = Tile.tileSize * height;
			
			// Checks if the current tile is part of a hill
			for(int k = 0; k < mapHills.length; k++){
				
				for(int j = 0; j < mapHills[k].hillTiles.size(); j++){
					
					if(mapHills[k].hillTiles.get(j).x == width && mapHills[k].hillTiles.get(j).y == height){

						if(mapHills[k].hillTiles.get(j).topTile){
							tiles[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
						}else{
							tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
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
				tiles[i] = new Tile(currentX, currentY, Database.BLOCK_AIR);
			} else if(height == groundLevel){
				// Top grass layer
				tiles[i] = new Tile(currentX, currentY, Database.BLOCK_GRASS);
			} else{
				if(height != mapHeight - 1){
					// Most generation stuff goes here
					int random = ThreadLocalRandom.current().nextInt(1, 101);
					
					if(!(height <= stoneDepth) && !(height >= stoneDepth - stoneTransition)) {
						// Dirt layer
						
						tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
					} else if(height <= stoneDepth && height >= stoneDepth - stoneTransition) {    
						// Stone transitional layer
						
						if(random <= 55){
							tiles[i] = new Tile(currentX, currentY, Database.BLOCK_STONE);
						}
						else{
							tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
						}
					} else if(height > stoneDepth){
						// Stone layer
						
						random = ThreadLocalRandom.current().nextInt(0, 101);
						
						float ironChance = 0.0f;
						if(height > ironDepth){
							ironChance = (height - ironDepth)*ironFrequencyMultiplier*10;
							
							if(ironChance > 10){
								ironChance = 10;
							}
						}
						
						if(random < ironChance){
							tiles[i] = new Tile(currentX, currentY, Database.BLOCK_IRONORE);
						}else{
						tiles[i] = new Tile(currentX, currentY, Database.BLOCK_STONE);
						}
					}
				}
				else{
					tiles[i] = new Tile(currentX, currentY, Database.BLOCK_BEDROCK);
				}
			}
			
			width++;
			if((i + 1)%mapWidth == 0){
				height++;
				width = 0;
			}
			
			if(tiles[i] == null)
			{
				tiles[i] = new Tile(currentX, currentY, Database.BLOCK_DIRT);
			}
		}
	}
	
	public void calculateLightLevels(){
		
		HashMap<String, Tile> tileCache = new HashMap<String, Tile>();
		
		for(int i = 0; i < tiles.length; i++){
			tiles[i].lightLevel = 0;
		}
		
		int index = 0;
		for(int i = 0; i < mapHeight; i++){
			for(int k = 0; k < mapWidth; k++){
				
				Tile above = null, below = null, right = null, left = null;
				
				if((above = tileCache.get(tiles[index].x + "," + (tiles[index].y - Tile.tileSize))) == null){
					above = getTileAtCoordinates(tiles[index].x, tiles[index].y - Tile.tileSize);
					tileCache.put(tiles[index].x + "," + (tiles[index].y - Tile.tileSize), above);
				}
				if((below = tileCache.get(tiles[index].x + "," + (tiles[index].y + Tile.tileSize))) == null){
					below = getTileAtCoordinates(tiles[index].x, tiles[index].y + Tile.tileSize); 
					tileCache.put(tiles[index].x + "," + (tiles[index].y + Tile.tileSize), below);
				}
				if((right = tileCache.get((tiles[index].x + Tile.tileSize) + "," + tiles[index].y)) == null){
					right = getTileAtCoordinates(tiles[index].x + Tile.tileSize, tiles[index].y); 
					tileCache.put((tiles[index].x + Tile.tileSize) + "," + tiles[index].y, right);
				}
				if((left = tileCache.get((tiles[index].x - Tile.tileSize) + "," + tiles[index].y)) == null){
					left = getTileAtCoordinates(tiles[index].x - Tile.tileSize, tiles[index].y);
					tileCache.put((tiles[index].x - Tile.tileSize) + "," + tiles[index].y, left);
				}
				
				if(tiles[index].block.equals(Database.BLOCK_AIR) || tiles[index].block.equals(Database.BLOCK_WOOD) || tiles[index].block.equals(Database.BLOCK_LEAVES)){
					tiles[index].lightLevel = 2.0f;
				}
			
			if(above != null){
				above.lightLevel += tiles[index].lightLevel/4f;
			}
			if(below != null){
				below.lightLevel += tiles[index].lightLevel/4f;
			}
			if(right != null){
				right.lightLevel += tiles[index].lightLevel/4f;
			}
			if(left != null){
				left.lightLevel += tiles[index].lightLevel/4f;
			}
				
				index++;
			}
		}
	}
	
	/**
	 * Checks if any grass blocks have a block above them,
	 * and if they do, the grass block is changed to dirt
	 */
	public void fixGrassBlocks() {
		for(int i = 0; i < tiles.length; i++){
			if(tiles[i].block == Database.BLOCK_GRASS) {
				Block block = getTileAtCoordinates(tiles[i].x, tiles[i].y - Tile.tileSize).block;
				if(block != null){
					if(block != Database.BLOCK_AIR && block.solid == true) {     
						tiles[i].setBlock(Database.BLOCK_DIRT);
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
		for(int i = 0; i < tiles.length; i++){
			if(tiles[i].block == Database.BLOCK_GRASS){
				int random = ThreadLocalRandom.current().nextInt(1, 101);
				
				if(random < treeChance){
					// Make a tree
					getTileAtCoordinates(tiles[i].x, tiles[i].y - Tile.tileSize).setBlock(Database.BLOCK_SAPLING);
				} else{
					// treeChance += (5*treeDensity);
				}
			}
		}
	}
	
	/**
	 * Replaces all saplings with fully grown trees
	 */
	public void growTrees(){
		int treeGrowChance = 101;
		TileLoop: for(int i = 0; i < tiles.length; i++){
			if(tiles[i].block == Database.BLOCK_SAPLING){
				int rand = ThreadLocalRandom.current().nextInt(1, 101);
				if(rand <= treeGrowChance){
					int stemHeight = ThreadLocalRandom.current().nextInt(1, 5);
					
					// Grow tree
					tiles[i].setBlock(Database.BLOCK_WOOD);
					
					int maxStemHeight = 0;
					for(int k = 1; k < stemHeight; k++){
						Tile tile = getTileAtCoordinates(tiles[i].x, tiles[i].y - Tile.tileSize*k);
						if(tile != null){
							if(tile.block == Database.BLOCK_AIR){
								tile.setBlock(Database.BLOCK_WOOD);
							} else {
								continue TileLoop;
							}
						}
						
						maxStemHeight = k;
					}
					
					// Leaves
					Tile temp;
					
					temp = getTileAtCoordinates(tiles[i].x - Tile.tileSize, tiles[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(tiles[i].x, tiles[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(tiles[i].x + Tile.tileSize, tiles[i].y - maxStemHeight * Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
						}
					}
					
					temp = getTileAtCoordinates(tiles[i].x, tiles[i].y - maxStemHeight * Tile.tileSize - Tile.tileSize);
					if(temp != null){
						if(temp.block == Database.BLOCK_AIR){
							temp.setBlock(Database.BLOCK_LEAVES);
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
				if(tile.block == Database.BLOCK_DIRT){
					// Makes sure the tile above is air
					if(getTileAtCoordinates(x, y - Tile.tileSize).block == Database.BLOCK_AIR){
						tile.setBlock(Database.BLOCK_GRASS);
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
		
		if(tile != null && tile.block != Database.BLOCK_BEDROCK){
			
			// Takes away from the tile's health
			tile.health -= Game.myPlayer.selectedItem.MiningPower;
			
			if(tile.health <= 0){
				
				// Gives the player block drops
				if(tile.block.itemDropIDs[0][0] != -1){
					for(int i = 0; i < tile.block.itemDropIDs[0].length; i++){
						
						int rand = ThreadLocalRandom.current().nextInt(1, 100);
						if(rand < tile.block.itemDropIDs[1][i]){
							Game.myPlayer.addItem(tile.block.itemDropIDs[0][i], 1);
						}
						
					}
				}
				
				tile.block = Database.BLOCK_AIR;
			}
		}
	}
	
	// Called when the player clicks on a block with a block selected in the Game.myPlayer.Hotbar
	public void placeBlockAtCoordinates(int x, int y){
		Tile tile = getTileAtCoordinates(x, y);
		if(tile != null){
			if(tile.block == Database.BLOCK_AIR){
				tile.setBlock(Game.myPlayer.selectedItem.block);
				Game.myPlayer.removeItem(Game.myPlayer.selectedItem, 1);
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
		for(int i = 0; i < tiles.length; i++){
			java.awt.Rectangle tileRect = new java.awt.Rectangle(tiles[i].x, tiles[i].y, Tile.tileSize, Tile.tileSize);
			
			if(tileRect.contains(x, y)){
				return tiles[i];
			}
		}
		
		return null;
	}

	
}
