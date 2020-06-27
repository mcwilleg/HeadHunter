package com.neo.headhunter.manager.head;

import com.neo.headhunter.HeadHunter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class HeadDrop {
	private static final String DEFAULT_CURRENCY = "$";
	private static final DecimalFormat DF_MONEY = new DecimalFormat("0.00");
	private static final DecimalFormat DF_PERCENT = new DecimalFormat("0.0");
	
	private final HeadHunter plugin;
	
	private final Player hunter;
	private final ItemStack weapon;
	private final LivingEntity victim;
	
	private ItemStack baseHead;
	private double stealBalance, dropChance, stolenValue, bountyValue;
	private OfflinePlayer topHunter = null;
	private boolean stealOnSell = false;
	
	private HeadDrop(HeadHunter plugin, Player hunter, ItemStack weapon, LivingEntity victim) {
		this.plugin = plugin;
		this.hunter = hunter;
		this.weapon = weapon;
		this.victim = victim;
		initialize();
	}
	
	private void initialize() {
		double balance;
		if(victim instanceof Player) {
			Player victimPlayer = (Player) victim;
			baseHead = plugin.getHeadLibrary().getPlayerHead(victimPlayer);
			balance = plugin.getEconomy().getBalance(victimPlayer);
			stealBalance = plugin.getDropManager().getPlayerDropBalance(hunter, weapon, victimPlayer);
			dropChance = plugin.getDropManager().getPlayerDropChance(hunter, weapon, victimPlayer);
			
			// player victim specific
			bountyValue = plugin.getBountyManager().getTotalBounty(victimPlayer);
			topHunter = plugin.getBountyManager().getTopHunter(victimPlayer);
		} else {
			String mobConfigPath = plugin.getHeadLibrary().getConfigPath(victim);
			baseHead = plugin.getHeadLibrary().getMobHead(mobConfigPath);
			balance = plugin.getHeadLibrary().getMaxPrice(mobConfigPath);
			stealBalance = plugin.getDropManager().getMobDropBalance(hunter, weapon, mobConfigPath);
			dropChance = plugin.getDropManager().getMobDropChance(hunter, weapon, mobConfigPath);
		}
		stolenValue = balance * stealBalance;
		stealOnSell = plugin.getSettings().isStealOnSell();
	}
	
	// called when a head is dropped
	public ItemStack getFormattedHead() {
		if(baseHead != null) {
			ItemMeta meta = baseHead.getItemMeta();
			if(meta != null) {
				String balanceString;
				if (stealOnSell && victim instanceof Player)
					balanceString = DF_PERCENT.format(stealBalance * 100) + "%";
				else
					balanceString = DF_MONEY.format(stolenValue);
				
				String bountyString = null;
				if(hunter != null && bountyValue > 0)
					bountyString = DF_MONEY.format(bountyValue);
				
				return format(plugin, baseHead, balanceString, bountyString);
			}
		}
		return baseHead;
	}
	
	public boolean isWorthless() {
		return (stolenValue + bountyValue) < plugin.getSettings().getWorthlessValue();
	}
	
	public Player getHunter() {
		return hunter;
	}
	
	public ItemStack getWeapon() {
		return weapon;
	}
	
	public LivingEntity getVictim() {
		return victim;
	}
	
	public ItemStack getBaseHead() {
		return baseHead;
	}
	
	public void setBaseHead(ItemStack baseHead) {
		this.baseHead = baseHead;
	}
	
	public double getStealBalance() {
		return stealBalance;
	}
	
	public void setStealBalance(double stealBalance) {
		this.stealBalance = stealBalance;
	}
	
	public double getDropChance() {
		return dropChance;
	}
	
	public void setDropChance(double dropChance) {
		this.dropChance = dropChance;
	}
	
	public double getStolenValue() {
		return stolenValue;
	}
	
	public void setStolenValue(double stolenValue) {
		this.stolenValue = stolenValue;
	}
	
	public double getBountyValue() {
		return bountyValue;
	}
	
	public void setBountyValue(double bountyValue) {
		this.bountyValue = bountyValue;
	}
	
	public OfflinePlayer getTopHunter() {
		return topHunter;
	}
	
	public void setTopHunter(OfflinePlayer topHunter) {
		this.topHunter = topHunter;
	}
	
	public boolean isStealOnSell() {
		return stealOnSell;
	}
	
	public static HeadDrop create(HeadHunter plugin, Player hunter, ItemStack weapon, LivingEntity victim) {
		return new HeadDrop(plugin, hunter, weapon, victim);
	}
	
	public static ItemStack format(HeadHunter plugin, ItemStack baseHead, String balanceString, String bountyString) {
		if(baseHead != null) {
			ItemMeta meta = baseHead.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.DARK_AQUA + meta.getDisplayName());
				List<String> lore = new ArrayList<>();
				
				// currency symbol
				String currencySymbol = DEFAULT_CURRENCY;
				if(plugin.getEssentialsHook() != null)
					currencySymbol = plugin.getEssentialsHook().getCurrencySymbol();
				
				// balance value
				String balanceValueLore = plugin.getSettings().getHeadValueFormat() + ": &6";
				if (balanceString == null || balanceString.equals("0.00") || balanceString.equals("0.0%"))
					balanceValueLore += plugin.getSettings().getWorthlessFormat();
				else {
					if (!balanceString.endsWith("%"))
						balanceString = (currencySymbol + balanceString);
					balanceValueLore += balanceString;
				}
				lore.add(ChatColor.translateAlternateColorCodes('&', balanceValueLore));
				
				// bounty value
				String bountyValueLore = plugin.getSettings().getHeadBountyFormat() + ": &6";
				if (bountyString != null && !bountyString.equals("0.00")) {
					bountyString = (currencySymbol + bountyString);
					bountyValueLore += bountyString;
					lore.add(ChatColor.translateAlternateColorCodes('&', bountyValueLore));
				}
				
				meta.setLore(lore);
				baseHead.setItemMeta(meta);
				return baseHead;
			}
		}
		return null;
	}
	
	public static String formatMoney(HeadHunter plugin, double value) {
		String currencySymbol = DEFAULT_CURRENCY;
		if(plugin.getEssentialsHook() != null)
			currencySymbol = plugin.getEssentialsHook().getCurrencySymbol();
		return currencySymbol + DF_MONEY.format(value);
	}
}
