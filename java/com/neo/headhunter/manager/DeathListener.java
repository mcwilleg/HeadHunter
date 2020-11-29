package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.manager.block.HeadBlockManager;
import com.neo.headhunter.manager.head.HeadDrop;
import com.neo.headhunter.manager.support.factions.FactionsHook;
import com.neo.headhunter.util.message.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;
import java.util.logging.Level;

@RequiredArgsConstructor
public final class DeathListener implements Listener {
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private final HeadHunter plugin;
	
	// Death listener for all entities
	@EventHandler(ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity victim = event.getEntity();
		
		if (!plugin.getWorldManager().isValidWorld(victim.getWorld())) {
			return;
		}
		
		if (plugin.getEntityManager().isFromSpawner(victim) && !plugin.getSettings().isDropSpawnerMobs()) {
			return;
		}
		
		Player hunter = null;
		ItemStack weapon = null;
		
		EntityDamageEvent lastDamageCause = victim.getLastDamageCause();
		if (lastDamageCause == null) {
			return;
		}
		
		if (lastDamageCause instanceof EntityDamageByEntityEvent) {
			// victim was killed by an entity
			
			EntityDamageByEntityEvent lastDamageEntityCause = (EntityDamageByEntityEvent) lastDamageCause;
			if (lastDamageEntityCause.getDamager() instanceof Player) {
				// victim was killed by a player
				
				hunter = (Player) lastDamageEntityCause.getDamager();
				PlayerInventory inv = hunter.getInventory();
				weapon = inv.getItem(inv.getHeldItemSlot());
			} else if (lastDamageEntityCause.getDamager() instanceof Projectile) {
				// victim was killed by a projectile
				
				// check drop-projectiles option
				if (!plugin.getSettings().isDropProjectiles()) {
					return;
				}
				
				Projectile projectile = (Projectile) lastDamageEntityCause.getDamager();
				if (projectile.getShooter() instanceof Player) {
					// victim was killed by a projectile launched by a player
					
					hunter = (Player) projectile.getShooter();
					weapon = plugin.getEntityManager().getProjectileWeapon(projectile);
				}
			}
		} else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
			// victim was killed by burning
			if (plugin.getSettings().isDropFireDamage()) {
				// assign kill credit for killing with Flame or Fire Aspect
				hunter = plugin.getEntityManager().getCombuster(victim);
				weapon = plugin.getEntityManager().getCombusterWeapon(victim);
			}
			plugin.getEntityManager().stopFireTickTimer(victim);
		}
		
		// check player-kills-only option
		if (hunter == null && plugin.getSettings().isPlayerKillsOnly()) {
			return;
		}
		
		// check the factions plugin if there is one and the victim is a player
		FactionsHook factionsHook = plugin.getFactionsHook();
		if (factionsHook != null && victim instanceof Player) {
			if (!factionsHook.isValidTerritory((Player) victim)) {
				return;
			}
			if (!factionsHook.isValidZone(victim.getLocation())) {
				return;
			}
			if (!factionsHook.isValidHunter(hunter, (Player) victim)) {
				return;
			}
		}
		
		// perform drop rate check and drop
		HeadDrop headDrop = HeadDrop.create(plugin, hunter, weapon, victim);
		
		// if base head is null
		if (headDrop.getBaseHead() == null) {
			return;
		}
		
		// if worthless drops are disabled, assert the head is valuable
		if (!plugin.getSettings().isDropWorthless() && headDrop.isWorthless()) {
			return;
		}

		double success = RANDOM.nextDouble();
		if (HeadHunter.DEBUG) {
			plugin.log(Level.INFO, String.format(
					"%.1f%% chance to drop %s head: %s (%.1f%%)",
					headDrop.getDropChance() * 100,
					victim.getType().name(),
					(success < headDrop.getDropChance()) ? "SUCCESS" : "FAILURE",
					success * 100
			));
		}

		// check drop chance
		if (!(success < headDrop.getDropChance())) {
			return;
		}
		
		Location dropLocation = victim.getEyeLocation();
		
		if (victim instanceof Player) {
			// manipulate money if a player died
			if (!headDrop.isStealOnSell() && headDrop.getStolenValue() > 0) {
				plugin.getEconomy().withdrawPlayer((Player) victim, headDrop.getStolenValue());
			}
			
			// manipulate bounties if the killer is a player
			if (hunter != null) {
				double bounty = plugin.getBountyManager().removeTotalBounty((Player) victim);
				plugin.getSignBlockManager().requestUpdate();
				if (bounty > 0) {
					// check drop location setting
					OfflinePlayer topHunterOffline = headDrop.getTopHunter();
					if (plugin.getSettings().isTopHunterMode() && topHunterOffline != null) {
						Player topHunter = topHunterOffline.getPlayer();
						if (topHunter != null) {
							dropLocation = topHunter.getEyeLocation();
						}
					}
					
					// check broadcast setting and message
					String hName = hunter.getName(), vName = victim.getName();
					if (plugin.getSettings().isBroadcastClaim()) {
						Message.BOUNTY_BROADCAST_CLAIM.broadcast(plugin, hName, bounty, vName);
					} else {
						Message.BOUNTY_CLAIM.send(plugin, hunter, bounty, vName);
					}
				}
			}
		} else {
			// manipulate drops if a mob died
			for (ItemStack i : event.getDrops()) {
				if (HeadBlockManager.isHead(i)) {
					i.setType(Material.AIR);
				} else {
					victim.getWorld().dropItemNaturally(victim.getLocation(), i);
				}
			}
			event.getDrops().clear();
		}
		
		// drop head
		victim.getWorld().dropItemNaturally(dropLocation, headDrop.getFormattedHead());
	}
}
