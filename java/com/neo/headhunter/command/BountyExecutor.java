package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.bounty.BountyListEntry;
import com.neo.headhunter.message.Message;
import com.neo.headhunter.message.Usage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class BountyExecutor implements CommandExecutor {
	private HeadHunter plugin;
	
	public BountyExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
		if(args.length < 1) {
			sender.sendMessage(Usage.BOUNTY.toString());
			return false;
		}
		
		if(args[0].equalsIgnoreCase("list")) {
			// list bounties
			
			// permission
			if(!sender.hasPermission("hunter.bounty.check")) {
				sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET>"));
				return false;
			}
			
			// assert command is exactly /bounty list
			if(args.length != 1 && args.length != 2) {
				sender.sendMessage(Usage.BOUNTY_LIST.toString());
				return false;
			}
			
			// get page number and assert it is valid
			int page = 1;
			if(args.length == 2) {
				if(!args[1].matches("\\d+")) {
					sender.sendMessage(Message.BOUNTY_PAGE_INVALID.format(args[1]));
					return false;
				}
				
				page = Integer.valueOf(args[1]);
				if(page < 0) {
					sender.sendMessage(Message.BOUNTY_PAGE_INVALID.format(args[1]));
					return false;
				}
			}
			
			// assert page number is within range of bounty list
			List<BountyListEntry> bountyListPage = plugin.getBountyManager().getBountyListPage(page);
			if(bountyListPage == null) {
				sender.sendMessage(Message.BOUNTY_PAGE_EMPTY.format(page));
				return false;
			}
			
			// assert bounty list is not empty
			if(bountyListPage.isEmpty()) {
				sender.sendMessage(Message.BOUNTY_LIST_EMPTY.format());
				return true;
			}
			
			// messages for bounty list
			for(BountyListEntry entry : bountyListPage) {
				OfflinePlayer victim = entry.getVictim();
				double totalBounty = entry.getAmount();
				
				// get personal bounty
				double hunterBounty = 0;
				if(sender instanceof Player)
					hunterBounty = plugin.getBountyManager().getBounty((Player) sender, victim);
				
				// create message for personal bounty
				String personal = "";
				if(hunterBounty > 0)
					personal = Message.BOUNTY_PERSONAL.format(hunterBounty);
				sender.sendMessage(Message.BOUNTY_TOTAL.format(victim.getName(), totalBounty, personal));
			}
			return true;
		} else {
			if(!(sender instanceof Player)) {
				sender.sendMessage(Message.PLAYERS_ONLY.format("/bounty <TARGET> [AMOUNT/remove]"));
				return false;
			}
			
			Player hunter = (Player) sender;
			OfflinePlayer victim = getPlayer(args[0]);
			if(victim == null) {
				sender.sendMessage(Message.BOUNTY_TARGET_INVALID.format(args[0]));
				return false;
			}
			
			String hName = hunter.getName(), vName = victim.getName();
			if(args.length == 1) {
				// check bounty
				
				// permission
				if(!sender.hasPermission("hunter.bounty.check")) {
					sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET>"));
					return false;
				}
				
				double totalBounty = plugin.getBountyManager().getTotalBounty(victim);
				double hunterBounty = plugin.getBountyManager().getBounty(hunter, victim);
				
				// message for bounty check
				String personal = "";
				if(hunterBounty > 0)
					personal = Message.BOUNTY_PERSONAL.format(hunterBounty);
				sender.sendMessage(Message.BOUNTY_TOTAL.format(vName, totalBounty, personal));
				return true;
			} else if(args.length == 2) {
				// set or remove bounty
				
				String bountyString = args[1];
				if(bountyString.equalsIgnoreCase("remove") || bountyString.equalsIgnoreCase("0")) {
					// remove bounty
					
					// permission
					if(!sender.hasPermission("hunter.bounty.set")) {
						sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET> <remove>"));
						return false;
					}
					
					// assert bounty exists
					double amount = plugin.getBountyManager().removeBounty(hunter, victim);
					if(amount <= 0) {
						sender.sendMessage(Message.BOUNTY_REMOVE_FAIL.format(vName));
						return true;
					}
					
					// refund money and message for bounty remove
					plugin.getEconomy().depositPlayer(hunter, amount);
					if(plugin.getSettings().isBountyBroadcast())
						Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_REMOVE.format(hName, amount, vName));
					else
						sender.sendMessage(Message.BOUNTY_REMOVED.format(vName));
					return true;
				} else if(bountyString.matches("\\d+([.]\\d{0,2})?")){
					// set bounty
					
					// permission
					if(!sender.hasPermission("hunter.bounty.set")) {
						sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET> <AMOUNT>"));
						return false;
					}
					
					// assert bounty target is not the command sender
					if(hunter.equals(victim)) {
						sender.sendMessage(Message.BOUNTY_SET_SELF.format());
						return true;
					}
					
					// assert new bounty is above minimum threshold
					double amount = Double.valueOf(bountyString);
					if(amount < plugin.getSettings().getMinimumBounty()) {
						sender.sendMessage(Message.BOUNTY_AMOUNT_LOW.format(plugin.getSettings().getMinimumBounty()));
						return true;
					}
					
					// assert hunter has enough money to set this bounty
					double current = plugin.getBountyManager().removeBounty(hunter, victim);
					double balance = plugin.getEconomy().getBalance(hunter);
					if(amount > balance + current) {
						sender.sendMessage(Message.BOUNTY_SET_AFFORD.format(amount));
						return true;
					}
					
					// set new bounty and message for bounty set
					plugin.getEconomy().depositPlayer(hunter, current);
					plugin.getBountyManager().setBounty(hunter, victim, amount);
					plugin.getEconomy().withdrawPlayer(hunter, amount);
					if (plugin.getSettings().isBountyBroadcast())
						Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_SET.format(hName, amount, vName));
					else
						sender.sendMessage(Message.BOUNTY_SET.format(vName, amount));
					return true;
				} else {
					sender.sendMessage(Message.BOUNTY_AMOUNT_INVALID.format(bountyString));
					return false;
				}
			} else {
				sender.sendMessage(Usage.BOUNTY.toString());
				return false;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public OfflinePlayer getPlayer(String name) {
		OfflinePlayer result = Bukkit.getPlayerExact(name);
		if(result == null)
			result = Bukkit.getPlayer(name);
		if(result == null)
			result = Bukkit.getOfflinePlayer(name);
		if(result.hasPlayedBefore())
			return result;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getName().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}
}
