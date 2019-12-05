package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HeadLibrary extends ConfigAccessor<HeadHunter> {
	private Map<String, ItemStack> library;
	
	public HeadLibrary(HeadHunter plugin) {
		super(plugin, true, "mobs.yml");
		initLibrary();
	}
	
	double getMaxPrice(String mobConfigPath) {
		if(mobConfigPath != null)
			return config.getDouble(mobConfigPath + ".max-price", 0);
		return 0;
	}
	
	double getDropChance(String mobConfigPath) {
		if(mobConfigPath != null)
			return config.getDouble(mobConfigPath + ".drop-chance", 1);
		return 0;
	}
	
	// returns a new ItemStack head object for the given player
	@SuppressWarnings("deprecation")
	public ItemStack getPlayerHead(OfflinePlayer owner) {
		ItemStack head;
		if(plugin.isVersionBefore(1, 13, 0))
			head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
		else
			head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		if(meta != null) {
			if(plugin.isVersionBefore(1, 13, 0))
				meta.setOwner(owner.getName());
			else
				meta.setOwningPlayer(owner);
			String displayName = owner.getName() + "\'";
			if(!displayName.endsWith("s\'"))
				displayName += "s";
			meta.setDisplayName(displayName + " Head");
			head.setItemMeta(meta);
		}
		return head;
	}
	
	// returns a new ItemStack head object for the given mob path
	public ItemStack getMobHead(String mobConfigPath) {
		ItemStack result = library.get(mobConfigPath);
		return result == null ? null : result.clone();
	}
	
	// returns the mob config path used to create the specified head item
	public String getConfigPath(ItemStack head) {
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
	
	// returns the mob config path used to create a head for the specified victim
	@SuppressWarnings("deprecation")
	String getConfigPath(LivingEntity victim) {
		if(victim != null) {
			String type = victim.getType().name();
			String variant = null;
			
			switch(type) {
			case "CAT":
				variant = ((Cat) victim).getCatType().name();
				break;
			case "FOX":
				variant = ((Fox) victim).getFoxType().name();
				break;
			case "HORSE":
				variant = ((Horse) victim).getColor().name();
				break;
			case "LLAMA":
			case "TRADER_LLAMA":
				type = "LLAMA";
				variant = ((Llama) victim).getColor().name();
				break;
			case "MUSHROOM_COW":
				if(plugin.isVersionBefore(1, 14, 0))
					variant = "RED";
				else
					variant = ((MushroomCow) victim).getVariant().name();
				break;
			case "OCELOT":
				if(plugin.isVersionBefore(1, 14, 0)) {
					switch(((Ocelot) victim).getCatType().name()) {
					case "BLACK_CAT":
						type = "CAT";
						variant = "BLACK";
						break;
					case "RED_CAT":
						type = "CAT";
						variant = "RED";
						break;
					case "SIAMESE_CAT":
						type = "CAT";
						variant = "SIAMESE";
						break;
					}
				}
				break;
			case "PANDA":
				Panda victimPanda = (Panda) victim;
				Panda.Gene mainGene = victimPanda.getMainGene();
				Panda.Gene hiddenGene = victimPanda.getHiddenGene();
				if(mainGene == Panda.Gene.BROWN && mainGene == hiddenGene)
					variant = Panda.Gene.BROWN.name();
				else
					variant = Panda.Gene.NORMAL.name();
				break;
			case "PARROT":
				variant = ((Parrot) victim).getVariant().name();
				break;
			case "RABBIT":
				variant = ((Rabbit) victim).getRabbitType().name();
				break;
			case "SKELETON":
				switch(((Skeleton) victim).getSkeletonType().name()) {
				case "WITHER":
					type = "WITHER_SKELETON";
					break;
				case "STRAY":
					type = "STRAY";
					break;
				}
				break;
			case "VILLAGER":
				if(plugin.isVersionBefore(1, 14, 0))
					variant = "PLAINS";
				else
					variant = ((Villager) victim).getVillagerType().name();
				break;
			case "ZOMBIE":
				if(plugin.isVersionBefore(1, 13, 0)) {
					if(((Zombie) victim).isVillager()) {
						type = "ZOMBIE_VILLAGER";
						variant = "PLAINS";
					}
				}
				break;
			case "ZOMBIE_VILLAGER":
				variant = "PLAINS";
				break;
			}
			
			if(variant != null)
				return String.join(".", type, variant);
			return type;
		}
		return null;
	}
	
	// caches default mob heads from internal resource
	private void initLibrary() {
		library = new HashMap<>();
		InputStream resource;
		if(plugin.isVersionBefore(1, 9, 0))
			resource = plugin.getResource("mob_db_1_8.yml");
		else if(plugin.isVersionBefore(1, 13, 0))
			resource = plugin.getResource("mob_db_1_12.yml");
		else
			resource = plugin.getResource("mob_db.yml");
		if(resource != null) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			for(String key : config.getKeys(true)) {
				ItemStack head = config.getItemStack(key);
				if(head != null)
					library.put(key, head);
			}
		}
	}
}
