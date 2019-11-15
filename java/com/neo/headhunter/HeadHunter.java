package com.neo.headhunter;

import com.neo.headhunter.bounty.BountyManager;
import com.neo.headhunter.command.BountyExecutor;
import com.neo.headhunter.command.HunterExecutor;
import com.neo.headhunter.command.SellExecutor;
import com.neo.headhunter.config.Settings;
import com.neo.headhunter.manager.*;
import com.neo.headhunter.manager.block.HeadBlockManager;
import com.neo.headhunter.manager.block.SignBlockManager;
import com.neo.headhunter.manager.support.FactionsBlueHook;
import com.neo.headhunter.manager.support.FactionsHook;
import com.neo.headhunter.manager.support.FactionsUUIDHook;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HeadHunter extends JavaPlugin implements Listener, CommandExecutor {
	private static final boolean DEBUG = true;

	private Economy economy;
	private FactionsHook factionsHook;
	
	private Settings settings;
	private DropManager dropManager;
	private MobLibrary mobLibrary;
	private WorldManager worldManager;
	private ProjectileManager projectileManager;
	private BountyManager bountyManager;
	private HeadBlockManager headBlockManager;
	private SellExecutor sellExecutor;
	
	@Override
	public void onEnable() {
		// Plugin startup logic
		economy = connectEconomy();
		if(economy == null) {
			getLogger().log(Level.SEVERE, "Could not connect to Vault. Make sure Vault is installed for HeadHunter!");
			return;
		}
		
		factionsHook = connectFactions();
		
		settings = new Settings(this);
		dropManager = new DropManager(this);
		mobLibrary = new MobLibrary(this);
		worldManager = new WorldManager(this);
		projectileManager = new ProjectileManager(this);
		bountyManager = new BountyManager(this);
		headBlockManager = new HeadBlockManager(this);
		sellExecutor = new SellExecutor(this);
		
		// register listeners
		if(DEBUG)
			registerListener(this);
		registerListener(dropManager);
		registerListener(new DeathListener(this));
		registerListener(projectileManager);
		registerListener(headBlockManager);
		registerListener(new SignBlockManager(this));
		
		// register commands
		if(DEBUG)
			registerCommand("hhdebug", this);
		registerCommand("hunter", new HunterExecutor(this));
		registerCommand("bounty", new BountyExecutor(this));
		registerCommand("sellhead", sellExecutor);
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
		headBlockManager.reloadConfig();
	}
	
	private Economy connectEconomy() {
		if(Bukkit.getPluginManager().getPlugin("Vault") == null)
			return null;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			return null;
		
		return rsp.getProvider();
	}
	
	private FactionsHook connectFactions() {
		if(Bukkit.getPluginManager().isPluginEnabled("FactionsBlue"))
			factionsHook = new FactionsBlueHook(this);
		else if(Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			Plugin factionsPlugin = Bukkit.getPluginManager().getPlugin("Factions");
			if(factionsPlugin != null && factionsPlugin.getDescription().getDepend().contains("MassiveCore"))
				factionsHook = null;
			else
				factionsHook = new FactionsUUIDHook(this);
		}
		return null;
	}
	
	@EventHandler
	public void onDebug(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_AIR) {
			// debug statements here
			event.getPlayer().performCommand("hunter sellhead");
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
	
	public void debug(String message) {
		if(DEBUG)
			getLogger().log(Level.INFO, message);
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
	
	public HeadBlockManager getHeadBlockManager() {
		return headBlockManager;
	}
	
	public SellExecutor getSellExecutor() {
		return sellExecutor;
	}
	
	public FactionsHook getFactionsHook() {
		return factionsHook;
	}
}
