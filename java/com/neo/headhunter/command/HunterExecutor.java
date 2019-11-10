package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HunterExecutor implements CommandExecutor {
	private HeadHunter plugin;
	
	public HunterExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(args.length == 1) {
					plugin.reloadAll();
					// message for "/hunter reload"
					return true;
				} else {
					// usage of "/hunter reload"
				}
			} else if(args[0].equalsIgnoreCase("world")) {
				if(sender instanceof Player) {
					World world = ((Player) sender).getWorld();
					if (args.length == 1) {
						if(plugin.getWorldManager().isValidWorld(world)) {
							// message for confirming valid world
						} else {
							// message for confirming invalid world
						}
					} else if(args.length == 2) {
						if(args[1].equalsIgnoreCase("add")) {
							if(plugin.getWorldManager().addValidWorld(world)) {
								// message for successful world addition
							} else {
								// message for failed world addition
							}
							return true;
						} else if(args[1].equalsIgnoreCase("remove")) {
							if(plugin.getWorldManager().removeValidWorld(world)) {
								// message for successful world removal
							} else {
								// message for failed world removal
							}
							return true;
						} else {
							// usage of "/hunter world"
						}
					} else {
						// usage of "/hunter world"
					}
				} else {
					// message for player-only commands
				}
			} else {
				// usage of "/hunter"
			}
		}
		return false;
	}
}
