package de.whiletrue.serverandclientapi.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import de.whiletrue.serverandclientapi.packets.ClientPacket;
import de.whiletrue.serverandclientapi.packets.ServerPacket;
import de.whiletrue.serverandclientapi.util.events.ClientPacketHandler;
import de.whiletrue.serverandclientapi.util.events.ConnectionServerEndHandler;

public class ConnectionThread extends Thread{

	//Thread counter
	private static int id;
	
	//Socket of the connection
	private Socket socket;
	//Handlers for the events
	private ConnectionServerEndHandler onConnectionEnd;
	private ClientPacketHandler onPacket;
	//Reader and writer
	private DataInputStream reader;
	private DataOutputStream writer;
	
	public ConnectionThread(Socket socket,ConnectionServerEndHandler onConnectionEnd,ClientPacketHandler onPacket){
		this.socket=socket;
		this.onConnectionEnd=onConnectionEnd;
		this.onPacket=onPacket;
		
		//Starts the thread
		this.setName("Connection #"+(id++));
		this.start();
	}
	
	@Override
	public void run(){
		try{
			//Creates the reader and writer
			this.reader=new DataInputStream(this.socket.getInputStream());
			this.writer=new DataOutputStream(this.socket.getOutputStream());
			
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
					this.onPacket.execute(ClientPacket.getPacketByID(type,data).get());
				
			}
		}catch(Exception e){}
		
		//Checks if a connection end event got set
		if(this.onConnectionEnd!=null)
			//Executes the disconnect event
			this.onConnectionEnd.execute();
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
	public void sendPacket(ServerPacket packet) {
		try{
			//Gets the data
			byte[] data = packet.getData().toBytes();
			
			//Sends the packet ID
			this.writer.write(ServerPacket.getIdByPacket(packet.getClass()));
			//Sends the data length
			this.writer.write(ByteBuffer.allocate(2).putShort((short)data.length).array());
			//Sends the data
			this.writer.write(data);
			
			//Flushes the packet
			this.writer.flush();
		}catch(Exception e){}
	}
	
	public void setOnPacket(ClientPacketHandler onPacket){
		this.onPacket=onPacket;
	}
}
