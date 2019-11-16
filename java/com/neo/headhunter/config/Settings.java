package com.neo.headhunter.config;

import com.neo.headhunter.HeadHunter;

public final class Settings extends ConfigAccessor {
	private static final String PLAYER_KILLS_ONLY = "player-kills-only";
	private static final String BALANCE_PLUS_BOUNTY = "balance-plus-bounty";
	private static final String MINIMUM_BOUNTY = "minimum-bounty";
	private static final String BOUNTY_BROADCAST = "bounty-broadcast";
	
	public Settings(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isPlayerKillsOnly() {
		return config.getBoolean(PLAYER_KILLS_ONLY, true);
	}
	
	public boolean isBalancePlusBounty() {
		return config.getBoolean(BALANCE_PLUS_BOUNTY, true);
	}
	
	public double getMinimumBounty() {
		return config.getDouble(MINIMUM_BOUNTY, 20);
	}
	
	public boolean isBountyBroadcast() {
		return config.getBoolean(BOUNTY_BROADCAST, true);
	}
}
