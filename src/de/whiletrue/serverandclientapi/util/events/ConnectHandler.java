package de.whiletrue.serverandclientapi.util.events;

import de.whiletrue.serverandclientapi.server.Connection;

@FunctionalInterface
public interface ConnectHandler{
	public ClientPacketHandler execute(Connection connection);
}