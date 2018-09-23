/*
 * Created by Elias on 31.08.18 18:48
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.moderation;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class Ban implements ExtendedGuildCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {

        List<Member> mentionedMembers = syntax.getAsListMember("users");
        Guild guild = event.getGuild();
        int selPosition = guild.getSelfMember().getRoles().get(0).getPosition();
        int authorPosition = event.getMember().getRoles().get(0).getPosition();
        StringBuilder banned = new StringBuilder();
        StringBuilder notBanned = new StringBuilder();

        mentionedMembers.forEach(member -> {

            if (member.getRoles().get(0).getPosition() >= selPosition || member.getRoles().get(0).getPosition() >= authorPosition || member.isOwner()) {
                notBanned.append((notBanned.length() > 0 ? ", " : "") + member.getAsMention());
            }
            else {
                guild.getController().ban(member, 0).reason("Command executed by " + author.getEffectiveName()).queue();
                banned.append((banned.length() > 0 ? ", " : "") + member.getAsMention());
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
                    .addElement("users", SyntaxElementType.MEMBER, true)
            );
    }
}
