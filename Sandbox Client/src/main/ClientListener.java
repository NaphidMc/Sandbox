package main;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

import packets.PlayerPacket;

//This class listens for connections, disconnections, and incoming packets
public class ClientListener implements SocketListener {

	//Called when connected to the server
	@Override
	public void connected(Connection conn) {
		System.out.println("Connected to server...");
	}
	
	//Called when disconnected from the server
	@Override
	public void disconnected(Connection conn) {
		
	}

	//Called upon receiving a packet from the server
	@Override
	public void received(Connection conn, Object obj) {
		if(obj instanceof Integer){
			Game.myPlayer.ID = (Integer)obj;
			conn.sendTcp(new PlayerPacket(Game.myPlayer));
		}
	}
}
