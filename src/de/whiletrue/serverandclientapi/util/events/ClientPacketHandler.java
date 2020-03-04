package de.whiletrue.serverandclientapi.util.events;

import de.whiletrue.serverandclientapi.packets.ClientPacket;

@FunctionalInterface
public interface ClientPacketHandler{
	public void execute(ClientPacket packet);
}
