package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.manager.support.FactionsHook;
import com.neo.headhunter.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DeathListener implements Listener {
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private HeadHunter plugin;
	
	public DeathListener(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	// Death listener for all entities
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity victim = event.getEntity();
		
		if(!plugin.getWorldManager().isValidWorld(victim.getWorld()))
			return;
		
		Player hunter = null;
		ItemStack weapon = null;
		
		EntityDamageEvent lastDamageCause = victim.getLastDamageCause();
		if(lastDamageCause instanceof EntityDamageByEntityEvent) {
			// victim was killed by an entity
			
			EntityDamageByEntityEvent lastDamageEntityCause = (EntityDamageByEntityEvent) lastDamageCause;
			if(lastDamageEntityCause.getDamager() instanceof Player) {
				// victim was killed by a player
				
				hunter = (Player) lastDamageEntityCause.getDamager();
				weapon = hunter.getInventory().getItemInMainHand();
			} else if(lastDamageEntityCause.getDamager() instanceof Projectile) {
				// victim was killed by a projectile
				
				Projectile projectile = (Projectile) lastDamageEntityCause.getDamager();
				if(projectile.getShooter() instanceof Player) {
					// victim was killed by a projectile launched by a player
					
					hunter = (Player) projectile.getShooter();
					weapon = plugin.getProjectileManager().getWeapon(projectile);
				}
			}
		}
		
		// check player-kills-only option
		if(hunter != null || !plugin.getSettings().isPlayerKillsOnly()) {
			
			// check the factions plugin if there is one
			FactionsHook factionsHook = plugin.getFactionsHook();
			if(factionsHook != null && victim instanceof Player) {
				if(!factionsHook.isValidTerritory((Player) victim))
					return;
				if(!factionsHook.isValidZone(victim.getLocation()))
					return;
			}
			
			if(RANDOM.nextDouble() < plugin.getDropManager().getDropChance(hunter, weapon, victim)) {
				double withdrawValue = plugin.getDropManager().performHeadDrop(hunter, weapon, victim);
				
				// manipulate money if a player died
				if(victim instanceof Player) {
					if(withdrawValue > 0)
						plugin.getEconomy().withdrawPlayer((Player) victim, withdrawValue);
					
					// manipulate bounties if the killer is a player
					if(hunter != null) {
						double bounty = plugin.getBountyManager().removeTotalBounty((Player) victim);
						if (bounty > 0) {
							if (plugin.getSettings().isBountyBroadcast())
								Bukkit.broadcastMessage(Message.BOUNTY_BROADCAST_CLAIM.format(hunter.getName(), bounty, victim.getName()));
							else
								hunter.sendMessage(Message.BOUNTY_CLAIM.format(bounty, victim.getName()));
						}
					}
				}
			}
		}
	}
}
