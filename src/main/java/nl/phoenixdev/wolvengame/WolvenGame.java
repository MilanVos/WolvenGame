package nl.phoenixdev.wolvengame;

import nl.phoenixdev.wolvengame.command.CommandHandler;
import nl.phoenixdev.wolvengame.manager.DiscordManager;
import nl.phoenixdev.wolvengame.manager.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WolvenGame extends JavaPlugin {

    private GameManager gameManager;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        getLogger().info("§aWolvenGame plugin is aan het inladen...");

        gameManager = new GameManager(this, null);
        commandHandler = new CommandHandler(gameManager);

        registerCommands();

        getLogger().info("§aWolvenGame plugin succesvol ingeladen!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.resetGame();
        }
        getLogger().info("§cWolvenGame plugin is uitgeschakeld!");
    }

    private void registerCommands() {
        getCommand("host").setExecutor(commandHandler);
        getCommand("addplayer").setExecutor(commandHandler);
        getCommand("vote").setExecutor(commandHandler);
        getCommand("sethouse").setExecutor(commandHandler);
        getCommand("setspawn").setExecutor(commandHandler);
        getCommand("setroleinfo").setExecutor(commandHandler);
        getCommand("reloadconfig").setExecutor(commandHandler);
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
