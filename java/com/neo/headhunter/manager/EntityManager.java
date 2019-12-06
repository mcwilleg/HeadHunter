package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

public class EntityManager implements Listener {
	private static final String
			WEAPON_META_KEY = "headhunter_weapon",
			SPAWNER_ENTITY = "headhunter_spawner";
	
	private HeadHunter plugin;
	
	public EntityManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	ItemStack getWeapon(Projectile projectile) {
		if(projectile.hasMetadata(WEAPON_META_KEY)) {
			for(MetadataValue metaWeapon : projectile.getMetadata(WEAPON_META_KEY)) {
				if(metaWeapon.value() instanceof ItemStack)
					return (ItemStack) metaWeapon.value();
			}
		}
		return null;
	}
	
	boolean isFromSpawner(Entity entity) {
		if(entity.hasMetadata(SPAWNER_ENTITY)) {
			for(MetadataValue metaEntity : entity.getMetadata(SPAWNER_ENTITY)) {
				if(metaEntity.value() instanceof Boolean)
					return (boolean) metaEntity.value();
			}
		}
		return false;
	}
	
	// Tagging projectiles shot for kill credit
	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile p = event.getEntity();
		ProjectileSource s = p.getShooter();
		if(s instanceof Player) {
			Player hunter = (Player) s;
			PlayerInventory inv = hunter.getInventory();
			ItemStack projectileWeapon = inv.getItem(inv.getHeldItemSlot());
			p.setMetadata(WEAPON_META_KEY, new FixedMetadataValue(plugin, projectileWeapon));
		}
	}
	
	// Tagging entities spawned by a spawner
	@EventHandler(ignoreCancelled = true)
	public void onEntitySpawn(SpawnerSpawnEvent event) {
		Entity e = event.getEntity();
		e.setMetadata(SPAWNER_ENTITY, new FixedMetadataValue(plugin, true));
	}
}
