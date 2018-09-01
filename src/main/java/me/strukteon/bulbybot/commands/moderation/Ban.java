/*
 * Created by Elias on 31.08.18 18:48
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.moderation;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.PermissionsCheck;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class Ban implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        if (!(PermissionsCheck.hasPermission(event, event.getMember(), Permission.BAN_MEMBERS)))
            return;

        if (!(event.getMessage().getMentionedMembers().size() > 0)) {

            event.getTextChannel().sendMessage(
                    new EmbedBuilder()
                        .setColor(Static.COLOR_RED)
                        .setDescription("You must mention at least one member to ban him/her!")
                        .build()
            ).queue();

            return;
        }

        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        Guild guild = event.getGuild();
        int selPosition = guild.getSelfMember().getRoles().get(0).getPosition();
        int authorPosition = event.getMember().getRoles().get(0).getPosition();
        StringBuilder banned = new StringBuilder();
        StringBuilder notBanned = new StringBuilder();

        mentionedMembers.forEach(member -> {

            if (member.getRoles().get(0).getPosition() >= selPosition || member.getRoles().get(0).getPosition() >= authorPosition || member.isOwner()) {
                notBanned.append(" " + member.getEffectiveName());
            }
            else {
                guild.getController().ban(member, 0).reason("Command executed by " + author.getName()).queue();
                banned.append(" " + member.getEffectiveName());
            }

        });

        if (banned.toString().equals("")) {

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setTitle("Ban-List")
                    .setDescription("**Not banned members (in cause of their higher then me/you)**\n"+notBanned);

            channel.sendMessage(eb.build()).queue();

            return;

        }

        if (notBanned.toString().equals("")) {

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setTitle("Ban-List")
                    .setDescription("**Successfully banned members**\n"+banned);

            channel.sendMessage(eb.build()).queue();

            return;

        }

        EmbedBuilder eb = ChatTools.INFO(author)
                .setTitle("Ban-List")
                .setDescription("**Successfully banned members**\n"+banned+"\n\n**Not banned members (in cause of their higher then me/you)**\n"+notBanned);

        channel.sendMessage(eb.build()).queue();

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.BAN_MEMBERS)
                .addRequiredBotPerms(Permission.BAN_MEMBERS);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("ban")
                .setHelp("Bans all mentioned users!")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addElement("user", SyntaxElementType.USER, true)
                );
    }

}