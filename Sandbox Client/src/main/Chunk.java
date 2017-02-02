package main;

public class Chunk {
	
	public Tile[] tiles;
	private int index;
	public int chunkIndex = 0;
	
	public Chunk(int size){
		tiles = new Tile[size * Map.getHeight()];
	}
	
	public void addTile(Tile tile){
		tiles[index] = tile;
		index++;
	}
}
