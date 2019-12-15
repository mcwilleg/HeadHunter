package com.neo.headhunter.config;

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
		String chunkPath = getChunkPath(block);
		String blockPath = getBlockPath(block);
		String mainPath = String.join(".", worldPath, chunkPath, blockPath);
		
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
			
			// if there is no chunk data left, remove the section
			path = String.join(".", worldPath, chunkPath);
			if(prefix != null)
				path = String.join(".", prefix, path);
			ConfigurationSection chunkSection = config.getConfigurationSection(path);
			if(chunkSection == null || chunkSection.getKeys(false).isEmpty())
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
		String chunkPath = getChunkPath(block);
		String blockPath = getBlockPath(block);
		
		String path = String.join(".", worldPath, chunkPath, blockPath);
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
			ConfigurationSection chunks = worlds.getConfigurationSection(worldKey);
			if(chunks == null)
				continue;
			for(String chunkKey : chunks.getKeys(false)) {
				ConfigurationSection blocks = chunks.getConfigurationSection(chunkKey);
				if(blocks == null)
					continue;
				for(String blockKey : blocks.getKeys(false)) {
					Block block = getBlock(worldKey, chunkKey, blockKey);
					if(block != null)
						result.add(block);
				}
			}
		}
		return result;
	}
	
	private Block getBlock(String worldKey, String chunkKey, String blockKey) {
		World world = Bukkit.getWorld(UUID.fromString(worldKey));
		if(world != null) {
			String[] chunkSplit = chunkKey.split(";");
			int chunkX = Integer.valueOf(chunkSplit[0], 16);
			int chunkZ = Integer.valueOf(chunkSplit[1], 16);
			
			String[] blockSplit = blockKey.split(";");
			int blockX = Integer.valueOf(blockSplit[0], 16);
			int blockZ = Integer.valueOf(blockSplit[1], 16);
			int blockY = Integer.valueOf(blockSplit[2], 16);
			
			blockX = (chunkX << 4) | blockX;
			blockZ = (chunkZ << 4) | blockZ;
			Location location = new Location(world, blockX, blockY, blockZ);
			return location.getBlock();
		}
		return null;
	}
	
	private String getWorldPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return block.getWorld().getUID().toString();
	}
	
	private String getChunkPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return String.format("%H;%H", block.getX() >> 4, block.getZ() >> 4);
	}
	
	private String getBlockPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return String.format("%H;%H;%H", block.getX() & 0xF, block.getZ() & 0xF, (byte) block.getY());
	}
}
