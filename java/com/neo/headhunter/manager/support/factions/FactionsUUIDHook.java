package com.neo.headhunter.manager.support.factions;

import com.massivecraft.factions.*;
import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class FactionsUUIDHook extends ConfigAccessor<HeadHunter> implements FactionsHook {
	public FactionsUUIDHook(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	@Override
	public boolean isValidTerritory(Player victim) {
		Faction faction = Board.getInstance().getFactionAt(new FLocation(victim));
		FPlayer fPlayer = FPlayers.getInstance().getByPlayer(victim);
		if(faction != null && fPlayer != null && fPlayer.hasFaction()) {
			Faction ownedFaction = fPlayer.getFaction();
			if(faction.equals(ownedFaction))
				return plugin.getSettings().isFactionsDropHome();
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
		if(faction != null) {
			if(faction.isWilderness())
				return plugin.getSettings().isFactionsDropWilderness();
			if(faction.isSafeZone())
				return plugin.getSettings().isFactionsDropSafezone();
			if(faction.isWarZone())
				return plugin.getSettings().isFactionsDropWarzone();
		}
		return true;
	}
	
	@Override
	public boolean isValidHunter(Player hunter, Player victim) {
		FPlayer hFPlayer = FPlayers.getInstance().getByPlayer(hunter);
		FPlayer vFPlayer = FPlayers.getInstance().getByPlayer(victim);
		if(hFPlayer != null && vFPlayer != null && hFPlayer.hasFaction() && vFPlayer.hasFaction()) {
			Faction hunterFaction = hFPlayer.getFaction();
			Faction victimFaction = vFPlayer.getFaction();
			if(hunterFaction.equals(victimFaction))
				return plugin.getSettings().isFactionsDropFriendly();
		}
		return true;
	}
}
