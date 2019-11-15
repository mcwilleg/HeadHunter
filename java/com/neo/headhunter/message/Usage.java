package com.neo.headhunter.message;

import org.bukkit.ChatColor;

public enum Usage {
	HUNTER("/hunter <reload/world> ..."),
	HUNTER_RELOAD("/hunter reload"),
	HUNTER_WORLD("/hunter world [add/remove]"),
	SELLHEAD("/sellhead [all]"),
	BOUNTY("/bounty <TARGET> [AMOUNT/remove]");
	
	private final String message;
	
	Usage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return ChatColor.RED + "Usage: " + message;
	}
}
