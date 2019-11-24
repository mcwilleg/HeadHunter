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

public class SellExecutor implements CommandExecutor {
	private HeadHunter plugin;
	
	public SellExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			// check permission
			if(!sender.hasPermission("hunter.sellhead.hand")) {
				sender.sendMessage(Message.PERMISSION.format("/sellhead"));
				return true;
			}
			
			if(sender instanceof Player) {
				sellHeads((Player) sender, false);
				return true;
			} else
				sender.sendMessage(Message.PLAYERS_ONLY.format("/sellhead"));
		} else if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			// check permission
			if(!sender.hasPermission("hunter.sellhead.all")) {
				sender.sendMessage(Message.PERMISSION.format("/sellhead all"));
				return true;
			}
			
			if(sender instanceof Player) {
				sellHeads((Player) sender, true);
				return true;
			} else
				sender.sendMessage(Message.PLAYERS_ONLY.format("/sellhead all"));
		} else
			sender.sendMessage(Usage.SELLHEAD.toString());
		return false;
	}
	
	public void sellHeads(Player hunter, boolean all) {
		PlayerInventory inventory = hunter.getInventory();
		if(all) {
			double totalValue = 0;
			int totalAmount = 0;
			for(int i = 0; i < 36; i++) {
				ItemStack currentItem = inventory.getItem(i);
				double itemStackValue = plugin.getHeadBlockManager().getHeadStackValue(currentItem);
				if(currentItem != null && itemStackValue > 0) {
					totalValue += itemStackValue;
					totalAmount += currentItem.getAmount();
					inventory.clear(i);
				}
			}
			if(totalAmount > 0) {
				plugin.getEconomy().depositPlayer(hunter, totalValue);
				
				if(totalAmount == 1)
					hunter.sendMessage(Message.SELL_SINGLE.format(1, totalValue));
				else
					hunter.sendMessage(Message.SELL_MULTIPLE.format(totalAmount, totalValue));
			} else
				hunter.sendMessage(Message.SELL_FAIL.format());
		} else {
			int heldSlot = inventory.getHeldItemSlot();
			ItemStack heldItem = inventory.getItem(heldSlot);
			double headStackvalue = plugin.getHeadBlockManager().getHeadStackValue(heldItem);
			if(heldItem != null && headStackvalue > 0) {
				int amount = heldItem.getAmount();
				plugin.getEconomy().depositPlayer(hunter, headStackvalue);
				inventory.clear(heldSlot);
				
				if(amount == 1)
					hunter.sendMessage(Message.SELL_SINGLE.format(1, headStackvalue));
				else
					hunter.sendMessage(Message.SELL_MULTIPLE.format(amount, headStackvalue));
			} else
				hunter.sendMessage(Message.SELL_FAIL.format());
		}
	}
}
