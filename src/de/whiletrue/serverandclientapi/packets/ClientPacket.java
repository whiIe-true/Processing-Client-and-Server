package de.whiletrue.serverandclientapi.packets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import de.whiletrue.serverandclientapi.util.ByteSender;

/*
 * Packets the are send from the client to the sever
 * Client => Server
 * */

public abstract class ClientPacket extends Packet{

	//All registered Server packets
	private static Map<Byte,Class<? extends ClientPacket>> registeredPackets = new HashMap<Byte,Class<? extends ClientPacket>>();

	public ClientPacket(ByteSender data) throws Exception{
		super(data);
	}
	
	/*
	 * Register a custom server packet
	 * */
	public static void registerPacket(byte id,Class<? extends ClientPacket> packetType) {
		registeredPackets.put(id,packetType);
	}
	
	/*
	 * Gets the id by the packet class
	 * */
	public static byte getIdByPacket(Class<? extends ClientPacket> packet) {
		//Iterates over all packets
		for(Entry<Byte,Class<? extends ClientPacket>> p:registeredPackets.entrySet()) 
			//Checks if the packet matches
			if(p.getValue().equals(packet))
				return p.getKey();
		//If no id was found it returns error -1
		return -1;
	}
	
	/*
	 * Get the full packet by its id
	 * */
	public static Optional<ClientPacket> getPacketByID(byte id,byte[] bytes) {
		//Checks if the packet exists
		if(!registeredPackets.containsKey(id))
			return Optional.empty();
		
		try{
			//Inits the packet
			ClientPacket packet = registeredPackets.get(id).getConstructor(ByteSender.class).newInstance(new ByteSender(bytes));
			//Returns the packet
			return Optional.of(packet);
		}catch(Exception e){
			return Optional.empty();
		}
	}
}
