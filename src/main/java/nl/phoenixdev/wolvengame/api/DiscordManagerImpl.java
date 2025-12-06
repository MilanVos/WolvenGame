package nl.phoenixdev.wolvengame.api;

import com.google.gson.JsonObject;
import nl.phoenixdev.wolvengame.manager.DiscordManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DiscordManagerImpl implements DiscordManager {
    private final String apiUrl;
    private final JavaPlugin plugin;

    public DiscordManagerImpl(String apiUrl, JavaPlugin plugin) {
        this.apiUrl = apiUrl;
        this.plugin = plugin;
    }

    @Override
    public void notifyGameCreated(String hostName) {
        JsonObject json = new JsonObject();
        json.addProperty("event", "game_created");
        json.addProperty("host", hostName);
        sendToDiscord("/game/created", json.toString());
    }

    @Override
    public void notifyGameStarted(List<String> playerNames) {
        JsonObject json = new JsonObject();
        json.addProperty("event", "game_started");
        json.add("players", com.google.gson.JsonParser.parseString(
                new com.google.gson.Gson().toJson(playerNames)
        ).getAsJsonArray());
        sendToDiscord("/game/started", json.toString());
    }

    @Override
    public void notifyGameEnded(String winner) {
        JsonObject json = new JsonObject();
        json.addProperty("event", "game_ended");
        json.addProperty("winner", winner);
        sendToDiscord("/game/ended", json.toString());
    }

    @Override
    public void mutePlayer(String playerName) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "mute");
        json.addProperty("player", playerName);
        sendToDiscord("/voice/mute", json.toString());
    }

    @Override
    public void unmutePlayer(String playerName) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "unmute");
        json.addProperty("player", playerName);
        sendToDiscord("/voice/mute", json.toString());
    }

    @Override
    public void muteAllPlayers() {
        JsonObject json = new JsonObject();
        json.addProperty("action", "muteall");
        sendToDiscord("/voice/muteall", json.toString());
    }

    @Override
    public void unmuteAllPlayers() {
        JsonObject json = new JsonObject();
        json.addProperty("action", "unmuteall");
        sendToDiscord("/voice/muteall", json.toString());
    }

    private void sendToDiscord(String endpoint, String jsonData) {
        try {
            URL url = new URL(apiUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                plugin.getLogger().warning("Discord API error: " + responseCode);
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Error sending to Discord: " + e.getMessage());
        }
    }
}
