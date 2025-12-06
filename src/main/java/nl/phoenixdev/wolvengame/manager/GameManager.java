package nl.phoenixdev.wolvengame.manager;

import nl.phoenixdev.wolvengame.model.GameRole;
import nl.phoenixdev.wolvengame.model.GameState;
import nl.phoenixdev.wolvengame.model.WolvenPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameManager {
    private final JavaPlugin plugin;
    private GameState gameState;
    private Player hostPlayer;
    private Map<String, WolvenPlayer> players;
    private Map<String, Location> playerHouses;
    private Location spawnLocation;
    private boolean frozen;
    private DiscordManager discordManager;

    public GameManager(JavaPlugin plugin, DiscordManager discordManager) {
        this.plugin = plugin;
        this.discordManager = discordManager;
        this.gameState = GameState.IDLE;
        this.players = new HashMap<>();
        this.playerHouses = new HashMap<>();
        this.frozen = false;
    }

    public void createGame(Player host) {
        this.hostPlayer = host;
        this.gameState = GameState.WAITING;
        this.players.clear();
        addPlayer(host);
        
        if (discordManager != null) {
            discordManager.notifyGameCreated(host.getName());
        }
        
        host.sendMessage("§aGame gestart! Wacht op spelers.");
    }

    public void addPlayer(Player player) {
        if (!players.containsKey(player.getName())) {
            WolvenPlayer wolvenPlayer = new WolvenPlayer(player);
            players.put(player.getName(), wolvenPlayer);
            player.sendMessage("§aJe bent toegevoegd aan het spel!");
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getName());
    }

    public void startGame() {
        if (gameState != GameState.WAITING) {
            return;
        }

        gameState = GameState.STARTED;
        assignRoles();
        hideNameTags();
        teleportToHouses();

        if (discordManager != null) {
            discordManager.notifyGameStarted(getPlayerNames());
        }

        broadcastMessage("§c=== GAME GESTART ===");
        broadcastMessage("§eJe rol is: §6" + getPlayerRoles());
    }

    private void assignRoles() {
        List<WolvenPlayer> playerList = new ArrayList<>(players.values());
        int wolfCount = Math.max(1, playerList.size() / 3);

        for (int i = 0; i < wolfCount; i++) {
            playerList.get(i).setRole(GameRole.WOLF);
        }

        for (int i = wolfCount; i < playerList.size(); i++) {
            playerList.get(i).setRole(GameRole.VILLAGER);
        }
    }

    private void hideNameTags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (WolvenPlayer wolvenPlayer : players.values()) {
                player.hidePlayer(plugin, wolvenPlayer.getPlayer());
                player.showPlayer(plugin, wolvenPlayer.getPlayer());
            }
        }
    }

    private void teleportToHouses() {
        for (WolvenPlayer wolvenPlayer : players.values()) {
            Location house = playerHouses.get(wolvenPlayer.getName());
            if (house != null) {
                wolvenPlayer.getPlayer().teleport(house);
            }
        }
    }

    public void freezePlayers() {
        frozen = !frozen;
        for (WolvenPlayer wolvenPlayer : players.values()) {
            wolvenPlayer.getPlayer().setFreezeTicks(frozen ? Integer.MAX_VALUE : 0);
        }
        broadcastMessage(frozen ? "§cSpelers zijn bevrozen!" : "§aSpelers zijn ontdooid!");
    }

    public void setNight() {
        gameState = GameState.NIGHT;
        for (WolvenPlayer player : players.values()) {
            if (player.getPlayer().getWorld() != null) {
                player.getPlayer().getWorld().setTime(18000);
            }
        }
        broadcastMessage("§8=== NACHT GEVALLEN ===");
        teleportToHouses();
    }

    public void setDay() {
        gameState = GameState.DAY;
        for (WolvenPlayer player : players.values()) {
            if (player.getPlayer().getWorld() != null) {
                player.getPlayer().getWorld().setTime(0);
            }
        }
        broadcastMessage("§c=== DAG GEBROKEN ===");
    }

    public void startVoting() {
        gameState = GameState.VOTING;
        broadcastMessage("§e=== STEMMEN GESTART ===");
    }

    public void endGame(GameRole winner) {
        gameState = GameState.ENDED;
        broadcastMessage("§6=== SPEL VOORBIJ ===");
        broadcastMessage("§e" + winner.getDisplayName() + " hebben gewonnen!");
        
        if (discordManager != null) {
            discordManager.notifyGameEnded(winner.getDisplayName());
        }

        resetGame();
    }

    public void resetGame() {
        for (WolvenPlayer player : players.values()) {
            player.reset();
        }
        gameState = GameState.IDLE;
        frozen = false;
        hostPlayer = null;
    }

    public void setHouse(Player player, Location location) {
        playerHouses.put(player.getName(), location);
        player.sendMessage("§aHuisje ingesteld!");
    }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
    }

    public void broadcastMessage(String message) {
        for (WolvenPlayer player : players.values()) {
            player.getPlayer().sendMessage(message);
        }
    }

    private String getPlayerRoles() {
        StringBuilder sb = new StringBuilder();
        for (WolvenPlayer player : players.values()) {
            sb.append(player.getName()).append(": ").append(player.getRole().getDisplayName()).append(" ");
        }
        return sb.toString();
    }

    private List<String> getPlayerNames() {
        return players.values().stream()
                .map(WolvenPlayer::getName)
                .collect(Collectors.toList());
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getHost() {
        return hostPlayer;
    }

    public WolvenPlayer getWolvenPlayer(String name) {
        return players.get(name);
    }

    public List<WolvenPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public boolean isFrozen() {
        return frozen;
    }

    public Location getSpawn() {
        return spawnLocation;
    }
}
