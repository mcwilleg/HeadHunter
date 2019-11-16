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
					plugin.reloadAll();
					sender.sendMessage(Message.RELOADED.success(plugin.getName(), plugin.getVersion()));
					return true;
				} else
					sender.sendMessage(Usage.HUNTER_RELOAD.toString());
			} else if(args[0].equalsIgnoreCase("world")) {
				if(sender instanceof Player) {
					World world = ((Player) sender).getWorld();
					if (args.length == 1) {
						String status = "disabled";
						if(plugin.getWorldManager().isValidWorld(world))
							status = "enabled";
						sender.sendMessage(Message.WORLD_CHECK.info(status, world.getName()));
					} else if(args.length == 2) {
						if(args[1].equalsIgnoreCase("add")) {
							if(plugin.getWorldManager().addValidWorld(world))
								sender.sendMessage(Message.WORLD_ADDED.success(world.getName()));
							else
								sender.sendMessage(Message.WORLD_ADD_FAIL.info(world.getName()));
							return true;
						} else if(args[1].equalsIgnoreCase("remove")) {
							if(plugin.getWorldManager().removeValidWorld(world))
								sender.sendMessage(Message.WORLD_REMOVED.success(world.getName()));
							else
								sender.sendMessage(Message.WORLD_REMOVE_FAIL.info(world.getName()));
							return true;
						} else
							sender.sendMessage(Usage.HUNTER_WORLD.toString());
					} else
						sender.sendMessage(Usage.HUNTER_WORLD.toString());
				} else
					sender.sendMessage(Message.PLAYERS_ONLY.failure("/hunter world ..."));
			} else
				sender.sendMessage(Usage.HUNTER.toString());
		}
		return false;
	}
}
