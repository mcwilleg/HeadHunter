package com.neo.headhunter.manager.support.factions;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.util.config.ConfigAccessor;
import me.zysea.factions.api.FactionsApi;
import me.zysea.factions.faction.FPlayer;
import me.zysea.factions.faction.Faction;
import me.zysea.factions.objects.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class FactionsBlueHook extends ConfigAccessor<HeadHunter> implements FactionsHook {
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
				return plugin.getSettings().isFactionsDropHome();
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = FactionsApi.getOwner(new Claim(location));
		if(faction != null) {
			if(faction.isWilderness())
				return plugin.getSettings().isFactionsDropWilderness();
			if(faction.isSafezone())
				return plugin.getSettings().isFactionsDropSafezone();
			if(faction.isWarzone())
				return plugin.getSettings().isFactionsDropWarzone();
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
				return plugin.getSettings().isFactionsDropFriendly();
		}
		return true;
	}
}
