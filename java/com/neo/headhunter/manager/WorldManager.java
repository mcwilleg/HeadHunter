package com.neo.headhunter.manager;

import com.neo.headhunter.ConfigAccessor;
import com.neo.headhunter.HeadHunter;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WorldManager extends ConfigAccessor {
	private static final String
			WORLDS_IGNORED = "worlds.ignored",
			WORLDS_VALID = "worlds.valid";
	
	public WorldManager(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	public boolean isValidWorld(World world) {
		if(!config.getBoolean(WORLDS_IGNORED, true))
			return config.getStringList(WORLDS_VALID).contains(world.getUID().toString());
		return true;
	}
	
	public boolean addValidWorld(World world) {
		Set<String> validWorlds = new HashSet<>(config.getStringList(WORLDS_VALID));
		boolean success = validWorlds.add(world.getUID().toString());
		config.set(WORLDS_VALID, new ArrayList<>(validWorlds));
		saveConfig();
		return success;
	}
	
	public boolean removeValidWorld(World world) {
		Set<String> validWorlds = new HashSet<>(config.getStringList(WORLDS_VALID));
		boolean success = validWorlds.remove(world.getUID().toString());
		config.set(WORLDS_VALID, new ArrayList<>(validWorlds));
		saveConfig();
		return success;
	}
	
	public void setWorldsIgnored(boolean ignored) {
		config.set(WORLDS_IGNORED, ignored);
		saveConfig();
	}
}
