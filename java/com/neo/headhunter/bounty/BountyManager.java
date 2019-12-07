package com.neo.headhunter.bounty;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class BountyManager extends ConfigAccessor<HeadHunter> {
	private static final int LIST_PAGE_SIZE = 10;
	
	private List<BountyListEntry> bountyList;
	
	public BountyManager(HeadHunter plugin) {
		super(plugin, true, "bounties.yml", "data");
		this.bountyList = getBountyList();
		Collections.sort(this.bountyList);
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
	
	public List<BountyListEntry> getBountyListPage(int page) {
		List<BountyListEntry> result = new ArrayList<>();
		if(!bountyList.isEmpty()) {
			int totalPages = (bountyList.size() - 1) / LIST_PAGE_SIZE + 1;
			if(page <= totalPages) {
				int fromIndex = Math.max(0, (page - 1) * LIST_PAGE_SIZE);
				int toIndex = Math.min(bountyList.size(), page * LIST_PAGE_SIZE);
				result.addAll(bountyList.subList(fromIndex, toIndex));
				return result;
			}
			return null;
		}
		return result;
	}
	
	public OfflinePlayer getTopHunter(OfflinePlayer victim) {
		String victimPath = id(victim);
		ConfigurationSection hunterSection = config.getConfigurationSection(victimPath);
		String topHunterPath = null;
		double maxBounty = 0;
		if(hunterSection != null) {
			for (String hunterPath : hunterSection.getKeys(false)) {
				double bounty = hunterSection.getDouble(hunterPath);
				if(maxBounty < bounty || maxBounty == 0) {
					topHunterPath = hunterPath;
					maxBounty = bounty;
				}
			}
		}
		if(topHunterPath != null)
			return Bukkit.getOfflinePlayer(UUID.fromString(topHunterPath));
		return null;
	}
	
	private boolean hasBounties(OfflinePlayer victim) {
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
	
	private List<BountyListEntry> getBountyList() {
		List<BountyListEntry> result = new ArrayList<>();
		for(String victimIdString : config.getKeys(false)) {
			UUID victimId = UUID.fromString(victimIdString);
			OfflinePlayer victim;
			if((victim = Bukkit.getPlayer(victimId)) == null)
				victim = Bukkit.getOfflinePlayer(victimId);
			double amount = getTotalBounty(victim);
			result.add(new BountyListEntry(victim, amount));
		}
		return result;
	}
	
	@Override
	protected void saveConfig() {
		super.saveConfig();
		this.bountyList = getBountyList();
		Collections.sort(this.bountyList);
	}
}
