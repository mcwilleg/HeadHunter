package com.neo.headhunter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class ItemManager implements Listener {
	private static final DecimalFormat DF_MONEY = new DecimalFormat("$0.00");
	private static final String DROP_CHANCE_PERM = "hunter.drop-chance", STEAL_RATE_PERM = "hunter.steal-rate";
	private static final double DEFAULT_DROP_CHANCE = 1.0, DEFAULT_STEAL_RATE = 0.1;
	
	private HeadHunter plugin;
	
	ItemManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	// Creates a new ItemStack with the specified owner and monetary value
	public ItemStack createPlayerHead(Player victim, double value) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		if(head.hasItemMeta()) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(meta == null)
				return head;
			meta.setOwningPlayer(victim);
			meta.setDisplayName(ChatColor.DARK_AQUA + victim.getName() + "\'s Head");
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.DARK_GRAY + "Sell Price: " + ChatColor.GOLD + DF_MONEY.format(value));
			meta.setLore(lore);
			head.setItemMeta(meta);
		}
		return head;
	}

	private double getDropChance(Player hunter) {
		Double dropChance = getPermissionValue(hunter, DROP_CHANCE_PERM);
		return dropChance != null ? dropChance : DEFAULT_DROP_CHANCE;
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

	private double getStealRate(Player hunter) {
		Double stealRate = getPermissionValue(hunter, STEAL_RATE_PERM);
		return stealRate != null ? stealRate : DEFAULT_STEAL_RATE;
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
}
