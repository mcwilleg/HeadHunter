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
		if (args.length < 1) {
			Usage.HUNTER.send(sender);
			return false;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			// permission
			if (!sender.hasPermission("hunter.reload")) {
				Message.PERMISSION.send(plugin, sender, "/hunter reload");
				return false;
			}
			
			// assert command is exactly /hunter reload
			if (args.length != 1) {
				Usage.HUNTER_RELOAD.send(sender);
				return false;
			}
			
			// reload plugin
			plugin.reloadAll();
			Message.RELOADED.send(plugin, sender, plugin.getName(), plugin.getVersion());
			return true;
		} else if (args[0].equalsIgnoreCase("world")) {
			// permission
			if (!sender.hasPermission("hunter.world")) {
				Message.PERMISSION.send(plugin, sender, "/hunter world [add/remove]");
				return false;
			}
			
			// assert sender is a player
			if (!(sender instanceof Player)) {
				Message.PLAYERS_ONLY.send(plugin, sender, "/hunter world ...");
				return false;
			}
			
			// get world and check argument length
			World world = ((Player) sender).getWorld();
			if (args.length == 1) {
				
				// message for world check
				String status = "disabled";
				if (plugin.getWorldManager().isValidWorld(world)) {
					status = "enabled";
				}
				Message.WORLD_CHECK.send(plugin, sender, status, world.getName());
				return true;
			} else if (args.length == 2) {
				if (args[1].equalsIgnoreCase("add")) {
					
					// message for world add
					if (plugin.getWorldManager().addValidWorld(world)) {
						Message.WORLD_ADDED.send(plugin, sender, world.getName());
					} else {
						Message.WORLD_ADD_FAIL.send(plugin, sender, world.getName());
					}
					return true;
				} else if (args[1].equalsIgnoreCase("remove")) {
					
					// message for world remove
					if (plugin.getWorldManager().removeValidWorld(world)) {
						Message.WORLD_REMOVED.send(plugin, sender, world.getName());
					} else {
						Message.WORLD_REMOVE_FAIL.send(plugin, sender, world.getName());
					}
					return true;
				} else {
					Usage.HUNTER_WORLD.send(sender);
					return false;
				}
			} else {
				Usage.HUNTER_WORLD.send(sender);
				return false;
			}
		} else {
			Usage.HUNTER.send(sender);
			return false;
		}
	}
	
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
		final List<String> result = new ArrayList<>();
		if (args.length == 1) {
			Iterable<String> completions = Arrays.asList("reload", "world");
			StringUtil.copyPartialMatches(args[0], completions, result);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("world")) {
				Iterable<String> completions = Arrays.asList("add", "remove");
				StringUtil.copyPartialMatches(args[1], completions, result);
			}
		}
		return result;
	}
}
