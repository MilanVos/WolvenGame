package nl.phoenixdev.wolvengame.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getDiscordToken() {
        return config.getString("discord.token", "");
    }

    public String getGuildId() {
        return config.getString("discord.guild-id", "");
    }

    public String getVoiceChannelId() {
        return config.getString("discord.voice-channel-id", "");
    }

    public String getApiUrl() {
        return config.getString("discord.api-url", "http://localhost:8080");
    }

    public int getMaxPlayers() {
        return config.getInt("game.max-players", 20);
    }

    public int getMinPlayers() {
        return config.getInt("game.min-players", 4);
    }

    public double getWolfRatio() {
        return config.getDouble("game.wolf-ratio", 0.33);
    }

    public int getDayDuration() {
        return config.getInt("game.day-duration", 300);
    }

    public int getNightDuration() {
        return config.getInt("game.night-duration", 300);
    }

    public int getVotingDuration() {
        return config.getInt("game.voting-duration", 120);
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "");
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
