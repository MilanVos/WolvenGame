package nl.phoenixdev.wolvengame.model;

import org.bukkit.entity.Player;

public class WolvenPlayer {
    private final Player player;
    private GameRole role;
    private boolean alive;
    private boolean ready;
    private String discordId;

    public WolvenPlayer(Player player) {
        this.player = player;
        this.role = GameRole.NONE;
        this.alive = true;
        this.ready = false;
    }

    public Player getPlayer() {
        return player;
    }

    public GameRole getRole() {
        return role;
    }

    public void setRole(GameRole role) {
        this.role = role;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getName() {
        return player.getName();
    }

    public void reset() {
        this.role = GameRole.NONE;
        this.alive = true;
        this.ready = false;
    }
}
