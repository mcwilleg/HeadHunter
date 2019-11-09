package com.neo.headhunter;

final class Settings extends ConfigAccessor {
	private static final String PLAYER_KILLS_ONLY = "player-kills-only";
	
	Settings(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isPlayerKillsOnly() {
		return config.getBoolean(PLAYER_KILLS_ONLY, true);
	}
	
	public void setPlayerKillsOnly(boolean playerKillsOnly) {
		config.set(PLAYER_KILLS_ONLY, playerKillsOnly);
		saveConfig();
	}
}
