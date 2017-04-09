package com.sandbox.client.rendering;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.sandbox.client.Game;
import com.sandbox.client.map.Parallax;

/**
 * Renders stuff such as parallaxes and the sky
 * @author Nathan
 */
public class EnvironmentRenderer {
	
	public static void renderSky(Graphics g, Color skyColor) {
		// Draws the sky
		g.setColor(skyColor);
		g.fillRect(0, 0, 800, 600);
	}
	
	public static void renderParallaxes() {
		Game.parallaxsheet.startUse();
		for(Map.Entry<Integer, Parallax> entry : Game.currentMap.parallaxes.entrySet()) {
			Game.parallaxsheet.renderInUse(entry.getValue().x(), entry.getValue().y() - (int) Game.cameraOffsetY, entry.getValue().tx, entry.getValue().ty);
		}
		Game.parallaxsheet.endUse();
	}
}
