package nl.phoenixdev.wolvengame.command;

import nl.phoenixdev.wolvengame.manager.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    private final GameManager gameManager;

    public CommandHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDit commando kan alleen door spelers gebruikt worden!");
            return false;
        }

        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "host":
                return handleHostCommand(player, args);
            case "addplayer":
                return handleAddPlayerCommand(player, args);
            case "vote":
                return handleVoteCommand(player);
            case "sethouse":
                return handleSetHouseCommand(player);
            case "setspawn":
                return handleSetSpawnCommand(player);
            case "setroleinfo":
                return handleSetRoleInfoCommand(player, args);
            case "reloadconfig":
                return handleReloadConfigCommand(player);
            default:
                return false;
        }
    }

    private boolean handleHostCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§c/host create|freeze|nacht");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                gameManager.createGame(player);
                return true;
            case "freeze":
                if (!isHost(player)) {
                    player.sendMessage("§cAlleen de host kan dit doen!");
                    return false;
                }
                gameManager.freezePlayers();
                return true;
            case "nacht":
                if (!isHost(player)) {
                    player.sendMessage("§cAlleen de host kan dit doen!");
                    return false;
                }
                gameManager.setNight();
                return true;
            case "kill":
                if (!isHost(player)) {
                    player.sendMessage("§cAlleen de host kan dit doen!");
                    return false;
                }
                if (args.length < 2) {
                    player.sendMessage("§c/host kill <speler>");
                    return false;
                }
                gameManager.killPlayer(args[1]);
                return true;
            default:
                player.sendMessage("§c/host create|freeze|nacht|kill");
                return false;
        }
    }

    private boolean handleAddPlayerCommand(Player player, String[] args) {
        if (!isHost(player)) {
            player.sendMessage("§cAlleen de host kan dit doen!");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage("§c/addplayer <speler>");
            return false;
        }

        Player targetPlayer = player.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage("§cSpeler niet gevonden!");
            return false;
        }

        gameManager.addPlayer(targetPlayer);
        return true;
    }

    private boolean handleVoteCommand(Player player) {
        player.sendMessage("§eStemmingen zijn nog niet geïmplementeerd!");
        return true;
    }

    private boolean handleSetHouseCommand(Player player) {
        gameManager.setHouse(player, player.getLocation());
        return true;
    }

    private boolean handleSetSpawnCommand(Player player) {
        gameManager.setSpawn(player.getLocation());
        player.sendMessage("§aSpawnlocatie ingesteld!");
        return true;
    }

    private boolean handleSetRoleInfoCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c/setroleinfo <rol> <beschrijving>");
            return false;
        }
        player.sendMessage("§aRol informatie opgeslagen!");
        return true;
    }

    private boolean handleReloadConfigCommand(Player player) {
        player.sendMessage("§aConfig herladen!");
        return true;
    }

    private boolean isHost(Player player) {
        return gameManager.getHost() != null && gameManager.getHost().equals(player);
    }
}
