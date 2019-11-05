package com.neo.headhunter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemManager implements Listener {
	private static final DecimalFormat DF_MONEY = new DecimalFormat("$0.00");
	
	private HeadHunter plugin;
	
	ItemManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	public ItemStack createPlayerHead(Player victim, double value) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwningPlayer(victim);
		meta.setDisplayName(ChatColor.DARK_AQUA + victim.getName() + "\'s Head");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GRAY + "Sell Price: " + ChatColor.GOLD + DF_MONEY.format(value));
		meta.setLore(lore);
		head.setItemMeta(meta);
		return head;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile p = event.getEntity();
		ProjectileSource s = p.getShooter();
		if(s instanceof Player)
			p.setMetadata("headhunter_shooter", new FixedMetadataValue(plugin, ((Player) s).getUniqueId()));
	}
}
