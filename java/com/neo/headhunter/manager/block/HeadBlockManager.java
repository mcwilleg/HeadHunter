package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.BlockConfigAccessor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HeadBlockManager extends BlockConfigAccessor<HeadHunter> implements Listener {
	private static final List<Material> HEAD_MATERIALS = new ArrayList<>();
	private static final List<String> HEAD_MATERIAL_NAMES = Arrays.asList(
			"SKULL",
			"SKULL_ITEM",
			"LEGACY_SKULL",
			"LEGACY_SKULL_ITEM",
			"CREEPER_HEAD",
			"DRAGON_HEAD",
			"PLAYER_HEAD",
			"ZOMBIE_HEAD",
			"SKELETON_SKULL",
			"WITHER_SKELETON_SKULL",
			"CREEPER_WALL_HEAD",
			"DRAGON_WALL_HEAD",
			"PLAYER_WALL_HEAD",
			"ZOMBIE_WALL_HEAD",
			"SKELETON_WALL_SKULL",
			"WITHER_SKELETON_WALL_SKULL"
	);
	
	public HeadBlockManager(HeadHunter plugin) {
		super(plugin, "placed_heads.yml", "data");
		for(String materialName : HEAD_MATERIAL_NAMES) {
			try {
				Material headMaterial = Material.valueOf(materialName);
				HEAD_MATERIALS.add(headMaterial);
			} catch(IllegalArgumentException ex) {
				// ignore
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	@SuppressWarnings("deprecation")
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if(isHead(item) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if(meta != null) {
				double value = ((int) (getHeadStackValue(item) / item.getAmount() * 100)) / 100.0;
				String mobPath = plugin.getHeadLibrary().getConfigPath(item);
				if (mobPath == null) {
					OfflinePlayer owner;
					if(plugin.isVersionBefore(1, 13, 0))
						owner = plugin.getBountyExecutor().getPlayer(meta.getOwner());
					else
						owner = meta.getOwningPlayer();
					if(owner != null) {
						setBlockData(event.getBlock(), "PLAYER " + owner.getUniqueId().toString() + " " + value);
						saveConfig();
					}
				} else {
					setBlockData(event.getBlock(), mobPath.replace(".", ";") + " " + value);
					saveConfig();
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(isHeadBlock(block)) {
			String headDataString = getBlockData(block);
			if(headDataString != null) {
				block.setType(Material.AIR);
				String[] headData = headDataString.split(" ");
				ItemStack head = null;
				double value = 0;
				if(headData.length == 3 && headData[0].equals("PLAYER")) {
					UUID uuid = UUID.fromString(headData[1]);
					value = Double.valueOf(headData[2]);
					head = plugin.getHeadLibrary().getPlayerHead(Bukkit.getOfflinePlayer(uuid));
				} else if(headData.length == 2) {
					String mobPath = headData[0].replace(";", ".");
					value = Double.valueOf(headData[1]);
					head = plugin.getHeadLibrary().getMobHead(mobPath);
				}
				head = plugin.getDropManager().formatHead(head, value);
				
				if(head != null) {
					if(plugin.isVersionBefore(1, 12, 0) || event.isDropItems()) {
						if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
							block.getWorld().dropItemNaturally(block.getLocation(), head);
					}
					setBlockData(block, null);
					saveConfig();
				}
			}
		}
	}
	
	public boolean isHead(ItemStack head) {
		return head != null && HEAD_MATERIALS.contains(head.getType());
	}
	
	private boolean isHeadBlock(Block headBlock) {
		return headBlock != null && (HEAD_MATERIALS.contains(headBlock.getType()));
	}
	
	public double getHeadStackValue(ItemStack head) {
		if(isHead(head)) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if(meta != null) {
				List<String> lore = meta.getLore();
				if(lore != null && !lore.isEmpty()) {
					String priceString = lore.get(0);
					priceString = ChatColor.stripColor(priceString);
					priceString = priceString.replace("Sell Price: $", "");
					if(priceString.matches("\\d+([.]\\d{0,2})?")) {
						return Double.valueOf(priceString) * head.getAmount();
					}
				}
			}
		}
		return 0;
	}
}
