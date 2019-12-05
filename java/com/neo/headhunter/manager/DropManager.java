package com.neo.headhunter.manager;

import com.neo.headhunter.HeadHunter;
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
	
	private HeadHunter plugin;
	
	public DropManager(HeadHunter plugin) {
		this.plugin = plugin;
	}
	
	HeadDrop getHeadDrop(Player hunter, ItemStack weapon, LivingEntity victim) {
		return new HeadDrop(hunter, weapon, victim);
	}
	
	public ItemStack formatHead(ItemStack baseHead, double headPrice) {
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
	
	private double getLootingEffect(ItemStack weapon) {
		// hunter's weapon's looting effect
		if(weapon != null && weapon.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
			double lootingEffect = plugin.getSettings().getLootingEffect();
			return lootingEffect * weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
		}
		return 0;
	}
	
	private double getPlayerDropChance(Player hunter, ItemStack weapon, Player victim) {
		double stealChance = getBasePlayerStealChance(hunter);
		stealChance += getLootingEffect(weapon);
		double dropChance = getBasePlayerDropChance(victim);
		return Math.min(1.0, stealChance * dropChance);
	}
	
	private double getMobDropChance(Player hunter, ItemStack weapon, String mobConfigPath) {
		double stealChance = getBaseMobStealChance(hunter, mobConfigPath);
		stealChance += getLootingEffect(weapon);
		return Math.min(1.0, stealChance);
	}
	
	// proportion (0.0 - 1.0) of 'victim''s balance being imparted to a head if 'hunter' kills it with 'weapon'
	private double getStealRate(Player hunter, ItemStack weapon, LivingEntity victim) {
		double stealRate = getBaseStealBalance(hunter, victim);
		
		// hunter's weapon's smite effect
		if(weapon != null && weapon.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) {
			double smiteEffect = plugin.getSettings().getSmiteEffect();
			stealRate += (smiteEffect * weapon.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD));
		}
		
		// victim's save effect
		double victimRate = 1;
		if(victim instanceof Player)
			victimRate = getBaseDropBalance((Player) victim);
		
		return Math.min(1.0, stealRate * victimRate);
	}
	
	private double getBasePlayerStealChance(Player hunter) {
		Double stealChance = getPermissionValue(hunter, STEAL_CHANCE_PERM);
		return stealChance != null ? stealChance : DEFAULT_STEAL_CHANCE;
	}
	
	private double getBasePlayerDropChance(Player victim) {
		Double dropChance = getPermissionValue(victim, DROP_CHANCE_PERM);
		return dropChance != null ? dropChance : DEFAULT_DROP_CHANCE;
	}
	
	private double getBaseMobStealChance(Player hunter, String mobConfigPath) {
		if(mobConfigPath != null) {
			String stealChancePerm = String.join(".", STEAL_CHANCE_PERM, mobConfigPath.toLowerCase());
			Double stealChance = getPermissionValue(hunter, stealChancePerm);
			if(stealChance != null)
				return stealChance;
		}
		return plugin.getHeadLibrary().getDropChance(mobConfigPath);
	}
	
	private double getBaseStealBalance(Player hunter, LivingEntity victim) {
		String stealBalancePerm = STEAL_BALANCE_PERM;
		if(!(victim instanceof Player)) {
			String mobConfigPath = plugin.getHeadLibrary().getConfigPath(victim);
			if(mobConfigPath != null)
				stealBalancePerm = String.join(".", stealBalancePerm, mobConfigPath.toLowerCase());
		}
		Double stealBalance = getPermissionValue(hunter, stealBalancePerm);
		return stealBalance != null ? stealBalance : DEFAULT_STEAL_BALANCE;
	}
	
	private double getBaseDropBalance(Player victim) {
		Double dropBalance = getPermissionValue(victim, DROP_BALANCE_PERM);
		return dropBalance != null ? dropBalance : DEFAULT_DROP_BALANCE;
	}
	
	// Generic method for checking permissions regardless of op
	private Double getPermissionValue(Player p, String checkPermission) {
		if(p != null) {
			for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
				String regex = "\\Q" + checkPermission + ".\\E";
				String permission = pai.getPermission().toLowerCase();
				if (permission.matches(regex + "\\d+([.]\\d+)?"))
					return Double.valueOf(permission.replaceFirst(regex, ""));
			}
		}
		return null;
	}
	
	class HeadDrop {
		private final Player hunter;
		private final ItemStack weapon;
		private final LivingEntity victim;
		
		private ItemStack baseHead;
		private double sellValue = 0, balanceValue = 0, bountyValue = 0, withdrawValue = 0;
		private double dropChance = 0;
		
		HeadDrop(Player hunter, ItemStack weapon, LivingEntity victim) {
			this.hunter = hunter;
			this.weapon = weapon;
			this.victim = victim;
			init();
		}
		
		private void init() {
			if(victim instanceof Player) {
				Player victimPlayer = (Player) victim;
				baseHead = plugin.getHeadLibrary().getPlayerHead(victimPlayer);
				balanceValue = plugin.getEconomy().getBalance(victimPlayer);
				bountyValue = plugin.getBountyManager().getTotalBounty(victimPlayer);
				withdrawValue = balanceValue;
				if(plugin.getSettings().isCumulativeValue())
					sellValue = balanceValue + bountyValue;
				else if(bountyValue > 0) {
					sellValue = bountyValue;
					withdrawValue = 0;
				} else
					sellValue = balanceValue;
				if(sellValue < plugin.getSettings().getWorthlessValue()) {
					sellValue = 0;
					withdrawValue = 0;
				}
				dropChance = getPlayerDropChance(hunter, weapon, victimPlayer);
			} else {
				String mobConfigPath = plugin.getHeadLibrary().getConfigPath(victim);
				baseHead = plugin.getHeadLibrary().getMobHead(mobConfigPath);
				balanceValue = plugin.getHeadLibrary().getMaxPrice(mobConfigPath);
				dropChance = getMobDropChance(hunter, weapon, mobConfigPath);
			}
		}
		
		ItemStack getBaseHead() {
			return baseHead;
		}
		
		double getSellValue() {
			return sellValue;
		}
		
		double getWithdrawValue() {
			return withdrawValue;
		}
		
		double getDropChance() {
			return dropChance;
		}
	}
}
