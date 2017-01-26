package main;

import java.awt.Rectangle;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class MainMenu {

	//Below are the rectangles that are used as the buttons for the menu
	public Rectangle singlePlayerButton;
	public Rectangle multiPlayerButton;
	public Rectangle quitButton;
	
	boolean init;
	
	public MainMenu() {
		
		//Sets up the menu button rectangles
		singlePlayerButton = new Rectangle(200, 100, 150, 60);
		multiPlayerButton = new Rectangle(200, 200, 150, 60);
		quitButton = new Rectangle(200, 300, 150, 60);
		
		init = true;
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		
		//Draws background color
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, 800, 600);
		
		//Single-Player button
		g.setColor(Color.darkGray);
		g.fillRect(singlePlayerButton.x, singlePlayerButton.y, (float)singlePlayerButton.getWidth(), (float)singlePlayerButton.getHeight());
		g.setColor(Color.white);
		g.drawString("Singleplayer", singlePlayerButton.x + .5f * (float) singlePlayerButton.getHeight(), (float) singlePlayerButton.y + 25); 
		
		//Multi-Player button
		g.setColor(Color.darkGray);
		g.fillRect(multiPlayerButton.x, multiPlayerButton.y, (float)multiPlayerButton.getWidth(), (float)multiPlayerButton.getHeight());
		g.setColor(Color.white);
		g.drawString("Multiplayer", multiPlayerButton.x + .5f * (float) multiPlayerButton.getHeight(), (float) multiPlayerButton.y + 25); 
		
		//Quit Button
		g.setColor(Color.darkGray);
		g.fillRect(quitButton.x, quitButton.y, (float)quitButton.getWidth(), (float)quitButton.getHeight());
		g.setColor(Color.white);
		g.drawString("Quit", quitButton.x + .5f * (float) quitButton.getHeight(), (float) quitButton.y + 25); 
	
	}
	
	public void mousePressed(int button, int x, int y){
		
		if(!init)
			return;
		
		//Left click
		if(button == 0){
			
			//Checks if your mouse was inside the button's bounds when you clicked
			if(singlePlayerButton.contains(x, y)){
				launchGame(false);
			} 
			else if(multiPlayerButton.contains(x, y)){
				launchGame(true);
			} 
			else if(quitButton.contains(x, y)){
				System.exit(0); //Quit button
			}
		}
		
	}
	
	public void launchGame(boolean multiplayer){
		
		if(multiplayer)
			Game.current.startMultiplayer();
		
		Game.currentGameState = Game.GameState.Game;
	}
}
