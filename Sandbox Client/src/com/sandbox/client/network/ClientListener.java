package com.sandbox.client.network;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;
import com.sandbox.client.Game;
import com.sandbox.client.map.Map;
import com.sandbox.client.map.Tile;
import com.sandbox.client.utils.Logger;

// This class listens for connections, disconnections, and incoming packets
public class ClientListener implements SocketListener {
	
	// Called when connected to the server
	@Override
	public void connected(Connection conn) {
		Logger.log("Connected to server");
	}
	
	// Called when disconnected from the server
	@Override
	public void disconnected(Connection conn) {
		
	}

	// Called upon receiving a packet from the server
	@Override
	public void received(Connection conn, Object obj) {
		
		if(obj instanceof Integer){
			Game.myPlayer.ID = (Integer)obj;
		}
		
		if(obj instanceof PlayerPacket){
			PlayerPacket pp = (PlayerPacket)obj;
			if(pp.id != Game.myPlayer.ID){
				Game.players.put(pp.id, pp);
			}
		}
		
		if(obj instanceof MapPacket){
			MapPacket mp = (MapPacket)obj;
			Game.currentMap = new Map();
			Game.currentMap.mapBottonCoordinate = Tile.tileSize * mp.mapHeight;
			Game.currentMap.mapEndCoordinate = Tile.tileSize * mp.mapWidth;
			Game.currentMap.setWidth(mp.mapWidth);
			Game.currentMap.setHeight(mp.mapHeight);
		}
		
		// Disconnects a player
		if(obj instanceof DisconnectNotice){
			DisconnectNotice dn = (DisconnectNotice)obj;
			
			Game.playerIDS.remove(new Integer(dn.id));
			Game.players.remove(dn.id);
		}
		
		if(obj instanceof MapChunkPacket){
			MapChunkPacket mcp = (MapChunkPacket)obj;
			if(Game.currentMap == null){
				System.err.println("MAP CHUNK LOST!!!"); // This should not happen
			} else{
				for(int k = 0; k < Game.currentMap.chunks.length; k++){
					for(int i = 0; i < mcp.length; i++){
						if(Game.currentMap.chunks[k].tiles.length > mcp.startIndex + i){
							Game.currentMap.chunks[k].tiles[mcp.startIndex + i] = mcp.tiles[i];
							
							if(mcp.startIndex + i == Game.currentMap.chunks[k].tiles.length - 1 && !Game.mapLoaded){
								Game.mapLoaded = true;
								Game.currentMap.calculateLightLevels();
							}
						}
					}
				}
			}
		}
	}
}
