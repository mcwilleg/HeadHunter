package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class EntityManager implements Listener {
	private static final String
			WEAPON_META_KEY = "headhunter_weapon",
			SPAWNER_ENTITY = "headhunter_spawner";
	
	private HeadHunter plugin;
	private Map<LivingEntity, FireTickRunnable> burningTimers;
	
	public EntityManager(HeadHunter plugin) {
		this.plugin = plugin;
		this.burningTimers = new HashMap<>();
	}
	
	Player getCombuster(LivingEntity burning) {
		FireTickRunnable runnable = burningTimers.get(burning);
		if(runnable != null)
			return runnable.combuster;
		return null;
	}
	
	ItemStack getCombusterWeapon(LivingEntity burning) {
		FireTickRunnable runnable = burningTimers.get(burning);
		if(runnable != null)
			return runnable.weapon;
		return null;
	}
	
	void stopFireTickTimer(LivingEntity victim) {
		FireTickRunnable runnable = burningTimers.remove(victim);
		if(runnable != null)
			runnable.cancel();
	}
	
	ItemStack getProjectileWeapon(Projectile projectile) {
		if(projectile.hasMetadata(WEAPON_META_KEY)) {
			for(MetadataValue metaWeapon : projectile.getMetadata(WEAPON_META_KEY)) {
				if(metaWeapon.value() instanceof ItemStack)
					return (ItemStack) metaWeapon.value();
			}
		}
		return null;
	}
	
	boolean isFromSpawner(LivingEntity entity) {
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
	public void onSpawnerSpawn(SpawnerSpawnEvent event) {
		Entity e = event.getEntity();
		e.setMetadata(SPAWNER_ENTITY, new FixedMetadataValue(plugin, true));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		Player hunter = null;
		ItemStack weapon = null;
		Entity burning = event.getEntity();
		
		Entity combuster = event.getCombuster();
		if(combuster instanceof Projectile) {
			ProjectileSource source = (ProjectileSource) combuster;
			if(source instanceof Player) {
				hunter = (Player) source;
				weapon = getProjectileWeapon((Projectile) combuster);
			}
		} else if(combuster instanceof Player) {
			hunter = (Player) combuster;
			PlayerInventory inv = hunter.getInventory();
			weapon = inv.getItem(inv.getHeldItemSlot());
		}
		
		if(hunter != null && burning instanceof LivingEntity) {
			FireTickRunnable runnable = new FireTickRunnable(hunter, weapon, (LivingEntity) burning);
			burningTimers.put((LivingEntity) burning, runnable);
			runnable.runTaskTimer(plugin, 0L, 1L);
		}
	}
	
	private class FireTickRunnable extends BukkitRunnable {
		private final LivingEntity burning;
		
		private Player combuster;
		private ItemStack weapon;
		
		private FireTickRunnable(Player combuster, ItemStack weapon, LivingEntity burning) {
			this.burning = burning;
			
			this.combuster = combuster;
			this.weapon = weapon;
		}
		
		@Override
		public void run() {
			if(burning == null) {
				cancel();
				return;
			}
			if(burning.getFireTicks() == 0) {
				burningTimers.remove(burning);
				cancel();
			}
		}
	}
}
