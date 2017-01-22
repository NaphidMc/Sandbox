import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class InventorySlot {
	
	public static ArrayList<InventorySlot> inventorySlots = new ArrayList<InventorySlot>();
	public static Image texture;
	public static Image selectedTexture;
	public boolean isNotCraftingTableOutput = true;
	public static int inventorySlotSize = 100;
	public int x, y;
	public ItemStack itemStack;
	
	public InventorySlot(){
		
		//Sets the textures for the hotbar
		if(texture == null || selectedTexture == null){
			try{
				System.out.println("Hotbar texture set");
				texture = ImageIO.read(new File("resources/Hotbar Slot.png")).getScaledInstance(inventorySlotSize, inventorySlotSize, 1);       
			} catch (Exception e) {
				try{
					System.out.println("Hotbar texture not found!");
					texture = ImageIO.read(new File("resources/default.png")).getScaledInstance(inventorySlotSize, inventorySlotSize, 1);  
				}
				catch (Exception e1){
					texture = null;
				}
			}
			
			try{
				System.out.println("Hotbar selected texture set");
				selectedTexture = ImageIO.read(new File("resources/Hotbar Slot(Selected).png")).getScaledInstance(inventorySlotSize, inventorySlotSize, 1);  
			} catch (Exception e) {
				try{
					System.out.println("Hotbar selected texture not found");
					selectedTexture = ImageIO.read(new File("resources/default.png")).getScaledInstance(inventorySlotSize, inventorySlotSize, 1);  
				}
				catch (Exception e1){
					selectedTexture = null;
				}
			}
		}
		
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
