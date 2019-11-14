package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.BlockConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignBlockManager extends BlockConfigAccessor implements Listener {
	public SignBlockManager(HeadHunter plugin) {
		super(plugin, "signs.yml", "data");
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		String ownerUUID = getBlockData("sell-head", event.getClickedBlock());
		if(ownerUUID != null) {
			Player hunter = event.getPlayer();
			plugin.getSellExecutor().sellHeads(hunter, hunter.isSneaking());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();
		if(lines[0] != null && lines[0].equalsIgnoreCase("[sellhead]")) {
			ChatColor baseColor = ChatColor.DARK_GRAY;
			ChatColor accentColor = ChatColor.DARK_RED;
			
			event.setLine(0, baseColor + "[" + accentColor + "Sell Heads" + baseColor + "]");
			event.setLine(1, baseColor + "Click this sign");
			event.setLine(2, baseColor + "to sell your");
			event.setLine(3, baseColor + "victims' heads!");
			
			setBlockData("sell-head", event.getBlock(), event.getPlayer().getUniqueId().toString());
			saveConfig();
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		setBlockData("sell-head", event.getBlock(), null);
		saveConfig();
	}
}
