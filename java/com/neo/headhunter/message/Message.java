package com.neo.headhunter.message;

import org.bukkit.ChatColor;

public enum Message {
	RELOADED("%S v%S reloaded."),
	PLAYERS_ONLY("The %S command is for players only."),
	WORLD_CHECK("Head drops are %S in world %S."),
	WORLD_ADDED("Successfully allowed head drops in world %S."),
	WORLD_REMOVED("Successfully disabled head drops in world %S."),
	WORLD_ADD_FAIL("Head drops are already allowed in world %S."),
	WORLD_REMOVE_FAIL("Head drops are already disabled in world %S."),
	BOUNTY_TOTAL("%S: $%.2f %S"),
	BOUNTY_PERSONAL("(You own: $%.2f)"),
	BOUNTY_ADDED("Successfully set your bounty on %S to %.2f."),
	BOUNTY_REMOVED("Successfully removed your bounty on %S."),
	BOUNTY_REMOVE_FAIL("You do not have a bounty on %S."),
	BOUNTY_TARGET_INVALID("\"%S\" is an invalid bounty target."),
	BOUNTY_AMOUNT_INVALID("\"%S\" is an invalid bounty amount."),
	BOUNTY_AMOUNT_LOW("That bounty is too low. Bounties must be greater than: %.2f."),
	SELL_FAIL("You are not holding any heads."),
	SELL_SINGLE("Sold %d head for %.2f."),
	SELL_MULTIPLE("Sold %d heads for a total of %.2f.");
	
	private final String message;
	
	Message(String message) {
		this.message = message;
	}
	
	public String success(Object... parameters) {
		return ChatColor.AQUA + String.format(message, parameters);
	}
	
	public String info(Object... parameters) {
		return ChatColor.YELLOW + String.format(message, parameters);
	}
	
	public String failure(Object... parameters) {
		return ChatColor.RED + String.format(message, parameters);
	}
}
