package com.neo.headhunter;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HeadHunter extends JavaPlugin implements Listener {
	public static final boolean DEBUG = true;
	
	private ItemManager itemManager;
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		itemManager = new ItemManager(this);
		registerEvents();
		getLogger().log(Level.INFO, getDescription().getName() + " v" + getDescription().getVersion() + " enabled.");
	}
	
	@Override
	public void onDisable() {
		// Plugin shutdown logic
		getLogger().log(Level.INFO, getDescription().getName() + " v" + getDescription().getVersion() + " disabled.");
	}
	
	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
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
	
	@EventHandler
	public void onDebug(PlayerInteractEvent event) {
		// Debug listener
		if(event.getAction() == Action.RIGHT_CLICK_AIR) {
			// debug statements here
			Player p = event.getPlayer();
			p.getWorld().dropItemNaturally(p.getEyeLocation(), itemManager.createPlayerHead(p, 10));
		}
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
}
