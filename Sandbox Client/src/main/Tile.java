package main;

import java.io.Serializable;

import packets.MapChunkPacket;

public class Tile implements Serializable {
	
	private static final long serialVersionUID = -8806196118460783479L;
	
	public static int tileSize;
	public int x, y;
	public int index; //The index of this tile in Map.tiles
	public float health;
	public Block block;
	public int texture;
	public boolean changed = true;
	
	public Tile(int posX, int posY, Block block) {
		x = posX;
		y = posY;
		setBlock(block);
	}
	
	public void setBlock(Block block){
		this.block = block;
		this.texture = block.texture;
		this.health = block.health;
		
		//If it is multiplayer, send update to server
		if(Game.client != null) 
			Game.client.getServerConnection().sendTcp(new MapChunkPacket(Game.currentMap, index, 1));
	}
}
