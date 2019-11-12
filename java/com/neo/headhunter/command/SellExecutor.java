package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
			if(sender instanceof Player) {
				Player hunter = (Player) sender;
				PlayerInventory inventory = hunter.getInventory();
				int heldSlot = inventory.getHeldItemSlot();
				ItemStack heldItem = inventory.getItem(heldSlot);
				double headStackValue = getHeadStackValue(heldItem);
				if(heldItem != null && headStackValue > 0) {
					int amount = heldItem.getAmount();
					plugin.getEconomy().depositPlayer(hunter, headStackValue);
					inventory.clear(heldSlot);
					// message for successful head sell
				} else {
					// message for invalid head
				}
			} else {
				// message for player-only commands
			}
		} else if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			// sell all heads
			if(sender instanceof Player) {
				Player hunter = (Player) sender;
				PlayerInventory inventory = hunter.getInventory();
				double totalValue = 0;
				int totalAmount = 0;
				for(int i = 0; i < 36; i++) {
					ItemStack currentItem = inventory.getItem(i);
					double itemStackValue = getHeadStackValue(currentItem);
					if(currentItem != null && itemStackValue > 0) {
						totalValue += itemStackValue;
						totalAmount += currentItem.getAmount();
						inventory.clear(i);
					}
				}
				if(totalAmount > 0) {
					// message for successful head sell
				} else {
					// message for no heads in inventory
				}
			} else {
				// message for player-only commands
			}
		} else {
			// usage for "/sellhead"
		}
		return false;
	}
	
	private double getHeadStackValue(ItemStack head) {
		if(head != null && HEAD_MATERIALS.contains(head.getType())) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(meta != null) {
				List<String> lore = meta.getLore();
				if(lore != null && !lore.isEmpty()) {
					String priceString = lore.get(0);
					priceString = ChatColor.stripColor(priceString);
					priceString = priceString.replace("Sell Price: $", "");
					if(priceString.matches("\\d+([.]\\d+)?")) {
						return Double.valueOf(priceString) * head.getAmount();
					}
				}
			}
		}
		return 0;
	}
}
