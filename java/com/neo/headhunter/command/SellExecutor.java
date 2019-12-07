package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.message.Message;
import com.neo.headhunter.message.Usage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public class SellExecutor implements CommandExecutor {
	private HeadHunter plugin;
	
	public SellExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
		if(args.length == 0) {
			// permission
			if(!sender.hasPermission("hunter.sellhead.hand")) {
				sender.sendMessage(Message.PERMISSION.format("/sellhead"));
				return false;
			}
			
			// assert sender is a player
			if(!(sender instanceof Player)) {
				sender.sendMessage(Message.PLAYERS_ONLY.format("/sellhead"));
				return false;
			}
			
			// sell held stack of heads
			sellHeads((Player) sender, false);
			return true;
		} else if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			// permission
			if(!sender.hasPermission("hunter.sellhead.all")) {
				sender.sendMessage(Message.PERMISSION.format("/sellhead all"));
				return false;
			}
			
			// assert sender is a player
			if(!(sender instanceof Player)) {
				sender.sendMessage(Message.PLAYERS_ONLY.format("/sellhead all"));
				return false;
			}
			
			// sell all heads in inventory
			sellHeads((Player) sender, true);
			return true;
		} else
			sender.sendMessage(Usage.SELLHEAD.toString());
		return false;
	}
	
	public void sellHeads(Player hunter, boolean all) {
		PlayerInventory inventory = hunter.getInventory();
		String hName = hunter.getName();
		if(all) {
			String displayName = null;
			double totalValue = 0;
			int totalAmount = 0;
			for(int i = 0; i < 36; i++) {
				ItemStack currentItem = inventory.getItem(i);
				double itemStackValue = plugin.getHeadBlockManager().getHeadStackValue(currentItem);
				if(currentItem != null && itemStackValue > 0) {
					totalValue += itemStackValue;
					totalAmount += currentItem.getAmount();
					ItemMeta meta = currentItem.getItemMeta();
					displayName = currentItem.getType().name();
					if(meta != null)
						displayName = meta.getDisplayName();
					inventory.clear(i);
				}
			}
			if(totalAmount > 0) {
				plugin.getEconomy().depositPlayer(hunter, totalValue);
				
				// send sell messages
				if(plugin.getSettings().isBroadcastSell()) {
					if(totalAmount == 1)
						hunter.sendMessage(Message.SELL_SINGLE_BROADCAST.format(hName, displayName, 1, totalValue));
					else
						hunter.sendMessage(Message.SELL_MULTIPLE_BROADCAST.format(hName, totalAmount, totalValue));
				} else if(totalAmount == 1)
					hunter.sendMessage(Message.SELL_SINGLE.format(1, totalValue));
				else
					hunter.sendMessage(Message.SELL_MULTIPLE.format(totalAmount, totalValue));
				
			} else
				hunter.sendMessage(Message.SELL_FAIL.format());
		} else {
			int heldSlot = inventory.getHeldItemSlot();
			ItemStack heldItem = inventory.getItem(heldSlot);
			double stackValue = plugin.getHeadBlockManager().getHeadStackValue(heldItem);
			if(heldItem != null && stackValue > 0) {
				int amount = heldItem.getAmount();
				plugin.getEconomy().depositPlayer(hunter, stackValue);
				inventory.clear(heldSlot);
				
				// send sell messages
				if(plugin.getSettings().isBroadcastSell()) {
					ItemMeta meta = heldItem.getItemMeta();
					String displayName = heldItem.getType().name();
					if(meta != null)
						displayName = meta.getDisplayName();
					if(amount == 1)
						hunter.sendMessage(Message.SELL_SINGLE_BROADCAST.format(hName, displayName, 1, stackValue));
					else
						hunter.sendMessage(Message.SELL_MULTIPLE_BROADCAST.format(hName, amount, stackValue));
				} else if(amount == 1)
					hunter.sendMessage(Message.SELL_SINGLE.format(1, stackValue));
				else
					hunter.sendMessage(Message.SELL_MULTIPLE.format(amount, stackValue));
				
			} else
				hunter.sendMessage(Message.SELL_FAIL.format());
		}
	}
}
