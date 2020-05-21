package com.neo.headhunter.util;

import java.text.NumberFormat;
import java.text.ParseException;

public class Utils {
    private static NumberFormat localFormatter = NumberFormat.getInstance();

    public static double valueOf(String valueString) {
        try {
            return localFormatter.parse(valueString).doubleValue();
        } catch(ParseException ex) {
            return 0;
        }
    }
}
