package com.neo.headhunter;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HeadHunter extends JavaPlugin implements Listener, CommandExecutor {
	private static final boolean DEBUG = true;

	private Economy economy;
	private ItemManager itemManager;
	private MobLibrary mobLibrary;
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		economy = connectEconomy();
		if(economy == null) {
			getLogger().log(Level.SEVERE, "Could not connect to Vault. Make sure Vault is installed for HeadHunter!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		itemManager = new ItemManager(this);
		mobLibrary = new MobLibrary(this);
		saveDefaultConfig();
		registerEvents();
		registerCommands();
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(itemManager, this);
	}
	
	private void registerCommands() {
		getCommand("hunter").setExecutor(this);
	}
	
	private Economy connectEconomy() {
		if(Bukkit.getPluginManager().getPlugin("Vault") == null)
			return null;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			return null;
		
		return rsp.getProvider();
	}
	
	@EventHandler
	public void onDebug(PlayerInteractEvent event) {
		// Debug listener
		if(!DEBUG) return;
		
		if(event.getAction() == Action.RIGHT_CLICK_AIR) {
			// debug statements here
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(DEBUG) {
			if(sender instanceof Player && args.length == 2) {
				Player p = (Player) sender;
				if(args[0].equalsIgnoreCase("save")) {
					getConfig().set(args[1].toUpperCase(), p.getInventory().getItemInMainHand().clone());
					saveConfig();
				} else if(args[0].equalsIgnoreCase("load")) {
					ItemStack item = getConfig().getItemStack(args[1].toUpperCase());
					p.getWorld().dropItemNaturally(p.getLocation(), item);
				}
			}
		}
		return false;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
}
