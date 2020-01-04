package com.neo.headhunter.config;

import com.neo.headhunter.HeadHunter;

public final class Settings extends ConfigAccessor<HeadHunter> {
	private static final String
			STEAL_ON_SELL = "head.steal-on-sell",
			BROADCAST_SELL = "head.broadcast-sell",
			DROP_WORTHLESS = "head.drop-worthless",
			WORTHLESS_VALUE = "head.worthless-value",
			PLAYER_KILLS_ONLY = "head.player-kills-only",
			TOP_HUNTER_MODE = "head.give-to-top-hunter",
			DROP_SPAWNER_MOBS = "head.drop-spawner-mobs",
			DROP_PROJECTILES = "head.drop-projectiles",
			DROP_FIRE_DAMAGE = "head.drop-fire-damage";
	
	private static final String
			FORMAT_HEAD_VALUE = "head.format.value",
			FORMAT_HEAD_BOUNTY = "head.format.bounty",
			FORMAT_WORTHLESS = "head.format.worthless";
	
	private static final String
			MINIMUM_BOUNTY = "bounty.minimum-value",
			BOUNTY_COOLDOWN = "bounty.place-cooldown",
			BROADCAST_SET = "bounty.broadcast-place",
			BROADCAST_CLAIM = "bounty.broadcast-claim";
	
	private static final String
			LOOTING_EFFECT = "enchantments.looting-effect",
			SMITE_EFFECT = "enchantments.smite-effect";
	
	public Settings(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isStealOnSell() {
		return config.getBoolean(STEAL_ON_SELL, false);
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
	
	public String getHeadValueFormat() {
		return config.getString(FORMAT_HEAD_VALUE, "&eHead Value");
	}
	
	public String getHeadBountyFormat() {
		return config.getString(FORMAT_HEAD_BOUNTY, "&eBounty");
	}
	
	public String getWorthlessFormat() {
		return config.getString(FORMAT_WORTHLESS, "&r&c&oWorthless");
	}
	
	public double getMinimumBounty() {
		return config.getDouble(MINIMUM_BOUNTY, 20);
	}
	
	public long getBountyCooldown() {
		return config.getLong(BOUNTY_COOLDOWN, 300L);
	}
	
	public boolean isBroadcastPlace() {
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
