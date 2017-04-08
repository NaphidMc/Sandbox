package com.sandbox.client.map;

import java.io.Serializable;

import com.sandbox.client.utils.Logger;

public class TileType implements Serializable {
	
	private static final long serialVersionUID = -4698775568353685790L;
	
	public int texture;
	public String Name;
	public int[][] itemDropIDs; // Format: { <ItemID>, <ItemID> }, {<Chance to drop item 1>, <Chance to drop item 2>
	public boolean solid;
	public float health;
	
	public TileType(String name, int texture, int[][] itemDropIDs, boolean solid, float health) {
		
		this.texture = texture;
		Name = name;
		
		if(itemDropIDs[0].length != itemDropIDs[1].length){
			Logger.logError("Not all block drop ids have a corresponding drop chance!");   
		}
		
		this.itemDropIDs = new int[itemDropIDs.length][itemDropIDs[0].length];
		for(int i = 0; i < itemDropIDs[0].length; i++){
			this.itemDropIDs[0][i] = itemDropIDs[0][i];
			this.itemDropIDs[1][i] = itemDropIDs[1][i];
		}
		
		this.health = health;
		this.solid = solid;
	}
	
	@Override
	public boolean equals(Object obj){
		
		if(obj == null)
			return false;
		
		if(obj instanceof TileType){
			TileType block = (TileType)obj;
			if(this.Name.equals(block.Name) && this.texture == block.texture){
				return true;
			}
		}
		
		return false;
	}
	
}
