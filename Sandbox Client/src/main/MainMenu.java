package main;

import java.awt.Rectangle;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class MainMenu extends BasicGame {

	//Below are the rectangles that are used as the buttons for the menu
	public Rectangle singlePlayerButton;
	public Rectangle multiPlayerButton;
	public Rectangle quitButton;
	
	public MainMenu(String title) {
		super(title);
		
		//Sets up the menu button rectangles
		singlePlayerButton = new Rectangle(200, 100, 150, 60);
		multiPlayerButton = new Rectangle(200, 170, 150, 60);
		quitButton = new Rectangle(200, 140, 150, 60);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		
		//Draws background color
		g.setColor(Color.lightGray);
		g.drawRect(0, 0, 800, 600);
		
		g.setColor(Color.darkGray);
		
		//Single-Player button
		g.drawRect(singlePlayerButton.x, singlePlayerButton.y, (float)singlePlayerButton.getWidth(), (float)singlePlayerButton.getHeight());
		g.drawString("Singleplayer", singlePlayerButton.x + .5f * (float) singlePlayerButton.getHeight(), (float) singlePlayerButton.y); 
		
		//Multi-Player button
		g.drawRect(multiPlayerButton.x, multiPlayerButton.y, (float)multiPlayerButton.getWidth(), (float)multiPlayerButton.getHeight());
		g.drawString("Multiplayer", multiPlayerButton.x + .5f * (float) multiPlayerButton.getHeight(), (float) multiPlayerButton.y); 
		
		//Quit Button
		g.drawRect(quitButton.x, quitButton.y, (float)quitButton.getWidth(), (float)quitButton.getHeight());
		g.drawString("Quit", quitButton.x + .5f * (float) quitButton.getHeight(), (float) quitButton.y); 
	
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
	}

	@Override
	public void mousePressed(int button, int x, int y){
		
		//Left click
		if(button == 0){
			
			//Checks if your mouse was inside the button's bounds when you clicked
			if(singlePlayerButton.contains(x, y)){
				launchGame(true);
			} 
			else if(multiPlayerButton.contains(x, y)){
				launchGame(false);
			} 
			else if(quitButton.contains(x, y)){
				System.exit(0); //Quit button
			}
		}
		
	}
	
	public void launchGame(boolean singlePlayer){
		
		GameInit.mainMenu.pause(); //Quits the main menu
		
		try {
			GameInit.game.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
