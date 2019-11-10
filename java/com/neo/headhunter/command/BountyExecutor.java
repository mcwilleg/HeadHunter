package com.neo.headhunter.command;

import com.neo.headhunter.HeadHunter;
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
						double totalBounty = plugin.getBountyManager().getTotalBounty(victim);
						double hunterBounty = plugin.getBountyManager().getBounty(hunter, victim);
						// message for bounty check
						return true;
					} else if (args.length == 2) {
						String bountyString = args[1];
						if(bountyString.equalsIgnoreCase("remove") || bountyString.equalsIgnoreCase("0")) {
							double amount = plugin.getBountyManager().removeBounty(hunter, victim);
							if(amount > 0) {
								plugin.getEconomy().depositPlayer(hunter, amount);
								// message for successful bounty removal
							} else {
								// message for failed bounty removal
							}
							return true;
						} else if (bountyString.matches("\\d+([.]\\d*)?")) {
							double amount = Double.valueOf(bountyString);
							if(amount > plugin.getSettings().getMinimumBounty()) {
								double current = plugin.getBountyManager().removeBounty(hunter, victim);
								plugin.getEconomy().depositPlayer(hunter, current);
								plugin.getBountyManager().setBounty(hunter, victim, amount);
								plugin.getEconomy().withdrawPlayer(hunter, amount);
								// message for successful bounty set
								return true;
							} else {
								// message for low amount
							}
						} else {
							// message for invalid amount
						}
					} else {
						// message for "/bounty"
					}
				} else {
					// message for invalid bounty target
				}
			} else {
				// message for player-only commands
			}
		} else {
			// message for "/bounty"
		}
		return false;
	}
	
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
