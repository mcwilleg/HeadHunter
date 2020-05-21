package com.neo.headhunter.manager.head;

import com.neo.headhunter.HeadHunter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public final class HeadData {
	private HeadHunter plugin;
	private ItemStack head;
	private String ownerString, balanceString, bountyString, dataString;
	private boolean mobHead;
	
	private HeadData(HeadHunter plugin, ItemStack head, String headData) {
		this.plugin = plugin;
		if((head == null) == (headData == null))
			throw new IllegalArgumentException("exactly one argument must not be null");
		if(head != null)
			loadData(head);
		else
			loadData(headData);
		
		// create data string
		String result = String.join(" ", ownerString, balanceString);
		if(bountyString != null)
			result = String.join(" ", result, bountyString);
		this.dataString = result;
		
		// determine mob head
		this.mobHead = plugin.getHeadLibrary().isMobHeadOwner(ownerString);
	}
	
	// constructor for heads being placed
	public HeadData(HeadHunter plugin, ItemStack head) {
		this(plugin, head, null);
	}
	
	// constructor for heads being broken
	public HeadData(HeadHunter plugin, String headData) {
		this(plugin, null, headData);
	}
	
	// called when heads are broken
	public ItemStack getFormattedHead() {
		return head;
	}
	
	// called when heads are placed
	public String getDataString() {
		return dataString;
	}
	
	// called when heads are sold
	public String getOwnerString() {
		return ownerString;
	}
	
	// called when heads are sold
	public String getBalanceString() {
		return balanceString;
	}
	
	// called when heads are sold
	public String getBountyString() {
		return bountyString;
	}
	
	public boolean isMobHead() {
		return mobHead;
	}
	
	// convert head ItemStack into head data String
	private void loadData(ItemStack head) {
		this.head = head;
		ownerString = getOwnerDataString(head);
		ItemMeta meta = head.getItemMeta();
		if(meta != null) {
			List<String> lore = meta.getLore();
			if(lore != null) {
				if(lore.size() >= 1) {
					balanceString = isolateValueString(lore.get(0));
					if(lore.size() == 2)
						bountyString = isolateValueString(lore.get(1));
				}
			}
		}
	}
	
	// convert head data String into head ItemStack
	private void loadData(String headData) {
		String[] split = headData.split(" ");
		if(split.length >= 1) {
			ownerString = split[0];
			if(split.length >= 2) {
				balanceString = split[1];
				if(split.length == 3)
					bountyString = split[2];
			}
		}
		head = plugin.getHeadLibrary().getBaseHeadFromOwner(ownerString);
		head = HeadDrop.format(plugin, head, balanceString, bountyString);
	}
	
	// convert a head into a String representing the head's owner
	@SuppressWarnings("deprecation")
	private String getOwnerDataString(ItemStack head) {
		if(head != null) {
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			if (meta != null) {
				String mobConfigPath = plugin.getHeadLibrary().getConfigPath(head);
				if (mobConfigPath == null) {
					OfflinePlayer owner;
					if (plugin.isVersionBefore(1, 13, 0))
						owner = plugin.getBountyExecutor().getPlayer(meta.getOwner());
					else
						owner = meta.getOwningPlayer();
					if (owner != null)
						return owner.getUniqueId().toString();
				} else
					return mobConfigPath;
			}
		}
		return null;
	}
	
	private String isolateValueString(String lore) {
		lore = ChatColor.stripColor(lore);
		lore = lore.replaceAll(".*\\Q: \\E[^0-9.,%]*", "");
		if(!lore.matches("[0-9]+[.,][0-9]{1,2}[%]?"))
			lore = null;
		return lore;
	}
}
