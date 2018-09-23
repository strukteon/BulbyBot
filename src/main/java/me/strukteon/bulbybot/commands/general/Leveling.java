package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 04.09.2018 at 16:59.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bulbybot.core.sql.GuildSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class Leveling implements ExtendedGuildCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {
        EmbedBuilder response = ChatTools.INFO(author);
        boolean enabled = syntax.getExecutedBuilder() == 1;
        GuildSQL.fromGuild(event.getGuild()).setLevelingEnabled(enabled);
        response.setDescription(String.format("You %s leveling on this guild.", enabled ? "enabled" : "disabled"));
        channel.sendMessage(response.build()).queue();
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.MANAGE_SERVER);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("leveling")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                            .addSubcommand("mode", "disable", "off"),
                        new SyntaxBuilder()
                            .addSubcommand("mode", "enable", "on")
                ));
    }
}
