package com.sandbox.client.item;

import com.sandbox.client.Database;
import com.sandbox.client.map.TileType;

public class Item {
	
	public float miningPower;
	public String name;
	public int icon;
	
	public int id;
	
	public TileType block;
	
	public Item(int id, String name, int icon, float miningPower, TileType block) {
		this.name = name;
		this.miningPower = miningPower;
		this.id = id;
		
		this.icon = icon;
		
		this.block = block;
	}
	
	public static Item getItemByID(int id) {
		for(int i = 0; i < Database.items.size(); i++) {
			if(Database.items.get(i).id == id) {
				return Database.items.get(i);
			}
		}
		
		return null;
	}
}
