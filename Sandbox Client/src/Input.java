
public class Input {
	
	public void keyPressed(int key, char c){
		
		if(c == 'a'){
			Game.KEY_A_DOWN = true;
		} else if(c == 'd'){
			Game.KEY_D_DOWN = true;
		} else if(c == ' '){
			Game.myPlayer.Jump();
		} else if(key == 1){ //Esc
			System.exit(0);
		}
	}
	
	public void keyReleased(int key, char c){
		if(c == 'a'){
			Game.KEY_A_DOWN = false;
		} else if(c == 'd'){
			Game.KEY_D_DOWN = false;
		}
	}
	
	public void mouseClicked(int button, int x, int y, int clickCount){
		
	}
	
	public void mousePressed(int button, int x, int y){
		
	}
}
