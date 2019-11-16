package com.neo.headhunter.message;

import org.bukkit.ChatColor;

public enum Message {
	RELOADED("%S v%S reloaded."),
	WORLD_CHECK("Head drops are %S in world %S."),
	WORLD_ADDED("Successfully allowed head drops in world %S."),
	WORLD_REMOVED("Successfully disabled head drops in world %S."),
	WORLD_ADD_FAIL("Head drops are already allowed in world %S."),
	WORLD_REMOVE_FAIL("Head drops are already disabled in world %S."),
	PLAYERS_ONLY("The %S command is for players only.");
	
	private final String message;
	
	Message(String message) {
		this.message = message;
	}
	
	public String success(Object... parameters) {
		return ChatColor.GREEN + String.format(message, parameters);
	}
	
	public String info(Object... parameters) {
		return ChatColor.YELLOW + String.format(message, parameters);
	}
	
	public String failure(Object... parameters) {
		return ChatColor.RED + String.format(message, parameters);
	}
}
