import java.awt.Color;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

//TODO: Eventually, this will be the menu and entry point to the application
public class Main extends JFrame {
	
	private static final long serialVersionUID = 4648172894076113183L;

	public static void main1(String[] args){
		
		JFrame frame = new JFrame();
		frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().width/2 - 400, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().height/2 - 300, 800, 600);
		frame.setTitle("Menu");
		frame.setBackground(new Color(100, 149, 237));
		
		JPanel content = new JPanel();
		content.setBounds(0, 0, 800, 600);
		
		
		
		frame.add(content);
		frame.setVisible(true);
		
	}
}
