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
						double totalBounty = plugin.getBountyManager().getTotalBounty(victim);
						double hunterBounty = plugin.getBountyManager().getBounty(hunter, victim);
						// message for bounty check
						String personal = "";
						if(hunterBounty > 0)
							personal = Message.BOUNTY_PERSONAL.success(hunterBounty);
						sender.sendMessage(Message.BOUNTY_TOTAL.info(victim.getName(), totalBounty, personal));
						return true;
					} else if (args.length == 2) {
						String bountyString = args[1];
						if(bountyString.equalsIgnoreCase("remove") || bountyString.equalsIgnoreCase("0")) {
							double amount = plugin.getBountyManager().removeBounty(hunter, victim);
							if(amount > 0) {
								plugin.getEconomy().depositPlayer(hunter, amount);
								if(plugin.getSettings().isBountyBroadcast())
									Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_REMOVE.success(hunter.getName(), amount, victim.getName()));
								else
									sender.sendMessage(Message.BOUNTY_REMOVED.success(victim.getName()));
							} else
								sender.sendMessage(Message.BOUNTY_REMOVE_FAIL.failure(victim.getName()));
							return true;
						} else if (bountyString.matches("\\d+([.]\\d*)?")) {
							double amount = Double.valueOf(bountyString);
							if(amount > plugin.getSettings().getMinimumBounty()) {
								double current = plugin.getBountyManager().removeBounty(hunter, victim);
								plugin.getEconomy().depositPlayer(hunter, current);
								plugin.getBountyManager().setBounty(hunter, victim, amount);
								plugin.getEconomy().withdrawPlayer(hunter, amount);
								if(plugin.getSettings().isBountyBroadcast())
									Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_SET.success(hunter.getName(), amount, victim.getName()));
								else
									sender.sendMessage(Message.BOUNTY_SET.success(victim.getName(), amount));
								return true;
							} else
								sender.sendMessage(Message.BOUNTY_AMOUNT_LOW.failure(plugin.getSettings().getMinimumBounty()));
						} else
							sender.sendMessage(Message.BOUNTY_AMOUNT_INVALID.failure(bountyString));
					} else
						sender.sendMessage(Usage.BOUNTY.toString());
				} else
					sender.sendMessage(Message.BOUNTY_TARGET_INVALID.failure(args[0]));
			} else
				sender.sendMessage(Message.PLAYERS_ONLY.failure("/bounty <TARGET> [AMOUNT/remove]"));
		} else
			sender.sendMessage(Usage.BOUNTY.toString());
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
