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
		switch(args.length) {
		case 0:
			// message for "/hunter"
			return false;
			
		case 1:
			if(args[0].equalsIgnoreCase("reload")) {
				plugin.reloadAll();
				// message for "/hunter reload"
				return true;
			}
			return false;
			
		case 2:
			if(args[0].equalsIgnoreCase("world")) {
				if(sender instanceof Player) {
					Player senderPlayer = (Player) sender;
					World world = senderPlayer.getWorld();
					
					if (args[1].equalsIgnoreCase("add")) {
						// message for "/hunter world add"
						boolean success = plugin.getWorldManager().addValidWorld(world);
						if(success) {
							// message for successful world addition
						} else {
							// message for failed world addition
						}
						return true;
						
					} else if (args[1].equalsIgnoreCase("remove")) {
						// message for "/hunter world remove"
						boolean success = plugin.getWorldManager().removeValidWorld(world);
						if(success) {
							// message for successful world removal
						} else {
							// message for failed world removal
						}
						return true;
					}
				} else {
					// message for player-only commands
				}
			}
		}
		return false;
	}
}
