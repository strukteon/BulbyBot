/*
 * Created by Elias on 03.09.18 10:17
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.moderation;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class Autochannel implements ExtendedGuildCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {

        EmbedBuilder eb = ChatTools.INFO(author)
                .setDescription("Put this emote \\⏬ anywhere in a voicechannel-name and join the channel to create a Autochannel!");

        channel.sendMessage(eb.build()).queue();

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredBotPerms(Permission.MANAGE_CHANNEL)
                .addRequiredUserPerms(Permission.MANAGE_CHANNEL);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("autochannel")
                .setAliases("autochannels")
                .setHelp("Shows you how to create Autochannels.");
    }

}
