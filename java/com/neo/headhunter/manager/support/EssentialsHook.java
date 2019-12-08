package com.neo.headhunter.manager.support;

import com.earth2me.essentials.Essentials;
import com.sun.istack.internal.NotNull;

public class EssentialsHook {
	private Essentials essentials;
	
	public EssentialsHook(@NotNull Essentials essentials) {
		this.essentials = essentials;
	}
	
	public String getCurrencySymbol() {
		return essentials.getSettings().getCurrencySymbol();
	}
}
