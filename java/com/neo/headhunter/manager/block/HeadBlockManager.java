package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.BlockConfigAccessor;
import com.neo.headhunter.head.HeadData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			String headDataString = (new HeadData(plugin, item)).getDataString();
			setBlockData(event.getBlock(), headDataString);
			saveConfig();
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(isHeadBlock(block)) {
			String headDataString = (String) getBlockData(block);
			if(headDataString != null) {
				block.setType(Material.AIR);
				
				ItemStack head = (new HeadData(plugin, headDataString)).getFormattedHead();
				
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
	
	public static boolean isHead(ItemStack head) {
		return head != null && HEAD_MATERIALS.contains(head.getType());
	}
	
	private static boolean isHeadBlock(Block headBlock) {
		return headBlock != null && (HEAD_MATERIALS.contains(headBlock.getType()));
	}
}
