package com.neo.headhunter.manager.support;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.config.ConfigAccessor;
import me.zysea.factions.api.FactionsApi;
import me.zysea.factions.faction.FPlayer;
import me.zysea.factions.faction.Faction;
import me.zysea.factions.objects.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsBlueHook extends ConfigAccessor<HeadHunter> implements FactionsHook {
	public FactionsBlueHook(HeadHunter plugin) {
		super(plugin, true, "config.yml");
	}
	
	@Override
	public boolean isValidTerritory(Player victim) {
		Faction faction = FactionsApi.getOwner(new Claim(victim.getLocation()));
		FPlayer fPlayer = FactionsApi.getFPlayer(victim);
		if(faction != null && fPlayer != null && fPlayer.hasFaction()) {
			Faction ownedFaction = fPlayer.getFaction();
			if(faction.equals(ownedFaction))
				return config.getBoolean(FactionsPath.DROP_HOME, false);
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = FactionsApi.getOwner(new Claim(location));
		if(faction != null) {
			if(faction.isWilderness())
				return config.getBoolean(FactionsPath.DROP_WILDERNESS, true);
			if(faction.isSafezone())
				return config.getBoolean(FactionsPath.DROP_SAFEZONE, false);
			if(faction.isWarzone())
				return config.getBoolean(FactionsPath.DROP_WARZONE, false);
		}
		return true;
	}
	
	@Override
	public boolean isValidHunter(Player hunter, Player victim) {
		FPlayer hFPlayer = FactionsApi.getFPlayer(hunter);
		FPlayer vFPlayer = FactionsApi.getFPlayer(victim);
		if(hFPlayer != null && vFPlayer != null && hFPlayer.hasFaction() && vFPlayer.hasFaction()) {
			Faction hunterFaction = hFPlayer.getFaction();
			Faction victimFaction = vFPlayer.getFaction();
			if(hunterFaction.equals(victimFaction))
				return config.getBoolean(FactionsPath.DROP_FRIENDLY, false);
		}
		return true;
	}
}
