import java.awt.Image;

public class Tile {
	
	public static int tileSize = 100;
	public static int currentTileNumber = 0;
	public int id;
	public int x, y;
	public float health;
	public Block block;
	public Image texture;
	public boolean changed = true;
	
	public Tile(int posX, int posY, Block block) {
		x = posX;
		y = posY;
		setBlock(block);
		id = currentTileNumber;
		currentTileNumber++;
	}
	
	public void setBlock(Block block){
		this.block = block;
		this.texture = block.texture;
		this.health = block.health;
	}
}
