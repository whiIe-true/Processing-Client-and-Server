package de.whiletrue.serverandclientapi.server;

import java.net.Socket;

import de.whiletrue.serverandclientapi.packets.ServerPacket;
import de.whiletrue.serverandclientapi.util.events.ClientPacketHandler;
import de.whiletrue.serverandclientapi.util.events.DisconnectHandler;;

public class Connection{

	//Connection thread
	private ConnectionThread thread;
	//Event for the packet
	private ClientPacketHandler onPacket;
	
	public Connection(Socket socket,DisconnectHandler onDisconnect){
		//Starts the connection thread
		this.thread = new ConnectionThread(
				socket,
				()->onDisconnect.execute(this),
				this.onPacket
				);
	}
	
	/*
	 * Closes the connection
	 * */
	public void disconnect() {
		this.thread.closeConnection();
	}
	
	/**
	 * Sends the given packet to the client
	 * 
	 * @param packet the packet to send
	 * */
	public void sendPacket(ServerPacket packet) {
		this.thread.sendPacket(packet);
	}
	
	public void setOnPacket(ClientPacketHandler onPacket){
		this.onPacket=onPacket;
		this.thread.setOnPacket(onPacket);
	}
}
