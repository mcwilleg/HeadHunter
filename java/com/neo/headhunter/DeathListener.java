package com.neo.headhunter;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
		if(victim.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) victim.getLastDamageCause();
			if(damageEvent.getDamager() instanceof Player) {
				Player hunter = (Player) damageEvent.getDamager();
				ItemStack weapon = hunter.getInventory().getItemInMainHand();
				plugin.getDropManager().performHeadDrop(hunter, weapon, victim);
			}
		}
	}
}
