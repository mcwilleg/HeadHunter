package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class SellExecutor implements CommandExecutor {
	private static final List<Material> HEAD_MATERIALS = Arrays.asList(
			Material.CREEPER_HEAD,
			Material.DRAGON_HEAD,
			Material.PLAYER_HEAD,
			Material.ZOMBIE_HEAD,
			Material.SKELETON_SKULL,
			Material.WITHER_SKELETON_SKULL
	);
	
	private HeadHunter plugin;
	
	public SellExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			// sell head
		} else if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			// sell all heads
		} else {
			// usage for "/sellhead"
		}
		return false;
	}
	
	private double getHeadValue(ItemStack head) {
		if(head != null && HEAD_MATERIALS.contains(head.getType())) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(meta != null) {
				List<String> lore = meta.getLore();
				if(lore != null && !lore.isEmpty()) {
					String priceString = lore.get(0);
					priceString = ChatColor.stripColor(priceString);
					priceString = priceString.replace("Sell Price: $", "");
					if(priceString.matches("\\d+([.]\\d+)?")) {
						return Double.valueOf(priceString);
					}
				}
			}
		}
		return 0;
	}
}
