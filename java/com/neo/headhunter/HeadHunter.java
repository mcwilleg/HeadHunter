package com.neo.headhunter;

import com.neo.headhunter.bounty.BountyManager;
import com.neo.headhunter.command.BountyExecutor;
import com.neo.headhunter.command.HunterExecutor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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
	
	private Settings settings;
	private DropManager dropManager;
	private MobLibrary mobLibrary;
	private WorldManager worldManager;
	private ProjectileManager projectileManager;
	private BountyManager bountyManager;
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		economy = connectEconomy();
		if(economy == null) {
			getLogger().log(Level.SEVERE, "Could not connect to Vault. Make sure Vault is installed for HeadHunter!");
			return;
		}
		
		settings = new Settings(this);
		dropManager = new DropManager(this);
		mobLibrary = new MobLibrary(this);
		worldManager = new WorldManager(this);
		projectileManager = new ProjectileManager(this);
		bountyManager = new BountyManager(this);
		
		// register listeners
		if(DEBUG)
			registerListener(this);
		registerListener(dropManager);
		registerListener(new DeathListener(this));
		registerListener(projectileManager);
		
		// register commands
		if(DEBUG)
			registerCommand("hhdebug", this);
		registerCommand("hunter", new HunterExecutor(this));
		registerCommand("bounty", new BountyExecutor(this));
	}
	
	private void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
	
	private void registerCommand(String name, CommandExecutor executor) {
		PluginCommand command = getCommand(name);
		if(command != null)
			command.setExecutor(executor);
		else
			getLogger().log(Level.SEVERE, "Could not register command: /" + name);
	}
	
	public void reloadAll() {
		settings.reloadConfig();
		mobLibrary.reloadConfig();
		worldManager.reloadConfig();
		bountyManager.reloadConfig();
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
		if(event.getAction() == Action.RIGHT_CLICK_AIR) {
			// debug statements here
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player && args.length == 2) {
			Player p = (Player) sender;
			if(args[0].equalsIgnoreCase("save")) {
				getConfig().set(args[1].toUpperCase(), p.getInventory().getItemInMainHand().clone());
				saveConfig();
			} else if(args[0].equalsIgnoreCase("load")) {
				ItemStack item = getConfig().getItemStack(args[1].toUpperCase());
				if(item != null)
					p.getWorld().dropItemNaturally(p.getLocation(), item);
				else
					p.sendMessage("Attempted item drop with null item.");
			}
		}
		return false;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public DropManager getDropManager() {
		return dropManager;
	}
	
	public MobLibrary getMobLibrary() {
		return mobLibrary;
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}
	
	public BountyManager getBountyManager() {
		return bountyManager;
	}
}
