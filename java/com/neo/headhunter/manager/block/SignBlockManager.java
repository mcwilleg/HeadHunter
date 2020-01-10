package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.BlockConfigAccessor;
import com.neo.headhunter.manager.bounty.BountyListEntry;
import com.neo.headhunter.manager.head.HeadDrop;
import com.neo.headhunter.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SignBlockManager extends BlockConfigAccessor<HeadHunter> implements Listener {
	private BountySignUpdateRunnable bountyRunnable;
	private Map<Player, Sign> signLinkMap;
	
	public SignBlockManager(HeadHunter plugin) {
		super(plugin, "signs.yml", "data");
		
		this.bountyRunnable = new BountySignUpdateRunnable();
		this.bountyRunnable.runTaskTimer(plugin, 0L, 20L);
		requestUpdate();
		
		this.signLinkMap = new HashMap<>();
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if(!plugin.isVersionBefore(1, 9, 0) && event.getHand() != EquipmentSlot.HAND)
			return;
		Block block = event.getClickedBlock();
		if(block == null)
			return;
		Player hunter = event.getPlayer();
		
		String ownerUUID = (String) getBlockData("sell-head", block, "owner");
		if(ownerUUID != null && block.getState() instanceof Sign) {
			// check permission
			if(!hunter.hasPermission("hunter.sellhead.sign")) {
				hunter.sendMessage(Message.PERMISSION.format("selling at signs"));
				return;
			}
			
			if(hunter.isSneaking())
				plugin.getSellExecutor().sellAllStacks(hunter);
			else
				plugin.getSellExecutor().sellHeldStack(hunter);
			return;
		}
		
		Integer bountyIndex = (Integer) getBlockData("bounty", block, "index");
		if(bountyIndex != null && block.getState() instanceof Sign) {
			// check permission
			if(!hunter.hasPermission("hunter.sign.bounty"))
				return;
			
			signLinkMap.put(hunter, (Sign) block.getState());
			hunter.sendMessage(Message.BOUNTY_SIGN_LINK.format());
			return;
		}
		
		if(signLinkMap.containsKey(hunter)) {
			if (block.getState() instanceof Skull) {
				Skull skullBlock = (Skull) block.getState();
				Sign bountySign = signLinkMap.get(hunter);
				String worldPath = getWorldPath(skullBlock.getBlock());
				String blockPath = getBlockPath(skullBlock.getBlock());
				setBlockData("bounty", bountySign.getBlock(), "head", String.join(";", worldPath, blockPath));
				saveConfig();
				hunter.sendMessage(Message.BOUNTY_HEAD_LINK.format());
				requestUpdate();
			} else
				hunter.sendMessage(Message.BOUNTY_LINK_ABORT.format());
			signLinkMap.remove(hunter);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(HeadBlockManager.isHeadBlock(event.getBlockPlaced())) {
			if(getBlockData("sell-head", event.getBlockAgainst(), null) != null)
				event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();
		if(lines[0] == null)
			return;
		if(lines[0].equalsIgnoreCase("[sellhead]")) {
			// check permission
			if(!event.getPlayer().hasPermission("hunter.sign.sellhead")) {
				event.getPlayer().sendMessage(Message.PERMISSION.format("creating head-selling signs"));
				return;
			}
			
			setBlockData("sell-head", event.getBlock(), "owner", event.getPlayer().getUniqueId().toString());
			saveConfig();
			
			event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&0[&4Sell Heads&0]"));
			event.setLine(1, ChatColor.translateAlternateColorCodes('&', "&0Click to sell!"));
			event.setLine(2, ChatColor.translateAlternateColorCodes('&', "&0Crouch and click"));
			event.setLine(3, ChatColor.translateAlternateColorCodes('&', "&0to sell all."));
		} else if(lines[0].equalsIgnoreCase("[bounty]")) {
			// check permission
			if(!event.getPlayer().hasPermission("hunter.sign.bounty")) {
				event.getPlayer().sendMessage(Message.PERMISSION.format("creating bounty display signs"));
				return;
			}
			
			if(lines[1] != null && lines[1].matches("[1-9]\\d*")) {
				int bountyListIndex = Integer.valueOf(lines[1]) - 1;
				
				setBlockData("bounty", event.getBlock(), "index", bountyListIndex);
				saveConfig();
				bountyRunnable.signs.add((Sign) event.getBlock().getState());
				
				event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&0[&4Wanted&0]"));
				event.setLine(1, ChatColor.translateAlternateColorCodes('&', "&0#" + (bountyListIndex + 1)));
				event.setLine(2, ChatColor.translateAlternateColorCodes('&', "&0No Bounty"));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		removeBlock(event.getBlock());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent event) {
		for(Block block : event.blockList()) {
			if(block != null && block.getState() instanceof Sign)
				removeBlock(block);
		}
	}
	
	public void requestUpdate() {
		for(Block bountySignBlock : getBlockKeys("bounty")) {
			if(bountySignBlock.getState() instanceof Sign)
				bountyRunnable.signs.add((Sign) bountySignBlock.getState());
		}
	}
	
	private void removeBlock(Block block) {
		setBlockData("sell-head", block, null, null);
		setBlockData("bounty", block, null, null);
		saveConfig();
	}
	
	private class BountySignUpdateRunnable extends BukkitRunnable {
		private Set<Sign> signs;
		
		private BountySignUpdateRunnable() {
			this.signs = new HashSet<>();
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if(signs.isEmpty())
				return;
			
			Iterator<Sign> iterator = signs.iterator();
			while(iterator.hasNext()) {
				Sign sign = iterator.next();
				String signData = (String) getBlockData("bounty", sign.getBlock(), "head");
				if(signData == null)
					continue;
				
				String[] signDataSplit = signData.split(";", 2);
				BlockState blockState = getBlock(signDataSplit[0], signDataSplit[1]).getState();
				Skull skull = null;
				if(blockState instanceof Skull)
					skull = (Skull) blockState;
				
				if(!sign.getChunk().isLoaded() || (skull != null && !skull.getChunk().isLoaded()))
					continue;
				
				int index = (int) getBlockData("bounty", sign.getBlock(), "index");
				
				BountyListEntry bounty = plugin.getBountyManager().getListEntry(index);
				String indexLine = "#" + (index + 1);
				String victimLine = "No Bounty";
				String amountLine = "";
				if(bounty != null) {
					victimLine = bounty.getVictim().getName();
					amountLine = HeadDrop.formatMoney(plugin, bounty.getAmount());
					if(skull != null) {
						if (plugin.isVersionBefore(1, 13, 0))
							skull.setOwner(bounty.getVictim().getName());
						else
							skull.setOwningPlayer(bounty.getVictim());
						skull.update();
					}
				} else if(skull != null) {
					skull.setOwner("MHF_Question");
					skull.update();
				}
				
				sign.setLine(0, ChatColor.translateAlternateColorCodes('&', "&0[&4Wanted&0]"));
				sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&0" + indexLine));
				sign.setLine(2, ChatColor.translateAlternateColorCodes('&', "&0" + victimLine));
				sign.setLine(3, ChatColor.translateAlternateColorCodes('&', "&0" + amountLine));
				
				sign.update();
				
				iterator.remove();
			}
		}
	}
}
