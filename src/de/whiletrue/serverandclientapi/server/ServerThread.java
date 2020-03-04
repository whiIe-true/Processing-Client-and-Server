package de.whiletrue.serverandclientapi.server;

import java.io.IOException;
import java.net.ServerSocket;

import de.whiletrue.serverandclientapi.util.events.ClientPacketHandler;
import de.whiletrue.serverandclientapi.util.events.ConnectHandler;
import de.whiletrue.serverandclientapi.util.events.DisconnectHandler;
import de.whiletrue.serverandclientapi.util.events.ErrorHandler;

public class ServerThread extends Thread{

	//Port the server is running on
	private int port;
	//Event handlers
	private DisconnectHandler onDisconnect;
	private ConnectHandler onConnect;
	private ErrorHandler onError;
	
	//Server that is waiting for connection
	private ServerSocket socket;
	
	public ServerThread(int port,ConnectHandler onConnect,DisconnectHandler onDisconnect,ErrorHandler onError){
		this.port=port;
		this.onError=onError;
		this.onConnect=onConnect;
		this.onDisconnect=onDisconnect;
		
		//Sets the name
		this.setName("Serversocket");
		
		//Starts the server
		this.start();
	}
	
	/*
	 * Stops the server
	 * */
	public void stopServer() {
		//Checks if the server is running
		if(this.socket!=null&&!this.socket.isClosed())
			try{
				this.socket.close();
			}catch(IOException e){}
		this.socket=null;
	}
	
	@Override
	public void run(){
		try{
			//Starts the server
			this.socket = new ServerSocket(this.port);
			//Sets the timeout to 2 seconds
			this.socket.setSoTimeout(2000);
		}catch(Exception e){
			//Executes the error handler
			this.onError.execute(e);
		}
		
		try{
			//As long as the server is open
			while(this.socket!=null&&!this.socket.isClosed()) {
				
				try{
					//Waits for a connection
					Connection client = new Connection(this.socket.accept(),this.onDisconnect);
					//Executes the accept method
					ClientPacketHandler handler = this.onConnect.execute(client);
					//Sets the packet handler
					client.setOnPacket(handler);
				}catch(Exception e){}
			}
		}catch(Exception e){}
	}
	
}
