package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.message.Message;
import com.neo.headhunter.message.Usage;
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
					// check permission
					if(!sender.hasPermission("hunter.reload")) {
						sender.sendMessage(Message.PERMISSION.format("/hunter reload"));
						return true;
					}
					
					plugin.reloadAll();
					sender.sendMessage(Message.RELOADED.format(plugin.getName(), plugin.getVersion()));
					return true;
				} else
					sender.sendMessage(Usage.HUNTER_RELOAD.toString());
			} else if(args[0].equalsIgnoreCase("world")) {
				// check permission
				if(!sender.hasPermission("hunter.world")) {
					sender.sendMessage(Message.PERMISSION.format("/hunter world [add/remove]"));
					return true;
				}
				
				if(sender instanceof Player) {
					World world = ((Player) sender).getWorld();
					if (args.length == 1) {
						String status = "disabled";
						if(plugin.getWorldManager().isValidWorld(world))
							status = "enabled";
						sender.sendMessage(Message.WORLD_CHECK.format(status, world.getName()));
					} else if(args.length == 2) {
						if(args[1].equalsIgnoreCase("add")) {
							if(plugin.getWorldManager().addValidWorld(world))
								sender.sendMessage(Message.WORLD_ADDED.format(world.getName()));
							else
								sender.sendMessage(Message.WORLD_ADD_FAIL.format(world.getName()));
							return true;
						} else if(args[1].equalsIgnoreCase("remove")) {
							if(plugin.getWorldManager().removeValidWorld(world))
								sender.sendMessage(Message.WORLD_REMOVED.format(world.getName()));
							else
								sender.sendMessage(Message.WORLD_REMOVE_FAIL.format(world.getName()));
							return true;
						} else
							sender.sendMessage(Usage.HUNTER_WORLD.toString());
					} else
						sender.sendMessage(Usage.HUNTER_WORLD.toString());
				} else
					sender.sendMessage(Message.PLAYERS_ONLY.format("/hunter world ..."));
			} else
				sender.sendMessage(Usage.HUNTER.toString());
		} else
			sender.sendMessage(Usage.HUNTER.toString());
		return false;
	}
}
