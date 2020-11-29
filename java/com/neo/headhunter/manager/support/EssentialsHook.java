package com.neo.headhunter.manager.support;

import com.earth2me.essentials.Essentials;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EssentialsHook {
	private final Essentials essentials;
	
	public String getCurrencySymbol() {
		return essentials.getSettings().getCurrencySymbol();
	}
}
