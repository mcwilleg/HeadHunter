package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.util.message.Message;
import com.neo.headhunter.util.message.Usage;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HunterExecutor implements CommandExecutor, TabCompleter {
	private final HeadHunter plugin;
	
	public HunterExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
		if(args.length < 1) {
			sender.sendMessage(Usage.HUNTER.toString());
			return false;
		}
		
		if(args[0].equalsIgnoreCase("reload")) {
			// permission
			if(!sender.hasPermission("hunter.reload")) {
				sender.sendMessage(Message.PERMISSION.format("/hunter reload"));
				return false;
			}
			
			// assert command is exactly /hunter reload
			if(args.length != 1) {
				sender.sendMessage(Usage.HUNTER_RELOAD.toString());
				return false;
			}
			
			// reload plugin
			plugin.reloadAll();
			sender.sendMessage(Message.RELOADED.format(plugin.getName(), plugin.getVersion()));
			return true;
		} else if(args[0].equalsIgnoreCase("world")) {
			// permission
			if(!sender.hasPermission("hunter.world")) {
				sender.sendMessage(Message.PERMISSION.format("/hunter world [add/remove]"));
				return false;
			}
			
			// assert sender is a player
			if(!(sender instanceof Player)) {
				sender.sendMessage(Message.PLAYERS_ONLY.format("/hunter world ..."));
				return false;
			}
			
			// get world and check argument length
			World world = ((Player) sender).getWorld();
			if(args.length == 1) {
				
				// message for world check
				String status = "disabled";
				if(plugin.getWorldManager().isValidWorld(world))
					status = "enabled";
				sender.sendMessage(Message.WORLD_CHECK.format(status, world.getName()));
				return true;
			} else if(args.length == 2) {
				if(args[1].equalsIgnoreCase("add")) {
					
					// message for world add
					if(plugin.getWorldManager().addValidWorld(world))
						sender.sendMessage(Message.WORLD_ADDED.format(world.getName()));
					else
						sender.sendMessage(Message.WORLD_ADD_FAIL.format(world.getName()));
					return true;
				} else if(args[1].equalsIgnoreCase("remove")) {
					
					// message for world remove
					if(plugin.getWorldManager().removeValidWorld(world))
						sender.sendMessage(Message.WORLD_REMOVED.format(world.getName()));
					else
						sender.sendMessage(Message.WORLD_REMOVE_FAIL.format(world.getName()));
					return true;
				} else {
					sender.sendMessage(Usage.HUNTER_WORLD.toString());
					return false;
				}
			} else {
				sender.sendMessage(Usage.HUNTER_WORLD.toString());
				return false;
			}
		} else {
			sender.sendMessage(Usage.HUNTER.toString());
			return false;
		}
	}
	
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
		final List<String> result = new ArrayList<>();
		if(args.length == 1) {
			Iterable<String> completions = Arrays.asList("reload", "world");
			StringUtil.copyPartialMatches(args[0], completions, result);
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("world")) {
				Iterable<String> completions = Arrays.asList("add", "remove");
				StringUtil.copyPartialMatches(args[1], completions, result);
			}
		}
		return result;
	}
}
