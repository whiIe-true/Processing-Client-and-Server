package de.whiletrue.serverandclientapi.packets;

import de.whiletrue.serverandclientapi.util.ByteSender;

public abstract class Packet{
	//Constructor to interprete Data
	public Packet(ByteSender data) throws Exception{}
	
	//Method to converter data
	public abstract ByteSender getData();
}
