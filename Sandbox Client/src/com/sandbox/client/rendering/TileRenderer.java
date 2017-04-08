package com.sandbox.client.rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.sandbox.client.Database;
import com.sandbox.client.Game;
import com.sandbox.client.map.Map;
import com.sandbox.client.map.Tile;

public class TileRenderer {

	public static void renderTiles(Graphics g) {
		// Loops through all the tiles and draws them
		Game.spritesheet.startUse();
		int mapIndex = 0;
		int xpos = 0;
		int ypos = 0;
		//Loops through all the chunks
		for(int k = 0; k < Game.currentMap.loadedChunks.size(); k++){
			mapIndex = 0;
			ypos = 0;
			xpos = Game.currentMap.loadedChunks.get(k).chunkIndex * Map.chunkSize;
			//Loops through all tiles in the chunk
			for(int i = 0; i < Game.currentMap.loadedChunks.get(k).tiles.length; i++) {
				
				try {
					// Updates tile coordinates
					Game.currentMap.chunks[Game.currentMap.loadedChunks.get(k).chunkIndex].tiles[mapIndex].x = Tile.tileSize * xpos;
					Game.currentMap.chunks[Game.currentMap.loadedChunks.get(k).chunkIndex].tiles[mapIndex].y = Tile.tileSize * ypos;
					
					// If the tile is 'air' it simply isn't drawn
					if (Game.currentMap.loadedChunks.get(k).tiles[mapIndex].type.equals(Database.AIR)) {
						
						mapIndex++;
						xpos++;
						if((xpos) == Map.chunkSize * (Game.currentMap.loadedChunks.get(k).chunkIndex + 1)){
							ypos++;
							xpos = Game.currentMap.loadedChunks.get(k).chunkIndex * Map.chunkSize;
						}
						continue;
					
					} else {
						// Before drawing a tile, it checks if it is visible
						if(Tile.tileSize * xpos + (int)Game.cameraOffsetX > -Tile.tileSize + 0 && Tile.tileSize * xpos + (int)Game.cameraOffsetX < 800 && Tile.tileSize * ypos - (int)Game.cameraOffsetY > 0 -Tile.tileSize && Tile.tileSize * ypos - (int) Game.cameraOffsetY < 600){
							// Finally, this draws the tile + shading for a shadow effect
							new Color(Game.currentMap.loadedChunks.get(k).tiles[mapIndex].lightLevel, Game.currentMap.loadedChunks.get(k).tiles[mapIndex].lightLevel, Game.currentMap.loadedChunks.get(k).tiles[mapIndex].lightLevel, 1f).bind();
							Game.spritesheet.renderInUse(0 + Tile.tileSize * xpos + (int)Game.cameraOffsetX, 0 + Tile.tileSize * ypos - (int)Game.cameraOffsetY, Game.currentMap.loadedChunks.get(k).tiles[mapIndex].texture%Game.SPRITESHEET_WIDTH, Game.currentMap.loadedChunks.get(k).tiles[mapIndex].texture/Game.SPRITESHEET_WIDTH);
						}
					}
					
				} catch (Exception e) { }
				
				mapIndex++;
				xpos++;
				if((xpos) == (Map.chunkSize * (Game.currentMap.loadedChunks.get(k).chunkIndex + 1))){
					ypos++;
					xpos = Game.currentMap.loadedChunks.get(k).chunkIndex * Map.chunkSize;
				}
			}
		}
		Color.white.bind();
	}
}
