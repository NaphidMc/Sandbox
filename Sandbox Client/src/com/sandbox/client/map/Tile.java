package com.sandbox.client.map;

import java.io.Serializable;

import com.sandbox.client.Game;

public class Tile implements Serializable {
	
	private static final long serialVersionUID = -8806196118460783479L;
	
	public static int tileSize;
	public int x, y;
	public float health;
	public TileType type;
	public int texture;
	public float lightLevel;
	
	public Tile(int posX, int posY, TileType type) {
		x = posX;
		y = posY;
		setTileType(type);
	}
	
	public void setTileType(TileType type){
		this.type = type;
		this.texture = type.texture;
		this.health = type.health;
		
		if(Game.currentMap != null)
			Game.currentMap.calculateLightLevels();	// Recalculates light levels when a block is changed
		
		// TODO: fix multiplayer
		// If it is multiplayer, send update to server
		//if(Game.client != null) 
		// Game.client.getServerConnection().sendTcp(new MapChunkPacket(Game.currentMap, index, 1));
	}
}
