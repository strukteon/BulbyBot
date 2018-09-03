/*
 * Created by Elias on 03.09.18 09:56
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.listeners;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutochannelListener extends ListenerAdapter {

    private List<String> active = new ArrayList<>();


    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

        VoiceChannel vc = event.getChannelJoined();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (vc.getName().contains("⏬")) {
            guild.getController().createCopyOfChannel(vc)
                    .setName("Autochannel")
                    .reason("Created Autochannel for " + member.getEffectiveName())
                    .queue((channel) -> {

                        VoiceChannel nvc = (VoiceChannel) channel;

                        if (nvc.getPermissionOverride(member) == null)
                            nvc.createPermissionOverride(member).setAllow(Permission.MANAGE_CHANNEL).queue();
                        else
                            nvc.putPermissionOverride(member).setAllow(Permission.MANAGE_CHANNEL).queue();

                        if (vc.getParent() != null)
                            nvc.getManager().setParent(vc.getParent()).queue();


                        guild.getController().moveVoiceMember(event.getMember(), nvc).queue((callback) -> {
                            guild.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
                        });
                        active.add(nvc.getId());

                    });


        }

    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {

        VoiceChannel vcJoined = event.getChannelJoined();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (vcJoined.getName().contains("⏬")) {
            guild.getController().createCopyOfChannel(vcJoined)
                    .setName("Autochannel")
                    .reason("Created Autochannel for " + member.getEffectiveName())
                    .queue((channel) -> {

                        VoiceChannel nvc = (VoiceChannel) channel;

                        if (nvc.getPermissionOverride(member) == null)
                            nvc.createPermissionOverride(member).setAllow(Permission.MANAGE_CHANNEL).queue();
                        else
                            nvc.putPermissionOverride(member).setAllow(Permission.MANAGE_CHANNEL).queue();

                        if (vcJoined.getParent() != null)
                            nvc.getManager().setParent(vcJoined.getParent()).queue();

                        guild.getController().moveVoiceMember(event.getMember(), nvc).queue((callback) -> {
                            guild.getController().modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vcJoined.getPosition() + 1).queue();
                        });
                        active.add(nvc.getId());

                    });


        }

        VoiceChannel vcLeft = event.getChannelLeft();

        if (active.contains(vcLeft.getId()) && vcLeft.getMembers().size() == 0) {
            active.remove(vcLeft.getId());
            vcLeft.delete().queue();
        }

    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel vc = event.getChannelLeft();

        if (active.contains(vc.getId()) && vc.getMembers().size() == 0) {
            active.remove(vc.getId());
            vc.delete().queue();
        }
    }

}
