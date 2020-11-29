package com.neo.headhunter.util.message;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public enum Usage {
	HUNTER("/hunter <reload/world> ..."),
	HUNTER_RELOAD("/hunter reload"),
	HUNTER_WORLD("/hunter world [add/remove]"),
	SELLHEAD("/sellhead [all]"),
	BOUNTY("/bounty <TARGET> [AMOUNT/remove]"),
	BOUNTY_LIST("/bounty list [page = 1]");
	
	private final String message;

	public void send(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: " + message));
	}
}
