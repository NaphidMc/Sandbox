import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class Item {
	
	public float MiningPower;
	public String Name;
	public Image icon;
	
	public int ID;
	
	public static int IconSize = InventorySlot.inventorySlotSize * 3/4;
	
	public Block block;
	
	public Item(int id, String name, String icon, float miningPower, Block block) {
		Name = name;
		MiningPower = miningPower;
		ID = id;
		
		try{
			this.icon = ImageIO.read(new File(icon)).getScaledInstance(IconSize, IconSize, 1);      
		} catch (Exception e){
			System.out.println("Item texture not found: " + name);
			try{
				this.icon = ImageIO.read(new File("resources/default.png")).getScaledInstance(IconSize, IconSize, 1);
			} catch(Exception e1){
				this.icon = null;
			}
		}
		
		this.block = block;
	}
	
	public static Item getItemByID(int id){
		for(int i = 0; i < Database.items.size(); i++){
			if(Database.items.get(i).ID == id){
				return Database.items.get(i);
			}
		}
		
		return null;
	}
}
