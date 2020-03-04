package de.whiletrue.serverandclientapi.util.events;

import de.whiletrue.serverandclientapi.packets.ServerPacket;

@FunctionalInterface
public interface ServerPacketHandler{
	public void execute(ServerPacket packet);
}
