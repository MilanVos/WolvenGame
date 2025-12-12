package nl.phoenixdev.wolvengame.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class VoiceManager {
    private final JDA jda;
    private final String guildId;
    private final String voiceChannelId;

    public VoiceManager(JDA jda, String guildId, String voiceChannelId) {
        this.jda = jda;
        this.guildId = guildId;
        this.voiceChannelId = voiceChannelId;
    }

    public void autoJoinVoiceChannel() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            System.err.println("Guild not found!");
            return;
        }

        net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel = guild.getVoiceChannelById(voiceChannelId);
        if (voiceChannel == null) {
            System.err.println("Voice channel not found!");
            return;
        }

        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceChannel);
    }

    public void leaveVoiceChannel() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

    public void mutePlayer(String playerName) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        List<Member> members = guild.getMembersByName(playerName, true);
        for (Member member : members) {
            guild.mute(member, true).queue();
        }
    }

    public void unmutePlayer(String playerName) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        List<Member> members = guild.getMembersByName(playerName, true);
        for (Member member : members) {
            guild.mute(member, false).queue();
        }
    }

    public void muteAllInChannel() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        VoiceChannel voiceChannel = guild.getVoiceChannelById(voiceChannelId);
        if (voiceChannel == null) return;

        for (Member member : voiceChannel.getMembers()) {
            if (!member.getUser().isBot()) {
                guild.mute(member, true).queue();
            }
        }
    }

    public void unmuteAllInChannel() {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        VoiceChannel voiceChannel = guild.getVoiceChannelById(voiceChannelId);
        if (voiceChannel == null) return;

        for (Member member : voiceChannel.getMembers()) {
            if (!member.getUser().isBot()) {
                guild.mute(member, false).queue();
            }
        }
    }

    public void muteDeadPlayers(List<String> deadPlayerNames) {
        for (String playerName : deadPlayerNames) {
            mutePlayer(playerName);
        }
    }
}
