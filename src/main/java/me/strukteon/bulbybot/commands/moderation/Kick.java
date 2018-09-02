/*
 * Created by Elias on 01.09.18 13:32
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.moderation;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.PermissionsCheck;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class Kick implements ExtendedGuildCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {

        if (!(PermissionsCheck.hasPermission(event, event.getMember(), Permission.KICK_MEMBERS)))
            return;

        List<Member> mentionedMembers = syntax.getAsListMember("users");
        Guild guild = event.getGuild();
        int selPosition = guild.getSelfMember().getRoles().get(0).getPosition();
        int authorPosition = event.getMember().getRoles().get(0).getPosition();
        StringBuilder kicked = new StringBuilder();
        StringBuilder notKicked = new StringBuilder();

        mentionedMembers.forEach(member -> {

            if (member.getRoles().get(0).getPosition() >= selPosition || member.getRoles().get(0).getPosition() >= authorPosition || member.isOwner()) {
                notKicked.append((notKicked.length() > 0 ? ", " : "") + member.getAsMention());
            }
            else {
                guild.getController().kick(member).reason("Command executed by " + author.getEffectiveName()).queue();
                kicked.append((kicked.length() > 0 ? ", " : "") + member.getAsMention());
            }

        });



        EmbedBuilder eb = ChatTools.INFO(author)
                .setTitle("Kick-List");
        if (notKicked.length() > 0)
            eb.setDescription("**Not kicked members (you/I have no permissions)**\n"+notKicked);
        if (kicked.length() > 0)
            eb.appendDescription((notKicked.length() > 0 ? "\n\n" : "") + "**Successfully kicked members**\n"+kicked);

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
                    .addElement("users", SyntaxElementType.MEMBER, true)
            );
    }
}
