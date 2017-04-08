package com.sandbox.client.network;

import java.io.Serializable;

import com.sandbox.client.map.Map;
import com.sandbox.client.map.Tile;

// Holds part of a map
public class MapChunkPacket implements Serializable {
	
	private static final long serialVersionUID = -6457402230661740952L;
	
	public Tile[] tiles;
	public int startIndex, length;
	
	public MapChunkPacket(Map map, int start, int length){
		
		this.startIndex = start;
		this.length = length;
		
		tiles = new Tile[length];
		for(int i = 0; i < tiles.length; i++){
			/*if(start + i < map.tiles.length)
				tiles[i] = map.tiles[start + i];
				tiles[i].index = i;*/
		}
	}
}
