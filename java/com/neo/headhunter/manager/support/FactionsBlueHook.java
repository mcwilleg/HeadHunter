package com.neo.headhunter.manager.support;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import me.zysea.factions.api.FactionsApi;
import me.zysea.factions.faction.FPlayer;
import me.zysea.factions.faction.Faction;
import me.zysea.factions.objects.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsBlueHook extends ConfigAccessor implements FactionsHook {
	public FactionsBlueHook(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	@Override
	public boolean isValidTerritory(Player victim) {
		Faction faction = getFactionAt(victim.getLocation());
		FPlayer factionVictim = FactionsApi.getFPlayer(victim);
		if(faction != null && factionVictim.hasFaction()) {
			Faction ownedFaction = factionVictim.getFaction();
			if(faction.equals(ownedFaction))
				return config.getBoolean(FactionsPath.DROP_HOME, false);
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = getFactionAt(location);
		if(faction != null) {
			if(faction.isWilderness())
				return config.getBoolean(FactionsPath.DROP_WILDERNESS, true);
			if(faction.isSafezone())
				return config.getBoolean(FactionsPath.DROP_SAFEZONE, false);
			if(faction.isWarzone())
				return config.getBoolean(FactionsPath.DROP_WARZONE, true);
		}
		return true;
	}
	
	private Faction getFactionAt(Location location) {
		return FactionsApi.getOwner(new Claim(location));
	}
}
