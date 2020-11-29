package com.neo.headhunter.util.message;

import com.neo.headhunter.HeadHunter;
import com.neo.headhunter.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public enum Message {
	RELOADED("&e%s &6v%s &ereloaded."),
	PERMISSION("&cYou do not have permission for %s."),
	PLAYERS_ONLY("&cThe %s command is for players only."),
	WORLD_CHECK("&eHead drops are &6%s &ein world &6%s&e."),
	WORLD_ADDED("&eSuccessfully allowed head drops in world &6%s&e."),
	WORLD_REMOVED("&eSuccessfully disabled head drops in world &6%s&e."),
	WORLD_ADD_FAIL("&3Head drops are already allowed in world &b%s&3."),
	WORLD_REMOVE_FAIL("&3Head drops are already disabled in world &b%s&3."),
	BOUNTY_BROADCAST_SET("&6%s&e has set a bounty of &6$%.2f &eon &c%s&e!"),
	BOUNTY_BROADCAST_REMOVE("&6%s&e has removed their bounty of &6$%.2f &efrom &c%s&e."),
	BOUNTY_BROADCAST_CLAIM("&6%s&e has claimed the &6$%.2f &ehead of &c%s&e!"),
	BOUNTY_CLAIM("&eYou have claimed the bounty of &6$%.2f &eon &c%s&e!"),
	BOUNTY_TOTAL("&c%s&e: &6$%.2f"),
	BOUNTY_TOTAL_OWNED("&c%s&e: &6$%.2f (You own: &6$%.2f&e)"),
	BOUNTY_LIST_EMPTY("&cThere are no bounties."),
	BOUNTY_PAGE_EMPTY("&cBounty list page %s is empty."),
	BOUNTY_PAGE_INVALID("&c%s is an invalid bounty list page."),
	BOUNTY_SET("&eSuccessfully set your bounty on &c%s &eto &6$%.2f&e."),
	BOUNTY_SET_AFFORD("&cYou do not have enough money to set a bounty of $%.2f."),
	BOUNTY_SET_SELF("&cYou cannot set a bounty on yourself."),
	BOUNTY_SET_COOLDOWN("&cYou cannot set another bounty for %s."),
	BOUNTY_REMOVED("&eSuccessfully removed your bounty on &c%s&e."),
	BOUNTY_REMOVE_FAIL("&3You do not have a bounty on &b%s&3."),
	BOUNTY_TARGET_INVALID("&c\"%s\" is an invalid bounty target."),
	BOUNTY_AMOUNT_INVALID("&c\"%s\" is an invalid bounty amount."),
	BOUNTY_AMOUNT_LOW("&cThat bounty is too low. Bounties must be at least $%.2f."),
	BOUNTY_SIGN_LINK("&eBounty sign found. Click placed head to link."),
	BOUNTY_LINK_ABORT("&cBounty sign link aborted."),
	BOUNTY_HEAD_LINK("&eBounty sign successfully linked to head."),
	SELL_FAIL("&cYou are not holding any valuable heads."),
	SELL_SINGLE("&eSold &6%s &e(%d) for &6$%.2f&e."),
	SELL_MULTIPLE("&eSold &6%d &eheads for a total of &6$%.2f&e."),
	SELL_SINGLE_BROADCAST("&6%s &esold &6%s &e(%d) for &6$%.2f&e!"),
	SELL_MULTIPLE_BROADCAST("&6%s &esold &6%d &eheads for a total of &6$%.2f&e!");

	@Getter
	private final String message;

	public void send(HeadHunter plugin, CommandSender sender, Object... parameters) {
		sender.sendMessage(this.format(plugin, parameters));
	}

	public void broadcast(HeadHunter plugin, Object... parameters) {
		Bukkit.broadcastMessage(this.format(plugin, parameters));
	}
	
	private String format(HeadHunter plugin, Object...parameters) {
		return ChatColor.translateAlternateColorCodes(
				'&',
				String.format(message, parameters)
		).replace("$", Utils.getCurrencySymbol(plugin));
	}
}
