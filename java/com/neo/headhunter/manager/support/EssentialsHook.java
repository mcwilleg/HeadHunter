package com.neo.headhunter.manager.support;

import com.earth2me.essentials.Essentials;

public class EssentialsHook {
	private Essentials essentials;
	
	public EssentialsHook(Essentials essentials) {
		this.essentials = essentials;
	}
	
	public String getCurrencySymbol() {
		return essentials.getSettings().getCurrencySymbol();
	}
}
