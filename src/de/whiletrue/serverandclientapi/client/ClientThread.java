package de.whiletrue.serverandclientapi.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import de.whiletrue.serverandclientapi.packets.ClientPacket;
import de.whiletrue.serverandclientapi.packets.ServerPacket;
import de.whiletrue.serverandclientapi.util.events.ConnectionClientEndHandler;
import de.whiletrue.serverandclientapi.util.events.ServerPacketHandler;
import de.whiletrue.serverandclientapi.util.events.ConnectionClientEndHandler.EnumReason;

public class ClientThread extends Thread{

	//Ip and port
	private String server;
	private int port;
	//Connection
	private Socket socket;
	//Reader and writer
	private DataOutputStream writer;
	private DataInputStream reader;
	//Handlers for the events
	private ConnectionClientEndHandler onConnectionEnd;
	private ServerPacketHandler onPacket;
	
	public ClientThread(String server,int port,ConnectionClientEndHandler onConnectionEnd,ServerPacketHandler onPacket){
		this.server=server;
		this.port=port;
		this.onConnectionEnd=onConnectionEnd;
		this.onPacket=onPacket;
		
		//Sets the name
		this.setName("Clientsocket");
		
		//Starts the client
		this.start();
	}
	
	@Override
	public void run(){
		try{
			//Connects to the server
			this.socket = new Socket(this.server,this.port);
			
			//Creates the reader and writer
			this.reader=new DataInputStream(this.socket.getInputStream());
			this.writer=new DataOutputStream(this.socket.getOutputStream());
		}catch(Exception e) {
			//Checks if a connection end event got set
			if(this.onConnectionEnd!=null)
				//Executes the disconnect event
				this.onConnectionEnd.execute(EnumReason.CONNECT_FAILED);
			return;
		}
		try{
			//Waits for packets
			while(!this.isInterrupted()) {
				//Gets the packet type
				byte type=this.reader.readByte();

				//Gets the length in bytes
				byte[] len={this.reader.readByte(),this.reader.readByte()};

				//Gets the length as a short
				short length=(short)(((len[0]&0xFF)<<8)|(len[1]&0xFF));

				//Checks if the packet has a valid length
				if(length<0)
					throw new Exception();

				//Creates the array with the data
				byte[] data=new byte[length];

				//Reads all the data
				for(short i=0; i<length; i++)
					data[i]=this.reader.readByte();

				//Checks if the packet handler is set
				if(this.onPacket!=null)
					//Creates the packet and sends it to the handler
					this.onPacket.execute(ServerPacket.getPacketByID(type,data).get());
				
			}
		}catch(Exception e){}
		
		//Checks if a connection end event got set
		if(this.onConnectionEnd!=null)
			//Executes the disconnect event
			this.onConnectionEnd.execute(EnumReason.SERVER_END);
	}
	
	/*
	 * Closes the connection
	 * */
	public void closeConnection() {
		try{
			this.reader.close();
		}catch(Exception e){}
		try{
			this.writer.close();
		}catch(Exception e){}
		try{
			this.socket.close();
		}catch(Exception e){}
	}
	
	/**
	 * Sends the given packet to the client
	 * 
	 * @param packet the packet to send
	 * */
	public void sendPacket(ClientPacket packet) {
		try{
			//Gets the data
			byte[] data = packet.getData().toBytes();
			
			//Sends the packet ID
			this.writer.write(ClientPacket.getIdByPacket(packet.getClass()));
			//Sends the data length
			this.writer.write(ByteBuffer.allocate(2).putShort((short)data.length).array());
			//Sends the data
			this.writer.write(data);
			
			//Flushes the packet
			this.writer.flush();
		}catch(Exception e){}
	}
	
}
