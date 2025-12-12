package nl.phoenixdev.wolvengame.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class DiscordCommandListener extends ListenerAdapter {
    private final DiscordBotMain bot;

    public DiscordCommandListener(DiscordBotMain bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();
        if (!content.startsWith("/")) return;

        String[] args = content.substring(1).split(" ");
        String command = args[0].toLowerCase();

        switch (command) {
            case "mute":
                handleMute(event, args);
                break;
            case "unmute":
                handleUnmute(event, args);
                break;
            case "muteall":
                handleMuteAll(event);
                break;
            case "unmuteall":
                handleUnmuteAll(event);
                break;
            default:
                break;
        }
    }

    private void handleMute(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("❌ Usage: /mute <user>").queue();
            return;
        }

        Guild guild = event.getGuild();
        String memberName = args[1];
        Member member = guild.getMembersByName(memberName, true).stream().findFirst().orElse(null);

        if (member == null) {
            event.getChannel().sendMessage("❌ Member not found").queue();
            return;
        }

        guild.mute(member, true).queue(
            success -> event.getChannel().sendMessage("✅ " + member.getUser().getName() + " is muted").queue(),
            error -> event.getChannel().sendMessage("❌ Failed to mute").queue()
        );
    }

    private void handleUnmute(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("❌ Usage: /unmute <user>").queue();
            return;
        }

        Guild guild = event.getGuild();
        String memberName = args[1];
        Member member = guild.getMembersByName(memberName, true).stream().findFirst().orElse(null);

        if (member == null) {
            event.getChannel().sendMessage("❌ Member not found").queue();
            return;
        }

        guild.mute(member, false).queue(
            success -> event.getChannel().sendMessage("✅ " + member.getUser().getName() + " is unmuted").queue(),
            error -> event.getChannel().sendMessage("❌ Failed to unmute").queue()
        );
    }

    private void handleMuteAll(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        guild.getVoiceChannels().forEach(voiceChannel -> 
            voiceChannel.getMembers().forEach(member -> 
                guild.mute(member, true).queue()
            )
        );
        event.getChannel().sendMessage("✅ All members muted").queue();
    }

    private void handleUnmuteAll(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        guild.getVoiceChannels().forEach(voiceChannel -> 
            voiceChannel.getMembers().forEach(member -> 
                guild.mute(member, false).queue()
            )
        );
        event.getChannel().sendMessage("✅ All members unmuted").queue();
    }
}
