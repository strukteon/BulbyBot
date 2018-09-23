/*
 * Created by Elias on 03.09.18 22:35
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.money;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Vote implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        EmbedBuilder eb = ChatTools.INFO(author)
                .setDescription("Vote for me on [discordbots.org](https://discordbots.org/bot/481221667813589027/vote) to get 50 coins, at the weekend even 100!");

        channel.sendMessage(eb.build()).queue();

    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("vote")
                .setHelp("Shows you how to get a reward for voting!");
    }

}
