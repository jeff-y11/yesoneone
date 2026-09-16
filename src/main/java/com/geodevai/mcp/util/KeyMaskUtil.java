package com.geodevai.mcp.util;

public class KeyMaskUtil {

    public static String mask(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        int length = key.length();
        if (length <= 10) {
            return key;
        }
        String prefix = key.substring(0, 5);
        String suffix = key.substring(length - 5);
        return prefix + "..." + suffix;
    }
}