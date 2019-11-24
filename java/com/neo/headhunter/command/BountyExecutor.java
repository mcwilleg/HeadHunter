package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.message.Message;
import com.neo.headhunter.message.Usage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyExecutor implements CommandExecutor {
	private HeadHunter plugin;
	
	public BountyExecutor(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length >= 1) {
			// set commands
			if(sender instanceof Player) {
				Player hunter = (Player) sender;
				OfflinePlayer victim = getPlayer(args[0]);
				if(victim != null) {
					if (args.length == 1) {
						// check permission
						if(!sender.hasPermission("hunter.bounty.check")) {
							sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET>"));
							return true;
						}
						
						double totalBounty = plugin.getBountyManager().getTotalBounty(victim);
						double hunterBounty = plugin.getBountyManager().getBounty(hunter, victim);
						
						// message for bounty check
						String personal = "";
						if(hunterBounty > 0)
							personal = Message.BOUNTY_PERSONAL.format(hunterBounty);
						sender.sendMessage(Message.BOUNTY_TOTAL.format(victim.getName(), totalBounty, personal));
						return true;
					} else if (args.length == 2) {
						String bountyString = args[1];
						if(bountyString.equalsIgnoreCase("remove") || bountyString.equalsIgnoreCase("0")) {
							// check permission
							if(!sender.hasPermission("hunter.bounty.remove")) {
								sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET> <remove>"));
								return true;
							}
							
							double amount = plugin.getBountyManager().removeBounty(hunter, victim);
							if(amount > 0) {
								plugin.getEconomy().depositPlayer(hunter, amount);
								if(plugin.getSettings().isBountyBroadcast())
									Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_REMOVE.format(hunter.getName(), amount, victim.getName()));
								else
									sender.sendMessage(Message.BOUNTY_REMOVED.format(victim.getName()));
							} else
								sender.sendMessage(Message.BOUNTY_REMOVE_FAIL.format(victim.getName()));
							return true;
						} else if (bountyString.matches("\\d+([.]\\d{0,2})?")) {
							// check permission
							if(!sender.hasPermission("hunter.bounty.set")) {
								sender.sendMessage(Message.PERMISSION.format("/bounty <TARGET> <AMOUNT>"));
								return true;
							}
							
							double amount = Double.valueOf(bountyString);
							if(amount >= plugin.getSettings().getMinimumBounty()) {
								double current = plugin.getBountyManager().removeBounty(hunter, victim);
								double balance = plugin.getEconomy().getBalance(hunter);
								if(balance + current >= amount) {
									plugin.getEconomy().depositPlayer(hunter, current);
									plugin.getBountyManager().setBounty(hunter, victim, amount);
									plugin.getEconomy().withdrawPlayer(hunter, amount);
									if (plugin.getSettings().isBountyBroadcast())
										Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_SET.format(hunter.getName(), amount, victim.getName()));
									else
										sender.sendMessage(Message.BOUNTY_SET.format(victim.getName(), amount));
									return true;
								} else
									sender.sendMessage(Message.BOUNTY_SET_AFFORD.format(amount));
							} else
								sender.sendMessage(Message.BOUNTY_AMOUNT_LOW.format(plugin.getSettings().getMinimumBounty()));
						} else
							sender.sendMessage(Message.BOUNTY_AMOUNT_INVALID.format(bountyString));
					} else
						sender.sendMessage(Usage.BOUNTY.toString());
				} else
					sender.sendMessage(Message.BOUNTY_TARGET_INVALID.format(args[0]));
			} else
				sender.sendMessage(Message.PLAYERS_ONLY.format("/bounty <TARGET> [AMOUNT/remove]"));
		} else
			sender.sendMessage(Usage.BOUNTY.toString());
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer(String name) {
		OfflinePlayer result = Bukkit.getPlayerExact(name);
		if(result == null)
			result = Bukkit.getPlayer(name);
		if(result == null)
			result = Bukkit.getOfflinePlayer(name);
		if(result.hasPlayedBefore())
			return result;
		return null;
	}
}
