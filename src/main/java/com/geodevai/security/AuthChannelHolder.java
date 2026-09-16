package com.geodevai.security;

/**
 * ThreadLocal holder for the current authentication channel (source) and display name.
 * This allows the auditing listener to know where the update originated from
 * (Web, MCP, Integration, etc.) and who made the change.
 */
public final class AuthChannelHolder {

    private static final ThreadLocal<String> CURRENT_CHANNEL = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_DISPLAY_NAME = new ThreadLocal<>();

    private AuthChannelHolder() {
    }

    public static void setChannel(String channel) {
        CURRENT_CHANNEL.set(channel);
    }

    public static String getChannel() {
        return CURRENT_CHANNEL.get();
    }

    public static void setDisplayName(String displayName) {
        CURRENT_DISPLAY_NAME.set(displayName);
    }

    public static String getDisplayName() {
        return CURRENT_DISPLAY_NAME.get();
    }

    public static void clear() {
        CURRENT_CHANNEL.remove();
        CURRENT_DISPLAY_NAME.remove();
    }
}
