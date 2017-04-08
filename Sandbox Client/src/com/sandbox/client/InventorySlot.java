package com.sandbox.client;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.sandbox.client.item.ItemStack;
import com.sandbox.client.rendering.UIRenderer;

public class InventorySlot {
	
	public static ArrayList<InventorySlot> inventorySlots = new ArrayList<InventorySlot>();
	public boolean isNotCraftingTableOutput = true;
	public int x, y;
	public ItemStack itemStack;
	
	public InventorySlot(){
		
		itemStack = new ItemStack(null, 0);
		
		inventorySlots.add(this);
	}
	
	public static InventorySlot getInventorySlotAtPosition(int x, int y){
		
		for(int i = 0; i < inventorySlots.size(); i++){
			Rectangle rect = new Rectangle(inventorySlots.get(i).x, inventorySlots.get(i).y, UIRenderer.inventorySlotSize, UIRenderer.inventorySlotSize);
			
			if(rect.contains(x, y)){
				return inventorySlots.get(i);
			}
		}
		
		return null;
	}
}
