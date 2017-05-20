package com.sandbox.client;

import java.awt.Rectangle;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.sandbox.client.map.Tile;

public class MainMenu {

	// Below are the rectangles that are used as the buttons for the menu
	private static Rectangle singlePlayerButton;
	private static Rectangle quitButton;
	
	private static boolean init;
	
	public MainMenu() {
		
		// Sets up the menu button rectangles
		singlePlayerButton = new Rectangle(200, 225, 325, 55);
		quitButton = new Rectangle(200, 300, 325, 55);
		
		init = true;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
		// Draws background color
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, 800, 600);
		
		// Background tile blocks
		Game.spritesheet.startUse();
		for(int i = 0; i < 67; i++) {
			for(int k = 0; k < 50; k++) {
				Game.spritesheet.renderInUse(i * Tile.tileSize, k * Tile.tileSize, 8, 1);
			}
		}
		Game.spritesheet.endUse();
		
		// Single-Player button
		// Button changes on mouse over
		if(singlePlayerButton.contains(Input.mouseX, Input.mouseY)) {
				Game.spritesheet.getSubImage(7, 1).draw(singlePlayerButton.x + 15, singlePlayerButton.y, 325, 275);
				Game.spritesheet.getSubImage(5, 1).draw(singlePlayerButton.x + 15, singlePlayerButton.y, 325, 275);
		} else {
			Game.spritesheet.getSubImage(5, 1).draw(singlePlayerButton.x, singlePlayerButton.y, 325, 275);
		}
		
		// Quit button
		// Button changes on mouse over
		if(quitButton.contains(Input.mouseX, Input.mouseY)) {
				Game.spritesheet.getSubImage(7, 1).draw(quitButton.x + 15, quitButton.y, 325, 275);
				Game.spritesheet.getSubImage(6, 1).draw(quitButton.x + 15, quitButton.y, 325, 275);
		} else {
			Game.spritesheet.getSubImage(6, 1).draw(quitButton.x, quitButton.y, 325, 275);
		}
		
		// Logo
		Game.image_logo.draw(160, 45, 450, 275);
	}
	
	public static void mousePressed(int button, int x, int y){
		
		if(!init)
			return;
		
		// Left click
		if(button == 0){
			
			// Checks if your mouse was inside the button's bounds when you clicked
			if(singlePlayerButton.contains(x, y)){
				launchGame(false);
			} else if(quitButton.contains(x, y)){
				System.exit(0); // Quit button
			}
		}
		
	}
	
	public static void launchGame(boolean multiplayer){
		
		if(multiplayer)
			Game.current.startMultiplayer();
		else
			Game.current.startSinglePlayer();
		
		Game.currentGameState = Game.GameState.Game;
	}
}
