package com.neo.headhunter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobLibrary {
	private HeadHunter plugin;
	private boolean enabled;
	private Map<String, ItemStack> library;
	
	MobLibrary(HeadHunter plugin) {
		this.plugin = plugin;
		this.enabled = false;
		initLibrary();
	}
	
	public ItemStack getBaseHead(LivingEntity victim) {
		if(victim != null) {
			String type = victim.getType().toString();
			if(type.equals("PLAYER")) {
				Player victimPlayer = (Player) victim;
				// player head
			} else if(isVariantMob(type)) {
				// check variants
			} else {
				// return normal mob head
			}
		}
		return null;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private void initLibrary() {
		library = new HashMap<>();
		InputStream resource = plugin.getResource("mobs.yml");
		if(resource != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			for(String key : config.getKeys(false)) {
				if(isVariantMob(key)) {
					// check variants
				} else
					library.put(key, config.getItemStack(key));
			}
		}
	}
	
	// helper method to improve readability
	private boolean isVariantMob(String entityType) {
		return VARIANT_MOBS.contains(entityType);
	}
	
	private static final String[] ALL_MOBS = {
			"BAT",
			"BLAZE",
			"CAT",
			"CAVE_SPIDER",
			"COD",
			"COW",
			"CREEPER",
			"DOLPHIN",
			"DONKEY",
			"DROWNED",
			"ELDER_GUARDIAN",
			"ENDER_DRAGON",
			"ENDERMAN",
			"ENDERMITE",
			"EVOKER",
			"FOX",
			"GHAST",
			"GIANT",
			"GUARDIAN",
			"HORSE",
			"HUSK",
			"ILLUSIONER",
			"IRON_GOLEM",
			"LLAMA",
			"MAGMA_CUBE",
			"MULE",
			"MUSHROOM_COW",
			"OCELOT",
			"PANDA",
			"PARROT",
			"PHANTOM",
			"PIG",
			"PIG_ZOMBIE",
			"PILLAGER",
			"POLAR_BEAR",
			"PUFFERFISH",
			"RABBIT",
			"RAVAGER",
			"SALMON",
			"SHEEP",
			"SHULKER",
			"SILVERFISH",
			"SKELETON",
			"SKELETON_HORSE",
			"SLIME",
			"SNOWMAN",
			"SPIDER",
			"SQUID",
			"STRAY",
			"TRADER_LLAMA",
			"TROPICAL_FISH",
			"TURTLE",
			"VEX",
			"VILLAGER",
			"VINDICATOR",
			"WANDERING_TRADER",
			"WITCH",
			"WITHER",
			"WITHER_SKELETON",
			"WOLF",
			"ZOMBIE",
			"ZOMBIE_HORSE",
			"ZOMBIE_VILLAGER",
	};
	
	private static final List<String> VARIANT_MOBS = Arrays.asList(
			"CAT", // 11**
			"FOX", // 2**
			"HORSE", // 7**
			"LLAMA", // 4**
			"MUSHROOM_COW", // 2**
			"PANDA", // 2**
			"PARROT", // 5**
			"RABBIT", // 7**
			"TRADER_LLAMA", // 4**
			"VILLAGER", // 7*
			"ZOMBIE_VILLAGER" // 7*
	);
}
