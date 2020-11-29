package com.neo.headhunter.util;

import com.neo.headhunter.HeadHunter;

import java.text.NumberFormat;
import java.text.ParseException;

public class Utils {
    private static final NumberFormat localFormatter = NumberFormat.getInstance();
    private static final String DEFAULT_CURRENCY = "$";

    public static double valueOf(String valueString) {
        try {
            return localFormatter.parse(valueString).doubleValue();
        } catch(ParseException ex) {
            return 0;
        }
    }

    public static String getCurrencySymbol(HeadHunter plugin) {
        if (plugin.getEssentialsHook() == null) {
            return DEFAULT_CURRENCY;
        } else {
            return plugin.getEssentialsHook().getCurrencySymbol();
        }
    }
}
