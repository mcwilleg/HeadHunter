package com.neo.headhunter.manager.support.factions;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.util.config.ConfigAccessor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class FactionsMassiveCoreHook extends ConfigAccessor<HeadHunter> implements FactionsHook {
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
				return plugin.getSettings().isFactionsDropHome();
		}
		return true;
	}
	
	@Override
	public boolean isValidZone(Location location) {
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
		if(faction != null) {
			if(faction.getId().equals(Factions.ID_NONE))
				return plugin.getSettings().isFactionsDropWilderness();
			if(faction.getId().equals(Factions.ID_SAFEZONE))
				return plugin.getSettings().isFactionsDropSafezone();
			if(faction.getId().equals(Factions.ID_WARZONE))
				return plugin.getSettings().isFactionsDropWarzone();
		}
		return true;
	}
	
	@Override
	public boolean isValidHunter(Player hunter, Player victim) {
		MPlayer hFPlayer = MPlayer.get(hunter);
		MPlayer vFPlayer = MPlayer.get(victim);
		if(hFPlayer != null && vFPlayer != null && hFPlayer.hasFaction() && vFPlayer.hasFaction()) {
			Faction hunterFaction = hFPlayer.getFaction();
			Faction victimFaction = vFPlayer.getFaction();
			if(hunterFaction.equals(victimFaction))
				return plugin.getSettings().isFactionsDropFriendly();
		}
		return true;
	}
}
