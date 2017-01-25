package main;

import java.awt.EventQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

public class GameInit extends ScalableGame {
	
	public GameInit(org.newdawn.slick.Game held, int normalWidth, int normalHeight, boolean maintainAspect) {
		super(held, normalWidth, normalHeight, maintainAspect);
	}
	
	public static AppGameContainer game;
	public static AppGameContainer mainMenu;
	public static Game g;
	public static MainMenu m;
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				
				Database.Populate();
				
				g = new Game("Sandbox");
				m = new MainMenu("Main Menu");
				
				try {
					
					GameInit gi = new GameInit(g, 800, 600, true);
					game = new AppGameContainer(gi);
					game.setDisplayMode(game.getScreenWidth(), game.getScreenHeight(), true);
					game.setFullscreen(true);
					Game.appgc = game;
					
					GameInit mi = new GameInit(m, 800, 600, false);
					mainMenu = new AppGameContainer(mi);
					mainMenu.setDisplayMode(mainMenu.getScreenWidth(), mainMenu.getScreenHeight(), true);
					mainMenu.setFullscreen(true);
					mainMenu.start();
					
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
