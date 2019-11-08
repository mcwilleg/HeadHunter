package com.neo.headhunter;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.projectiles.ProjectileSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropManager implements Listener {
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private static final DecimalFormat DF_MONEY = new DecimalFormat("$0.00");
	
	// chance of a hunter to collect the victim's head when killing
	private static final String DROP_CHANCE_PERM = "hunter.drop-chance";
	private static final double DEFAULT_DROP_CHANCE = 1.0;
	
	// chance of a victim to keep their head when killed
	private static final String PROTECT_CHANCE_PERM = "hunter.protect-chance";
	private static final double DEFAULT_PROTECT_CHANCE = 0.0;
	
	// the percentage of money stolen from a victim's balance
	private static final String STEAL_RATE_PERM = "hunter.steal-rate";
	private static final double DEFAULT_STEAL_RATE = 0.1;
	
	// the percentage of money kept in a victim's balance
	private static final String SAVE_RATE_PERM = "hunter.save-rate";
	private static final double DEFAULT_SAVE_RATE = 0.0;
	
	private HeadHunter plugin;
	
	DropManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	// Death listener for all entities
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity victim = event.getEntity();
		if(victim.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) victim.getLastDamageCause();
			if(damageEvent.getDamager() instanceof Player) {
				// Damage was caused by player
				if(victim instanceof Player) {
					// Victim was player
				} else {
					// Victim was mob
				}
			}
		}
	}
	
	private void dropHead(Player hunter, LivingEntity victim) {
		double headPrice = getHeadPrice(hunter, victim);
		ItemStack headLoot = createHeadDrop(victim, headPrice);
		if(headLoot != null)
			victim.getWorld().dropItemNaturally(victim.getEyeLocation(), headLoot);
	}
	
	private ItemStack createHeadDrop(LivingEntity victim, double headPrice) {
		ItemStack baseHead = plugin.getMobLibrary().getBaseHead(victim);
		if(baseHead != null) {
			SkullMeta meta = (SkullMeta) baseHead.getItemMeta();
			if (meta != null) {
				// get colors from settings here
				ChatColor displayNameColor = ChatColor.DARK_AQUA;
				ChatColor textColor = ChatColor.DARK_GRAY;
				ChatColor valueColor = ChatColor.GOLD;
				
				meta.setDisplayName(displayNameColor + meta.getDisplayName());
				// don't attach lore if the head is worthless
				if(headPrice > 0) {
					List<String> lore = new ArrayList<>();
					lore.add(textColor + "Sell Price: " + valueColor + DF_MONEY.format(headPrice));
					meta.setLore(lore);
				}
				baseHead.setItemMeta(meta);
			}
		}
		return baseHead;
	}
	
	private double getDropChance(Player hunter, LivingEntity victim) {
		return 1;
	}
	
	private double getHeadPrice(Player hunter, LivingEntity victim) {
		double stealRate = getStealRate(hunter);
		if(victim instanceof Player) {
			double victimBalance = plugin.getEconomy().getBalance((Player) victim);
			return victimBalance * stealRate;
		} else {
			// return total value of the mob * stealRate
		}
		return 0;
	}
	
	private double getDropChance(Player hunter) {
		Double dropChance = getPermissionValue(hunter, DROP_CHANCE_PERM);
		return dropChance != null ? dropChance : DEFAULT_DROP_CHANCE;
	}
	
	private double getProtectChance(Player victim) {
		Double protectChance = getPermissionValue(victim, PROTECT_CHANCE_PERM);
		return protectChance != null ? protectChance : DEFAULT_PROTECT_CHANCE;
	}

	private double getStealRate(Player hunter) {
		Double stealRate = getPermissionValue(hunter, STEAL_RATE_PERM);
		return stealRate != null ? stealRate : DEFAULT_STEAL_RATE;
	}
	
	private double getSaveRate(Player victim) {
		Double saveRate = getPermissionValue(victim, SAVE_RATE_PERM);
		return saveRate != null ? saveRate : DEFAULT_SAVE_RATE;
	}
	
	// Generic method for checking permissions
	private Double getPermissionValue(Player p, String checkPermission) {
		for(PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
			String regex = "\\Q" + checkPermission + ".\\E";
			if(pai.getPermission().matches(regex + "\\d+([.]\\d+)?"))
				return Double.valueOf(pai.getPermission().replaceFirst(checkPermission, ""));
		}
		return null;
	}

	// Tagging arrows that hit for kill credit
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile p = event.getEntity();
		ProjectileSource s = p.getShooter();
		if(s instanceof Player)
			p.setMetadata("headhunter_shooter", new FixedMetadataValue(plugin, ((Player) s).getUniqueId()));
	}
}
