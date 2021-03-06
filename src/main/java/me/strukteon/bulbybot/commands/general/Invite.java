/*
 * Created by Elias on 30.08.18 18:51
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.general;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Invite implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        EmbedBuilder eb = ChatTools.INFO(author)
            .setAuthor("Invite", Static.PLACEHOLDER_URL, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
            .setDescription("Want to brighten up your server? click [here](https://discordapp.com/oauth2/authorize?client_id=481221667813589027&permissions=8&scope=bot)");
        event.getTextChannel().sendMessage(eb.build()).queue();
        
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("invite")
                .setAliases("i")
                .setHelp("Sends you the invite Link for me!");
    }
}
