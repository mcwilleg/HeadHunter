package com.neo.headhunter.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Utils {
	public static String getDisplayName(ItemStack item) {
		if(item != null) {
			ItemMeta meta = item.getItemMeta();
			if(meta != null)
				return meta.getDisplayName();
		}
		return null;
	}
}
