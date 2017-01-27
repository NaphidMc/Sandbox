package main;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import packets.DisconnectNotice;
import packets.MapChunkPacket;
import packets.MapPacket;
import packets.PlayerPacket;

//This class listens for connections, disconnections, and incoming packets
public class ClientListener implements SocketListener {
	
	//Called when connected to the server
	@Override
	public void connected(Connection conn) {
		System.out.println("Connected to server...");
	}
	
	//Called when disconnected from the server
	@Override
	public void disconnected(Connection conn) {
		
	}

	//Called upon receiving a packet from the server
	@Override
	public void received(Connection conn, Object obj) {
		
		if(obj instanceof Integer){
			Game.myPlayer.ID = (Integer)obj;
		}
		
		if(obj instanceof PlayerPacket){
			PlayerPacket pp = (PlayerPacket)obj;
			if(pp.id != Game.myPlayer.ID){
				Game.current.players.put(pp.id, pp);
			}
		}
		
		if(obj instanceof MapPacket){
			MapPacket mp = (MapPacket)obj;
			Game.currentMap = new Map();
			Game.currentMap.mapBottonCoordinate = Tile.tileSize * mp.mapHeight;
			Game.currentMap.mapEndCoordinate = Tile.tileSize * mp.mapWidth;
			Game.currentMap.tiles = new Tile[mp.mapWidth * mp.mapHeight];
			Game.currentMap.setWidth(mp.mapWidth);
			Game.currentMap.setHeight(mp.mapHeight);
		}
		
		//Disconnects a player
		if(obj instanceof DisconnectNotice){
			DisconnectNotice dn = (DisconnectNotice)obj;
			
			Game.current.playerIDS.remove(new Integer(dn.id));
			Game.current.players.remove(dn.id);
		}
		
		if(obj instanceof MapChunkPacket){
			MapChunkPacket mcp = (MapChunkPacket)obj;
			if(Game.currentMap == null){
				System.err.println("MAP CHUNK LOST!!!"); //This should not happen
			} else{
				for(int i = 0; i < mcp.length; i++){
					if(Game.currentMap.tiles.length > mcp.startIndex + i){
						Game.currentMap.tiles[mcp.startIndex + i] = mcp.tiles[i];
						
						if(mcp.startIndex + i == Game.currentMap.tiles.length - 1 && !Game.current.mapLoaded)
							Game.current.mapLoaded = true;
					}
				}
			}
		}
	}
}
