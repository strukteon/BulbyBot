package me.strukteon.bulbybot.commands.moderation;
/*
    Created by nils on 02.09.2018 at 01:44.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.GuildSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class Prefix implements ExtendedGuildCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {
        EmbedBuilder response = ChatTools.INFO(author);
        GuildSQL guildSQL = GuildSQL.fromGuild(event.getGuild());
        if (syntax.getExecutedBuilder() == 0){
            response.setDescription("You set this guilds prefix to ``" + syntax.getAsString("prefix") + "``.");
            guildSQL.setPrefix(syntax.getAsString("prefix"));
        } else {
            response.setDescription("This guilds prefix is ``" + guildSQL.getPrefix() + "``. You can change it with ``" + event.getUsedPrefix() + "prefix set <prefix>``.");
        }
        channel.sendMessage(response.build()).queue();
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .addRequiredUserPerms(Permission.MANAGE_SERVER);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("prefix")
                .setHelp("Set a custom prefix for this guild")
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                                .addSubcommand("set", "set")
                                .addElement("prefix", SyntaxElementType.STRING),
                        new SyntaxBuilder()
                ));
    }
}
