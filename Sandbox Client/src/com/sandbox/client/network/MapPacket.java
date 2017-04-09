package com.sandbox.client.network;

import java.io.Serializable;

import com.sandbox.client.map.Map;

// The map packet class just holds the map's height and width
public class MapPacket implements Serializable {
	
	private static final long serialVersionUID = 1496087825181574563L;
	
	public int mapWidth, mapHeight;
	
	public MapPacket(Map map){
		mapHeight = Map.getHeight();
		mapWidth = Map.getWidth();
	}
}
