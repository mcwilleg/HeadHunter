package com.neo.headhunter.manager.support.factions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface FactionsHook {
	boolean isValidTerritory(Player victim);
	boolean isValidZone(Location location);
	boolean isValidHunter(Player hunter, Player victim);
}
