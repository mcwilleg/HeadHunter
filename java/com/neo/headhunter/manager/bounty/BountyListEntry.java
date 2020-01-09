package com.neo.headhunter.manager.bounty;

import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

public class BountyListEntry implements Comparable<BountyListEntry> {
	private final OfflinePlayer victim;
	private final double amount;
	
	BountyListEntry(OfflinePlayer victim, double amount) {
		if(victim == null)
			throw new IllegalArgumentException("victim cannot be null");
		this.victim = victim;
		this.amount = amount;
	}
	
	public OfflinePlayer getVictim() {
		return victim;
	}
	
	public double getAmount() {
		return amount;
	}
	
	@Override
	public int compareTo(@Nonnull BountyListEntry o) {
		return Double.compare(amount, o.amount);
	}
}
