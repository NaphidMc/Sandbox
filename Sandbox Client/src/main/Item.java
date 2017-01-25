package main;
public class Item {
	
	public float MiningPower;
	public String Name;
	public int icon;
	
	public int ID;
	
	public static int IconSize = InventorySlot.inventorySlotSize * 3/4;
	
	public Block block;
	
	public Item(int id, String name, int icon, float miningPower, Block block) {
		Name = name;
		MiningPower = miningPower;
		ID = id;
		
		this.icon = icon;
		
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
