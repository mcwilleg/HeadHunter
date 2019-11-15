package com.neo.headhunter.manager.support;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsMassiveCoreHook extends ConfigAccessor implements FactionsHook {
	public FactionsMassiveCoreHook(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	@Override
	public boolean isValidTerritory(Player victim) {
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(victim.getLocation()));
		MPlayer fPlayer = MPlayer.get(victim);
		if(faction != null && fPlayer != null && fPlayer.hasFaction()) {
			Faction ownedFaction = fPlayer.getFaction();
			if(faction.equals(ownedFaction))
				return config.getBoolean(FactionsPath.DROP_HOME, false);
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
		if(faction != null) {
			if(faction.getId().equals(Factions.ID_NONE))
				return config.getBoolean(FactionsPath.DROP_WILDERNESS, true);
			if(faction.getId().equals(Factions.ID_SAFEZONE))
				return config.getBoolean(FactionsPath.DROP_SAFEZONE, false);
			if(faction.getId().equals(Factions.ID_WARZONE))
				return config.getBoolean(FactionsPath.DROP_WARZONE, true);
		}
		return true;
	}
}
