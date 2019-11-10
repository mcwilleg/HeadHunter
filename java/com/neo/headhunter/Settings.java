package com.neo.headhunter;

final class Settings extends ConfigAccessor {
	private static final String PLAYER_KILLS_ONLY = "player-kills-only";
	private static final String BALANCE_PLUS_BOUNTY = "balance-plus-bounty";
	
	Settings(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isPlayerKillsOnly() {
		return config.getBoolean(PLAYER_KILLS_ONLY, true);
	}
	
	public boolean isBalancePlusBounty() {
		return config.getBoolean(BALANCE_PLUS_BOUNTY, true);
	}
}
