package com.neo.headhunter.bounty;

import com.neo.headhunter.config.ConfigAccessor;
import com.neo.headhunter.HeadHunter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

public class BountyManager extends ConfigAccessor {
	public BountyManager(HeadHunter plugin) {
		super(plugin, true, "bounties.yml", "data");
	}
	
	// returns the total of all bounties on the victim
	public double getTotalBounty(OfflinePlayer victim) {
		double total = 0;
		ConfigurationSection victimSection = config.getConfigurationSection(id(victim));
		if(victimSection != null) {
			for (String hunterKey : victimSection.getKeys(false))
				total += victimSection.getDouble(hunterKey);
		}
		return total;
	}
	
	// returns the hunter's bounty on the victim
	public double getBounty(OfflinePlayer hunter, OfflinePlayer victim) {
		return config.getDouble(bountyPath(hunter, victim));
	}
	
	// set's the hunter's bounty on the victim to the specified amount
	public void setBounty(OfflinePlayer hunter, OfflinePlayer victim, double amount) {
		config.set(bountyPath(hunter, victim), amount);
		saveConfig();
	}
	
	// deletes all bounties on the victim, and returns their total
	public double removeTotalBounty(OfflinePlayer victim) {
		double totalBounty = getTotalBounty(victim);
		config.set(id(victim), null);
		saveConfig();
		return totalBounty;
	}
	
	// deletes the hunter's bounty on the victim, and returns its amount
	public double removeBounty(OfflinePlayer hunter, OfflinePlayer victim) {
		double bounty = getBounty(hunter, victim);
		config.set(bountyPath(hunter, victim), null);
		if(hasBounties(victim))
			config.set(id(victim), null);
		saveConfig();
		return bounty;
	}
	
	public boolean hasBounties(OfflinePlayer victim) {
		ConfigurationSection victimSection = config.getConfigurationSection(id(victim));
		if(victimSection != null)
			return !victimSection.getKeys(false).isEmpty();
		return false;
	}
	
	// helper method to improve readability
	private String bountyPath(OfflinePlayer hunter, OfflinePlayer victim) {
		return id(victim) + "." + id(hunter);
	}
	
	// helper method to improve readability
	private String id(OfflinePlayer player) {
		if(player != null)
			return player.getUniqueId().toString();
		return null;
	}
}
