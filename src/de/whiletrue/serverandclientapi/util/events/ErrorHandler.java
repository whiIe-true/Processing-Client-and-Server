package de.whiletrue.serverandclientapi.util.events;
@FunctionalInterface
public interface ErrorHandler{
	public void execute(Exception exception);
}