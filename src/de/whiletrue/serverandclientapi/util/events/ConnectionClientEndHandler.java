package de.whiletrue.serverandclientapi.util.events;

@FunctionalInterface
public interface ConnectionClientEndHandler{
	public void execute(EnumReason reason);
	
	public enum EnumReason{
		SERVER_END,
		CONNECT_FAILED;
	}
}
