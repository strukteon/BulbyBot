/*
 * Created by Elias on 01.09.18 13:32
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

public class Kick implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        if (!(PermissionsCheck.hasPermission(event, event.getMember(), Permission.KICK_MEMBERS)))
            return;

        if (!(event.getMessage().getMentionedMembers().size() > 0)) {

            event.getTextChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Static.COLOR_RED)
                            .setDescription("You must mention at least one member to kick him/her!")
                            .build()
            ).queue();

            return;
        }

        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        Guild guild = event.getGuild();
        int selPosition = guild.getSelfMember().getRoles().get(0).getPosition();
        int authorPosition = event.getMember().getRoles().get(0).getPosition();
        StringBuilder kicked = new StringBuilder();
        StringBuilder notKicked = new StringBuilder();

        mentionedMembers.forEach(member -> {

            if (member.getRoles().get(0).getPosition() >= selPosition || member.getRoles().get(0).getPosition() >= authorPosition || member.isOwner()) {
                notKicked.append(" " + member.getEffectiveName());
            }
            else {
                guild.getController().kick(member).reason("Command executed by " + author.getName()).queue();
                kicked.append(" " + member.getEffectiveName());
            }

        });

        if (kicked.toString().equals("")) {

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setTitle("Kick-List")
                    .setDescription("**Not kicked members (in cause of their higher then me/you)**\n"+notKicked);

            channel.sendMessage(eb.build()).queue();

            return;

        }

        if (notKicked.toString().equals("")) {

            EmbedBuilder eb = ChatTools.INFO(author)
                    .setTitle("Kick-List")
                    .setDescription("**Successfully kicked members**\n"+kicked);

            channel.sendMessage(eb.build()).queue();

            return;

        }

        EmbedBuilder eb = ChatTools.INFO(author)
                .setTitle("Kick-List")
                .setDescription("**Successfully kicked members**\n"+kicked+"\n\n**Not kicked members (in cause of their higher then me/you)**\n"+notKicked);

        channel.sendMessage(eb.build()).queue();

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.KICK_MEMBERS)
                .addRequiredBotPerms(Permission.KICK_MEMBERS);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("kick")
            .setHelp("Kicks all mentioned users!")
            .setSyntaxBuilder(
                new SyntaxBuilder()
                    .addElement("user", SyntaxElementType.USER, true)
            );
    }

}