package com.neo.headhunter;

import com.earth2me.essentials.Essentials;
import com.neo.headhunter.command.BountyExecutor;
import com.neo.headhunter.command.HunterExecutor;
import com.neo.headhunter.command.SellExecutor;
import com.neo.headhunter.manager.DeathListener;
import com.neo.headhunter.manager.DropManager;
import com.neo.headhunter.manager.EntityManager;
import com.neo.headhunter.manager.WorldManager;
import com.neo.headhunter.manager.block.HeadBlockManager;
import com.neo.headhunter.manager.block.SignBlockManager;
import com.neo.headhunter.manager.bounty.BountyManager;
import com.neo.headhunter.manager.head.HeadLibrary;
import com.neo.headhunter.manager.support.EssentialsHook;
import com.neo.headhunter.manager.support.factions.FactionsBlueHook;
import com.neo.headhunter.manager.support.factions.FactionsHook;
import com.neo.headhunter.manager.support.factions.FactionsMassiveCoreHook;
import com.neo.headhunter.manager.support.factions.FactionsUUIDHook;
import com.neo.headhunter.util.config.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.logging.Level;

public final class HeadHunter extends JavaPlugin implements Listener, CommandExecutor {
	public static final boolean DEBUG = true;
	private static final int MAJOR_VER = 0, MINOR_VER = 1, PATCH_VER = 2;
	
	private final int[] version = new int[3];
	
	private Economy economy;
	private EssentialsHook essentialsHook;
	private FactionsHook factionsHook;
	
	private Settings settings;
	private DropManager dropManager;
	private HeadLibrary headLibrary;
	private WorldManager worldManager;
	private EntityManager entityManager;
	private BountyManager bountyManager;
	private HeadBlockManager headBlockManager;
	private SignBlockManager signBlockManager;
	private SellExecutor sellExecutor;
	private BountyExecutor bountyExecutor;
	
	@Override
	public void onEnable() {
		// determine current Bukkit version
		String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
		String majorVer = split[0]; // For 1.10 will be "1"
		String minorVer = split[1]; // For 1.10 will be "10"
		String patchVer = split.length > 2 ? split[2] : "0"; // For 1.10 will be "0", for 1.9.4 will be "4"
		
		version[MAJOR_VER] = Integer.parseInt(majorVer);
		version[MINOR_VER] = Integer.parseInt(minorVer);
		version[PATCH_VER] = Integer.parseInt(patchVer);
		
		// connections
		economy = connectEconomy();
		if(economy == null) {
			getLogger().log(Level.SEVERE, "Could not connect to Vault. Make sure Vault is installed for HeadHunter!");
			return;
		}
		
		essentialsHook = connectEssentials();
		factionsHook = connectFactions();
		
		// managers
		settings = new Settings(this);
		dropManager = new DropManager(this);
		headLibrary = new HeadLibrary(this);
		worldManager = new WorldManager(this);
		entityManager = new EntityManager(this);
		bountyManager = new BountyManager(this);
		headBlockManager = new HeadBlockManager(this);
		signBlockManager = new SignBlockManager(this);
		sellExecutor = new SellExecutor(this);
		bountyExecutor = new BountyExecutor(this);
		
		// register listeners
		if(DEBUG)
			registerListener(this);
		registerListener(dropManager);
		registerListener(new DeathListener(this));
		registerListener(entityManager);
		registerListener(headBlockManager);
		registerListener(signBlockManager);
		
		// register commands
		if(DEBUG)
			registerCommand("hhdebug", this);
		registerCommand("hunter", new HunterExecutor(this));
		registerCommand("sellhead", sellExecutor);
		registerCommand("bounty", bountyExecutor);
	}
	
	private void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
	
	private void registerCommand(String name, CommandExecutor executor) {
		PluginCommand command = getCommand(name);
		if(command != null) {
			command.setExecutor(executor);
			if(executor instanceof TabCompleter)
				command.setTabCompleter((TabCompleter) executor);
		} else
			getLogger().log(Level.SEVERE, "Could not register command: /" + name);
	}
	
	public void reloadAll() {
		settings.reloadConfig();
		headLibrary.reloadConfig();
		worldManager.reloadConfig();
		bountyManager.reloadConfig();
		headBlockManager.reloadConfig();
	}
	
	private Economy connectEconomy() {
		if(!Bukkit.getPluginManager().isPluginEnabled("Vault"))
			return null;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			return null;
		
		return rsp.getProvider();
	}
	
	private EssentialsHook connectEssentials() {
		if(Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
			Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			if(essentials != null)
				return new EssentialsHook(essentials);
		}
		return null;
	}
	
	private FactionsHook connectFactions() {
		if(Bukkit.getPluginManager().isPluginEnabled("FactionsBlue"))
			return new FactionsBlueHook(this);
		else if(Bukkit.getPluginManager().isPluginEnabled("Factions")) {
			Plugin factionsPlugin = Bukkit.getPluginManager().getPlugin("Factions");
			if(factionsPlugin != null) {
				if(factionsPlugin.getDescription().getDepend().contains("MassiveCore"))
					return new FactionsMassiveCoreHook(this);
				else
					return new FactionsUUIDHook(this);
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
		if(sender instanceof Player && args.length == 2) {
			Player p = (Player) sender;
			if(args[0].equalsIgnoreCase("save")) {
				PlayerInventory inv = p.getInventory();
				ItemStack heldItem = inv.getItem(inv.getHeldItemSlot());
				if(heldItem != null) {
					getConfig().set(args[1].toUpperCase(), heldItem.clone());
					saveConfig();
				}
			} else if(args[0].equalsIgnoreCase("load")) {
				ItemStack item = getConfig().getItemStack(args[1].toUpperCase());
				if(item != null)
					p.getWorld().dropItemNaturally(p.getLocation(), item);
				else
					p.sendMessage("Attempted item drop with null item.");
			} else if(args[0].equalsIgnoreCase("ping")) {
				PlayerInventory inv = p.getInventory();
				ItemStack heldItem = inv.getItem(inv.getHeldItemSlot());
				if(heldItem != null) {
					Map<String, Object> s = heldItem.serialize();
					for(Map.Entry<String, Object> entry : s.entrySet())
						System.out.println(entry.getKey() + " -> " + entry.getValue());
				}
			}
		}
		return false;
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	// parameters are the earliest necessary version
	public boolean isVersionBefore(int majorVer, int minorVer, int patchVer) {
		return version[MAJOR_VER] < majorVer || version[MINOR_VER] < minorVer || version[PATCH_VER] < patchVer;
	}
	
	public Economy getEconomy() {
		return economy;
	}
	
	public EssentialsHook getEssentialsHook() {
		return essentialsHook;
	}
	
	public FactionsHook getFactionsHook() {
		return factionsHook;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public DropManager getDropManager() {
		return dropManager;
	}
	
	public HeadLibrary getHeadLibrary() {
		return headLibrary;
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public BountyManager getBountyManager() {
		return bountyManager;
	}
	
	public SignBlockManager getSignBlockManager() {
		return signBlockManager;
	}
	
	public SellExecutor getSellExecutor() {
		return sellExecutor;
	}
	
	public BountyExecutor getBountyExecutor() {
		return bountyExecutor;
	}
}
