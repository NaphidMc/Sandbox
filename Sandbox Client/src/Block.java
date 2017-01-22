import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class Block {
	
	public Image texture;
	public String Name;
	public int[][] itemDropIDs; //Format: { <ItemID>, <ItemID> }, {<Chance to drop item 1>, <Chance to drop item 2>
	public String imagePath;
	public boolean solid;
	public float health;
	
	public Block(String name, String imagePath, int[][] itemDropIDs, boolean solid, float health) {
		try{
			texture = ImageIO.read(new File(imagePath)).getScaledInstance(Tile.tileSize, Tile.tileSize, 1);
		}
		catch (Exception e){
			System.out.println("Failed to load block texture!");
			try{
				texture = ImageIO.read(new File("resources/default.png")).getScaledInstance(Tile.tileSize, Tile.tileSize, 1);;
			} catch (Exception e1) { texture = null; }
		}
		
		this.imagePath = imagePath;
		Name = name;
		
		if(itemDropIDs[0].length != itemDropIDs[1].length){
			System.out.println("Hey...we have a problem...not all block drop ids have a correspoding drop chance. \n There will be a crash in your near future :( ");    
		}
		
		this.itemDropIDs = new int[itemDropIDs.length][itemDropIDs[0].length];
		for(int i = 0; i < itemDropIDs[0].length; i++){
			this.itemDropIDs[0][i] = itemDropIDs[0][i];
			this.itemDropIDs[1][i] = itemDropIDs[1][i];
		}
		
		this.health = health;
		this.solid = solid;
	}
	
}
