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

        List<Member> mentionedMembers = syntax.getAsListMember("users");
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

        

        EmbedBuilder eb = ChatTools.INFO(author)
            .setTitle("Ban-List");
        if (notBanned.length() > 0)
            eb.setDescription("**Not banned members (you/I have no permissions)**\n"+notBanned);
        if (banned.length() > 0)
            eb.appendDescription((notBanned.length() > 0 ? "\n\n" : "") + "**Successfully banned members**\n"+banned);

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
                                .addElement("users", SyntaxElementType.USER, true)
                );
    }

}