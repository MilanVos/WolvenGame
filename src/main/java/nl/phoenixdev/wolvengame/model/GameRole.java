package nl.phoenixdev.wolvengame.model;

import org.bukkit.ChatColor;

public enum GameRole {
    WOLF("Weerwolf", ChatColor.RED),
    VILLAGER("Burger", ChatColor.GREEN),
    SEER("Ziener", ChatColor.BLUE),
    HUNTER("Jager", ChatColor.YELLOW),
    NONE("Geen", ChatColor.GRAY);

    private final String displayName;
    private final ChatColor color;

    GameRole(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }
}
