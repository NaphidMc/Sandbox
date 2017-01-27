package packets;

import java.io.Serializable;

import main.Player;

/**The player packet is the object that is sent to the server
 * and holds the player's position and other relevant variables. 
 * Why didn't I just send the whole Player over the network?? Because that would
 * mean all variables in Player would have to be Serializable and I do not want to deal with that.
 * Besides, it would be much bigger and all the server really needs to know is the Player's position
 * @author Nathan
 *
 */
public class PlayerPacket implements Serializable {
	
	private static final long serialVersionUID = 336249218917848434L;
	
	public float x, y;
	public int id;
	
	public PlayerPacket(Player player){
		this.x = player.x;
		this.y = player.y;
		this.id = player.ID;
	}
}
