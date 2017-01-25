package main;

import java.awt.EventQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

public class GameInit extends ScalableGame {
	
	public GameInit(org.newdawn.slick.Game held, int normalWidth, int normalHeight, boolean maintainAspect) {
		super(held, normalWidth, normalHeight, maintainAspect);
	}
	
	public static Game g;
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				
				Database.Populate();
				
				AppGameContainer appgc = null;
				g = new Game("Sandbox", false);

				try {
					GameInit gi = new GameInit(g, 800, 600, true);
					appgc = new AppGameContainer(gi);
					appgc.setDisplayMode(appgc.getScreenWidth(), appgc.getScreenHeight(), true);
					appgc.setFullscreen(true);
					Game.appgc = appgc;
					appgc.start();
					
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
