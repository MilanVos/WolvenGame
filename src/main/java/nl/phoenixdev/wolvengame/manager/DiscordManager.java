package nl.phoenixdev.wolvengame.manager;

import java.util.List;

public interface DiscordManager {
    void notifyGameCreated(String hostName);
    void notifyGameStarted(List<String> playerNames);
    void notifyGameEnded(String winner);
    void mutePlayer(String playerName);
    void unmutePlayer(String playerName);
    void muteAllPlayers();
    void unmuteAllPlayers();
}
