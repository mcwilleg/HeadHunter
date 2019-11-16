package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.BlockConfigAccessor;
import com.neo.headhunter.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
		Block block = event.getClickedBlock();
		String ownerUUID = getBlockData("sell-head", block);
		if(ownerUUID != null && block != null && block.getState() instanceof Sign) {
			// check permission
			if(!event.getPlayer().hasPermission("hunter.sellhead.sign")) {
				event.getPlayer().sendMessage(Message.PERMISSION.failure("selling at signs"));
				return;
			}
			
			Player hunter = event.getPlayer();
			plugin.getSellExecutor().sellHeads(hunter, hunter.isSneaking());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();
		if(lines[0] != null && lines[0].equalsIgnoreCase("[sellhead]")) {
			// check permission
			if(!event.getPlayer().hasPermission("hunter.sign")) {
				event.getPlayer().sendMessage(Message.PERMISSION.failure("creating head-selling signs"));
				return;
			}
			
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
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		setBlockData("sell-head", event.getBlock(), null);
		saveConfig();
	}
}
