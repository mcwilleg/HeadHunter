package com.neo.headhunter.util.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockConfigAccessor<T extends JavaPlugin> extends ConfigAccessor<T> {
	public BlockConfigAccessor(T plugin, String fileName, String... ancestry) {
		super(plugin, true, fileName, ancestry);
	}
	
	protected void setBlockData(String prefix, Block block, String suffix, Object value) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		String worldPath = getWorldPath(block);
		String blockPath = getBlockPath(block);
		String mainPath = String.join(".", worldPath, blockPath);
		
		// add prefix and/or suffix
		String path = mainPath;
		if(prefix != null)
			path = String.join(".", prefix, path);
		if(suffix != null)
			path = String.join(".", path, suffix);
		
		// set or remove value, clean up if necessary
		if(value != null)
			config.set(path, value);
		else {
			// remove the individual line of block data
			config.set(path, null);
			
			// if there is no block data left, remove the section
			path = mainPath;
			if(prefix != null)
				path = String.join(".", prefix, path);
			ConfigurationSection blockSection = config.getConfigurationSection(path);
			if(blockSection == null || blockSection.getKeys(false).isEmpty())
				config.set(path, null);
			else return;
			
			// if there is no world data left, remove the section
			path = worldPath;
			if(prefix != null)
				path = String.join(".", prefix, path);
			ConfigurationSection worldSection = config.getConfigurationSection(path);
			if(worldSection == null || worldSection.getKeys(false).isEmpty())
				config.set(path, null);
		}
	}
	
	protected void setBlockData(Block block, String value) {
		setBlockData(null, block, null, value);
	}
	
	protected Object getBlockData(Block block) {
		return getBlockData(null, block, null);
	}
	
	protected Object getBlockData(String prefix, Block block, String suffix) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		
		String worldPath = getWorldPath(block);
		String blockPath = getBlockPath(block);
		
		String path = String.join(".", worldPath, blockPath);
		if(prefix != null)
			path = String.join(".", prefix, path);
		if(suffix != null)
			path = String.join(".", path, suffix);
		return config.get(path);
	}
	
	protected List<Block> getBlockKeys(String prefix) {
		List<Block> result = new ArrayList<>();
		ConfigurationSection worlds;
		if(prefix != null)
			worlds = config.getConfigurationSection(prefix);
		else
			worlds = config;
		if(worlds == null)
			return result;
		for(String worldKey : worlds.getKeys(false)) {
			ConfigurationSection blocks = worlds.getConfigurationSection(worldKey);
			if(blocks == null)
				continue;
			for(String blockKey : blocks.getKeys(false)) {
				Block block = getBlock(worldKey, blockKey);
				if(block != null)
					result.add(block);
			}
		}
		return result;
	}
	
	protected Block getBlock(String worldKey, String blockKey) {
		World world = Bukkit.getWorld(UUID.fromString(worldKey));
		if(world != null) {
			String[] blockSplit = blockKey.split(";");
			int blockX = Integer.parseInt(blockSplit[0]);
			int blockZ = Integer.parseInt(blockSplit[1]);
			int blockY = Integer.parseInt(blockSplit[2]);
			
			Location location = new Location(world, blockX, blockY, blockZ);
			return location.getBlock();
		}
		return null;
	}
	
	protected String getWorldPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return block.getWorld().getUID().toString();
	}
	
	protected String getBlockPath(Block block) {
		return String.format("%d;%d;%d", block.getX(), block.getZ(), block.getY());
	}
}
