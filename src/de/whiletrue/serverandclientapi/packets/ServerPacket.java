package de.whiletrue.serverandclientapi.packets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.whiletrue.serverandclientapi.util.ByteSender;

/*
 * Packets the are send from the server to the client
 * Server => Client
 * */

public abstract class ServerPacket extends Packet{

	//All registered Server packets
	private static Map<Byte,Class<? extends ServerPacket>> registeredPackets = new HashMap<Byte,Class<? extends ServerPacket>>();

	public ServerPacket(ByteSender data) throws Exception{
		super(data);
	}
	
	/*
	 * Register a custom server packet
	 * */
	public static void registerPacket(byte id,Class<? extends ServerPacket> packetType) {
		registeredPackets.put(id,packetType);
	}
	
	/*
	 * Gets the id by the packet class
	 * */
	public static byte getIdByPacket(Class<? extends ServerPacket> packet) {
		//Iterates over all packets
		for(Entry<Byte,Class<? extends ServerPacket>> p:registeredPackets.entrySet()) 
			//Checks if the packet matches
			if(p.getValue().equals(packet))
				return p.getKey();
		//If no id was found it returns error -1
		return -1;
	}
	
	/*
	 * Get the packet by its id
	 * */
	public static Optional<ServerPacket> getPacketByID(byte id,byte[] bytes) {
		//Checks if the packet exists
		if(!registeredPackets.containsKey(id))
			return Optional.empty();
		
		try{
			//Inits the packet
			ServerPacket packet = registeredPackets.get(id).getConstructor(ByteSender.class).newInstance(new ByteSender(bytes));
			//Returns the packet
			return Optional.of(packet);
		}catch(Exception e){
			return Optional.empty();
		}
	}
}
