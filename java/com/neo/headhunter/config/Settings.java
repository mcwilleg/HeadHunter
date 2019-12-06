package com.neo.headhunter.config;

import com.neo.headhunter.HeadHunter;

public final class Settings extends ConfigAccessor<HeadHunter> {
	private static final String
			BROADCAST_SELL = "head.broadcast-sell",
			DROP_WORTHLESS = "head.drop-worthless",
			WORTHLESS_VALUE = "head.worthless-value",
			CUMULATIVE_VALUE = "head.cumulative-value",
			PLAYER_KILLS_ONLY = "head.player-kills-only",
			TOP_HUNTER_MODE = "head.give-to-top-hunter",
			DROP_SPAWNER_MOBS = "head.drop-spawner-mobs",
			DROP_PROJECTILES = "head.drop-projectiles",
			DROP_FIRE_DAMAGE = "head.drop-fire-damage";
	
	private static final String
			MINIMUM_BOUNTY = "bounty.minimum-value",
			BROADCAST_SET = "bounty.broadcast-set",
			BROADCAST_CLAIM = "bounty.broadcast-claim";
	
	private static final String
			LOOTING_EFFECT = "enchantments.looting-effect",
			SMITE_EFFECT = "enchantments.smite-effect";
	
	public Settings(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isBroadcastSell() {
		return config.getBoolean(BROADCAST_SELL, false);
	}
	
	public boolean isDropWorthless() {
		return config.getBoolean(DROP_WORTHLESS, true);
	}
	
	public double getWorthlessValue() {
		return config.getDouble(WORTHLESS_VALUE, 0);
	}
	
	public boolean isCumulativeValue() {
		return config.getBoolean(CUMULATIVE_VALUE, true);
	}
	
	public boolean isPlayerKillsOnly() {
		return config.getBoolean(PLAYER_KILLS_ONLY, true);
	}
	
	public boolean isTopHunterMode() {
		return config.getBoolean(TOP_HUNTER_MODE, false);
	}
	
	public boolean isDropSpawnerMobs() {
		return config.getBoolean(DROP_SPAWNER_MOBS, true);
	}
	
	public boolean isDropProjectiles() {
		return config.getBoolean(DROP_PROJECTILES, true);
	}
	
	public boolean isDropFireDamage() {
		return config.getBoolean(DROP_FIRE_DAMAGE, true);
	}
	
	public double getMinimumBounty() {
		return config.getDouble(MINIMUM_BOUNTY, 20);
	}
	
	public boolean isBroadcastSet() {
		return config.getBoolean(BROADCAST_SET, true);
	}
	
	public boolean isBroadcastClaim() {
		return config.getBoolean(BROADCAST_CLAIM, true);
	}
	
	// the amount each Looting level improves the hunter's collect chance
	public double getLootingEffect() {
		return config.getDouble(LOOTING_EFFECT, 0.8);
	}
	
	// the amount each Smite level increases the hunter's steal rate
	public double getSmiteEffect() {
		return config.getDouble(SMITE_EFFECT, 0.05);
	}
}
