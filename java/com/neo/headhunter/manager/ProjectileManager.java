package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileManager implements Listener {
	private static final String WEAPON_META_KEY = "headhunter_weapon";
	
	private HeadHunter plugin;
	
	public ProjectileManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	public ItemStack getWeapon(Projectile projectile) {
		if(projectile.hasMetadata(WEAPON_META_KEY)) {
			for(MetadataValue metaWeapon : projectile.getMetadata(WEAPON_META_KEY)) {
				if(metaWeapon.value() instanceof ItemStack)
					return (ItemStack) metaWeapon.value();
			}
		}
		return null;
	}
	
	// Tagging projectiles shot for kill credit
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile p = event.getEntity();
		ProjectileSource s = p.getShooter();
		if(s instanceof Player) {
			Player hunter = (Player) s;
			ItemStack projectileWeapon = hunter.getInventory().getItemInMainHand();
			p.setMetadata(WEAPON_META_KEY, new FixedMetadataValue(plugin, projectileWeapon));
		}
	}
}
