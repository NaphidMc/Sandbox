import java.awt.Rectangle;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class InventorySlot {
	
	public static ArrayList<InventorySlot> inventorySlots = new ArrayList<InventorySlot>();
	public boolean isNotCraftingTableOutput = true;
	public static int inventorySlotSize = 68;
	public int x, y;
	public ItemStack itemStack;
	
	public InventorySlot(){
		
		itemStack = new ItemStack(null, 0);
		
		inventorySlots.add(this);
	}
	
	public static InventorySlot getInventorySlotAtPosition(int x, int y){
		
		for(int i = 0; i < inventorySlots.size(); i++){
			Rectangle rect = new Rectangle(inventorySlots.get(i).x, inventorySlots.get(i).y, inventorySlotSize, inventorySlotSize);
			
			if(rect.contains(x, y)){
				return inventorySlots.get(i);
			}
		}
		
		return null;
	}
}
