package com.sandbox.client.rendering;

import org.newdawn.slick.Graphics;

import com.sandbox.client.Game;
import com.sandbox.client.network.PlayerPacket;

public class EntityRenderer {

	public static void renderPlayers(Graphics g) {
		
		// Draws other players
		for(java.util.Map.Entry<Integer, PlayerPacket> entry : Game.players.entrySet()){
			PlayerPacket pp = entry.getValue();
			Game.spritesheet.renderInUse((int)pp.x + (int)Game.cameraOffsetX, (int)pp.y - (int)Game.cameraOffsetY, 9, 0);
		}
		
		//  Draws your player
		Game.spritesheet.renderInUse((int)Game.myPlayer.x + (int)Game.cameraOffsetX, (int)Game.myPlayer.y - (int)Game.cameraOffsetY, Game.myPlayer.textureX, Game.myPlayer.textureY);
		
		Game.spritesheet.endUse();
		
	}
}
