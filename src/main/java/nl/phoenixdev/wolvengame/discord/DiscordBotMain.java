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

    public DiscordBotMain(String discordToken) {
        this.discordToken = discordToken;
    }

    public void start() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(discordToken)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Weerwolven"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        jda.addEventListener(new DiscordEventListener(this));
        jda.addEventListener(new DiscordCommandListener(this));

        System.out.println("[WolvenGame Discord Bot] Bot is online!");
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public JDA getJDA() {
        return jda;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Discord token required as argument");
            System.exit(1);
        }

        try {
            DiscordBotMain bot = new DiscordBotMain(args[0]);
            bot.start();
        } catch (LoginException e) {
            System.err.println("Invalid Discord token!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
