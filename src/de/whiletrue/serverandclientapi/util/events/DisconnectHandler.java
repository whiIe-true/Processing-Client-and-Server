package de.whiletrue.serverandclientapi.util.events;

import de.whiletrue.serverandclientapi.server.Connection;

@FunctionalInterface
public interface DisconnectHandler{
	public void execute(Connection connection);
}