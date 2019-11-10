package com.neo.headhunter;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DropManager implements Listener {
	private static final DecimalFormat DF_MONEY = new DecimalFormat("$0.00");
	
	// chance of a hunter to collect the victim's head when killing
	private static final String STEAL_CHANCE_PERM = "hunter.steal-chance";
	private static final double DEFAULT_STEAL_CHANCE = 1.0;
	
	// chance of a victim to keep their head when killed
	private static final String DROP_CHANCE_PERM = "hunter.drop-chance";
	private static final double DEFAULT_DROP_CHANCE = 1.0;
	
	// the percentage of money stolen from a victim's balance
	private static final String STEAL_BALANCE_PERM = "hunter.steal-balance";
	private static final double DEFAULT_STEAL_BALANCE = 0.1;
	
	// the percentage of money kept in a victim's balance
	private static final String DROP_BALANCE_PERM = "hunter.drop-balance";
	private static final double DEFAULT_DROP_BALANCE = 1.0;
	
	// the amount each Looting level improves the hunter's collect chance
	private static final double LOOTING_EFFECT = 0.15;
	
	// the amount each Smite level increases the hunter's steal rate
	private static final double SMITE_EFFECT = 0.05;
	
	private HeadHunter plugin;
	
	DropManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	public double performHeadDrop(Player hunter, ItemStack weapon, LivingEntity victim) {
		double balanceValue = getBalanceValue(victim) * getStealRate(hunter, weapon, victim);
		double totalValue = balanceValue;
		double withdrawValue = balanceValue;
		if(victim instanceof Player) {
			double bountyValue = plugin.getBountyManager().getTotalBounty((Player) victim);
			if(bountyValue > 0) {
				if(plugin.getSettings().isBalancePlusBounty())
					totalValue += bountyValue;
				else {
					totalValue = bountyValue;
					withdrawValue = 0;
				}
			}
		}
		ItemStack headLoot = createHeadLoot(victim, totalValue);
		if(headLoot != null)
			victim.getWorld().dropItemNaturally(victim.getEyeLocation(), headLoot);
		return withdrawValue;
	}
	
	private ItemStack createHeadLoot(LivingEntity victim, double headPrice) {
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
	
	// chance (0.0 - 1.0) of 'victim' dropping a head if 'hunter' kills it with 'weapon'
	public double getDropChance(Player hunter, ItemStack weapon, LivingEntity victim) {
		double dropChance = getBaseStealChance(hunter);
		
		// hunter's weapon's looting effect
		if(weapon != null && weapon.containsEnchantment(Enchantment.LOOT_BONUS_MOBS))
			dropChance += (LOOTING_EFFECT * weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
		
		// victim's protection effect
		double victimChance;
		if(victim instanceof Player)
			victimChance = getBaseDropChance((Player) victim);
		else
			victimChance = plugin.getMobLibrary().getDropChance(victim, DEFAULT_DROP_CHANCE);
		return Math.min(1.0, dropChance * victimChance);
	}
	
	// proportion (0.0 - 1.0) of 'victim''s balance being imparted to a head if 'hunter' kills it with 'weapon'
	private double getStealRate(Player hunter, ItemStack weapon, LivingEntity victim) {
		double stealRate = getBaseStealBalance(hunter);
		
		// hunter's weapon's smite effect
		if(weapon != null && weapon.containsEnchantment(Enchantment.DAMAGE_UNDEAD))
			stealRate += (SMITE_EFFECT * weapon.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD));
		
		// victim's save effect
		double victimRate = 1;
		if(victim instanceof Player)
			victimRate = getBaseDropBalance((Player) victim);
		
		return Math.min(1.0, stealRate * victimRate);
	}
	
	private double getBalanceValue(LivingEntity victim) {
		if(victim instanceof Player)
			return plugin.getEconomy().getBalance((Player) victim);
		return plugin.getMobLibrary().getMaxPrice(victim, 0);
	}
	
	private double getBaseStealChance(Player hunter) {
		Double dropChance = getPermissionValue(hunter, STEAL_CHANCE_PERM);
		return dropChance != null ? dropChance : DEFAULT_STEAL_CHANCE;
	}
	
	private double getBaseDropChance(Player victim) {
		Double protectChance = getPermissionValue(victim, DROP_CHANCE_PERM);
		return protectChance != null ? protectChance : DEFAULT_DROP_CHANCE;
	}

	private double getBaseStealBalance(Player hunter) {
		Double stealRate = getPermissionValue(hunter, STEAL_BALANCE_PERM);
		return stealRate != null ? stealRate : DEFAULT_STEAL_BALANCE;
	}
	
	private double getBaseDropBalance(Player victim) {
		Double saveRate = getPermissionValue(victim, DROP_BALANCE_PERM);
		return saveRate != null ? saveRate : DEFAULT_DROP_BALANCE;
	}
	
	// Generic method for checking permissions regardless of op
	private Double getPermissionValue(Player p, String checkPermission) {
		if(p != null) {
			for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
				String regex = "\\Q" + checkPermission + ".\\E";
				if (pai.getPermission().matches(regex + "\\d+([.]\\d+)?"))
					return Double.valueOf(pai.getPermission().replaceFirst(regex, ""));
			}
		}
		return null;
	}
}
