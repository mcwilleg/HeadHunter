package com.neo.headhunter.config;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockConfigAccessor<T extends JavaPlugin> extends ConfigAccessor<T> {
	public BlockConfigAccessor(T plugin, String fileName, String... ancestry) {
		super(plugin, true, fileName, ancestry);
	}
	
	protected void setBlockData(String prefix, Block block, String value) {
		if(block != null) {
			String worldPath = getWorldPath(block);
			if(prefix != null)
				worldPath = String.join(".", prefix, worldPath);
			String chunkPath = getChunkPath(block);
			String blockPath = getBlockPath(block);
			String path = String.join(".", worldPath, chunkPath, blockPath);
			if (value != null) {
				config.set(path, value);
			} else {
				config.set(path, null);
				ConfigurationSection chunkSection = config.getConfigurationSection(worldPath + "." + chunkPath);
				if(chunkSection != null && chunkSection.getKeys(false).isEmpty()) {
					config.set(worldPath + "." + chunkPath, null);
					ConfigurationSection worldSection = config.getConfigurationSection(worldPath);
					if(worldSection != null && worldSection.getKeys(false).isEmpty())
						config.set(worldPath, null);
				}
			}
		}
	}
	
	protected void setBlockData(Block block, String value) {
		setBlockData(null, block, value);
	}
	
	protected String getBlockData(Block block) {
		return getBlockData(null, block);
	}
	
	protected String getBlockData(String prefix, Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		String worldPath = getWorldPath(block);
		if(prefix != null)
			worldPath = String.join(".", prefix, worldPath);
		String chunkPath = getChunkPath(block);
		String blockPath = getBlockPath(block);
		String path = String.join(".", worldPath, chunkPath, blockPath);
		return config.getString(path);
	}
	
	private String getWorldPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return block.getWorld().getUID().toString();
	}
	
	private String getChunkPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return String.format("%H%H", block.getX() >> 4, block.getZ() >> 4);
	}
	
	private String getBlockPath(Block block) {
		if(block == null)
			throw new IllegalArgumentException("block cannot be null");
		return String.format("%H%H%H", block.getX() & 0xF, block.getZ() & 0xF, (byte) block.getY());
	}
}
