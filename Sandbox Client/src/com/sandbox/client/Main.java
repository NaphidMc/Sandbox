package com.sandbox.client;

import java.awt.EventQueue;
import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

import com.sandbox.client.Game;

public class Main extends ScalableGame {
	
	public Main(org.newdawn.slick.Game held, int normalWidth, int normalHeight, boolean maintainAspect) {
		super(held, normalWidth, normalHeight, maintainAspect);
	}
	
	public static AppGameContainer appgc;
	public static Game g;
	
	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				
				
				g = new Game("Sandbox");
				
				try {
					
					Main gi = new Main(g, 800, 600, true);
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
