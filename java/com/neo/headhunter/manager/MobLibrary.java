package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobLibrary extends ConfigAccessor {
	private Map<String, ItemStack> library;
	
	public MobLibrary(HeadHunter plugin) {
		super(plugin, true, "mobs.yml", "data");
		initLibrary();
	}
	
	public double getDropChance(LivingEntity victim, double def) {
		String path = getConfigPath(victim);
		if(path != null)
			return config.getDouble(path + ".protect-chance", def);
		return def;
	}
	
	public double getMaxPrice(LivingEntity victim, double def) {
		String path = getConfigPath(victim);
		if(path != null)
			return config.getDouble(path + ".max-price", def);
		return def;
	}
	
	// returns an ItemStack head object corresponding to the victim LivingEntity
	// the returned ItemStack will include a colorless display name, and no lore or price
	public ItemStack getBaseHead(LivingEntity victim) {
		String path = getConfigPath(victim);
		if(path != null) {
			if(path.equals("PLAYER"))
				return getPlayerHead((Player) victim);
			else
				return getMobHead(path);
		}
		return null;
	}
	
	// returns a new ItemStack head object for the given player
	public ItemStack getPlayerHead(OfflinePlayer owner) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		if(head.hasItemMeta()) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(meta != null) {
				meta.setOwningPlayer(owner);
				meta.setDisplayName(owner.getName() + "\'s Head");
				head.setItemMeta(meta);
			}
		}
		return head;
	}
	
	public ItemStack getMobHead(String mobConfigPath) {
		ItemStack result = library.get(mobConfigPath);
		if(result != null)
			result = result.clone();
		return result;
	}
	
	// returns the config mob path used to create the specified head item
	public String getMobPath(ItemStack head) {
		for(Map.Entry<String, ItemStack> entry : library.entrySet()) {
			ItemMeta headMeta = head.getItemMeta(), libMeta = entry.getValue().getItemMeta();
			if(headMeta != null && libMeta != null) {
				String headName = ChatColor.stripColor(headMeta.getDisplayName());
				String libName = ChatColor.stripColor(libMeta.getDisplayName());
				if(headName.equals(libName))
					return entry.getKey();
			}
		}
		return null;
	}
	
	private String getConfigPath(LivingEntity victim) {
		if(victim != null) {
			String type = victim.getType().name();
			if(!type.equals("PLAYER")) {
				String variant = getVariant(victim);
				if(variant != null)
					return type + "." + variant;
			}
			return type;
		}
		return null;
	}
	
	private String getVariant(LivingEntity victim) {
		if(victim != null) {
			String type = victim.getType().name();
			if(isVariantMob(type)) {
				switch(type) {
				case "CAT":
					return ((Cat) victim).getCatType().name();
				case "FOX":
					return ((Fox) victim).getFoxType().name();
				case "HORSE":
					return ((Horse) victim).getColor().name();
				case "LLAMA":
				case "TRADER_LLAMA":
					return ((Llama) victim).getColor().name();
				case "MUSHROOM_COW":
					return ((MushroomCow) victim).getVariant().name();
				case "PANDA":
					Panda victimPanda = (Panda) victim;
					Panda.Gene mainGene = victimPanda.getMainGene();
					Panda.Gene hiddenGene = victimPanda.getHiddenGene();
					if(mainGene == Panda.Gene.BROWN && mainGene == hiddenGene)
						return Panda.Gene.BROWN.name();
					return Panda.Gene.NORMAL.name();
				case "PARROT":
					return ((Parrot) victim).getVariant().name();
				case "RABBIT":
					return ((Rabbit) victim).getRabbitType().name();
				case "VILLAGER":
					return ((Villager) victim).getVillagerType().name();
				case "ZOMBIE_VILLAGER":
					return Villager.Type.PLAINS.name();
				}
			}
		}
		return null;
	}
	
	// caches default mob heads from internal resource
	private void initLibrary() {
		library = new HashMap<>();
		InputStream resource = plugin.getResource("mob_db.yml");
		if(resource != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			for(String key : config.getKeys(false)) {
				if(isVariantMob(key)) {
					ConfigurationSection variantSection = config.getConfigurationSection(key);
					if(variantSection != null) {
						for (String variantKey : variantSection.getKeys(false)) {
							String path = key + "." + variantKey;
							library.put(path, config.getItemStack(path));
						}
					}
				} else
					library.put(key, config.getItemStack(key));
			}
		}
	}
	
	// helper method to improve readability
	private boolean isVariantMob(String entityType) {
		return VARIANT_MOBS.contains(entityType);
	}
	
	private static final List<String> VARIANT_MOBS = Arrays.asList(
			"CAT", // 11 types
			"FOX", // 2 types
			"HORSE", // 7 types
			"LLAMA", // 4 types
			"MUSHROOM_COW", // 2 types
			"PANDA", // 2 types
			"PARROT", // 5 types
			"RABBIT", // 7 types
			"TRADER_LLAMA", // 4 types
			"VILLAGER", // 7 types
			"ZOMBIE_VILLAGER" // 7 types
	);
}
