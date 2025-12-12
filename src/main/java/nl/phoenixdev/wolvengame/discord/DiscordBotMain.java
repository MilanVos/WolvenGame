package nl.phoenixdev.wolvengame.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class DiscordBotMain {
    private JDA jda;
    private String discordToken;
    private String guildId;
    private String voiceChannelId;
    private VoiceManager voiceManager;
    private BotHttpServer httpServer;

    public DiscordBotMain(String discordToken, String guildId, String voiceChannelId) {
        this.discordToken = discordToken;
        this.guildId = guildId;
        this.voiceChannelId = voiceChannelId;
    }

    public void start() throws LoginException, InterruptedException, java.io.IOException {
        jda = JDABuilder.createDefault(discordToken)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Weerwolven"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        voiceManager = new VoiceManager(jda, guildId, voiceChannelId);
        httpServer = new BotHttpServer(voiceManager);
        httpServer.start(8080);

        jda.addEventListener(new DiscordEventListener(this));
        jda.addEventListener(new DiscordCommandListener(this));

        System.out.println("[WolvenGame Discord Bot] Bot is online!");
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop();
        }
        if (jda != null) {
            jda.shutdown();
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java -jar bot.jar <token> <guildId> <voiceChannelId>");
            System.exit(1);
        }

        try {
            DiscordBotMain bot = new DiscordBotMain(args[0], args[1], args[2]);
            bot.start();
        } catch (LoginException e) {
            System.err.println("Invalid Discord token!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (java.io.IOException e) {
            System.err.println("Failed to start HTTP server!");
            e.printStackTrace();
        }
    }
}
