package com.neo.headhunter.manager.block;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.manager.head.HeadData;
import com.neo.headhunter.util.config.BlockConfigAccessor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class HeadBlockManager extends BlockConfigAccessor implements Listener {
	private static final List<String> HEAD_MATERIALS = Arrays.asList(
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

	private static final List<String> AIR_MATERIALS = Arrays.asList(
			"AIR",
			"LEGACY_AIR",
			"CAVE_AIR",
			"VOID_AIR"
	);

	private final HeadHunter plugin;
	
	public HeadBlockManager(HeadHunter plugin) {
		super(plugin, "placed_heads.yml", "data");
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (isHead(item) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			String headDataString = (new HeadData(plugin, item)).getDataString();
			setBlockData(event.getBlock(), headDataString);
			saveConfig();
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (isHeadBlock(block)) {
			String headDataString = (String) getBlockData(block);
			if (headDataString != null) {
				block.setType(Material.AIR);
				
				ItemStack head = (new HeadData(plugin, headDataString)).getFormattedHead();
				
				if (head != null) {
					if (plugin.isVersionBefore(1, 12, 0) || event.isDropItems()) {
						if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
							block.getWorld().dropItemNaturally(block.getLocation(), head);
						}
					}
					setBlockData(block, null);
					saveConfig();
				}
			}
		}
	}
	
	public static boolean isHead(ItemStack head) {
		if (head != null) {
			Material type = head.getType();
			return !AIR_MATERIALS.contains(type.name()) && HEAD_MATERIALS.contains(type.name());
		}
		return false;
	}
	
	public static boolean isHeadBlock(Block headBlock) {
		if (headBlock != null) {
			Material type = headBlock.getType();
			return !AIR_MATERIALS.contains(type.name()) && HEAD_MATERIALS.contains(type.name());
		}
		return false;
	}
}
