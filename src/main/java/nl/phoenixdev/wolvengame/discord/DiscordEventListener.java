package nl.phoenixdev.wolvengame.discord;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordEventListener extends ListenerAdapter {
    private final DiscordBotMain bot;

    public DiscordEventListener(DiscordBotMain bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null) {
            System.out.println("[Voice] " + event.getMember().getUser().getName() + 
                    " joined " + event.getChannelJoined().getName());
        }
        if (event.getChannelLeft() != null) {
            System.out.println("[Voice] " + event.getMember().getUser().getName() + 
                    " left " + event.getChannelLeft().getName());
        }
    }
}
