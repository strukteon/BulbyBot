package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 29.08.2018 at 00:09.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class About implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        EmbedBuilder eb = ChatTools.INFO(author)
                .setAuthor("Introducing... me!", Static.PLACEHOLDER_URL, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription("My Website: **Coming soon!**\nVote for me on: [discordbots.org](https://discordbots.org/bot/481221667813589027/vote)\nOur GitHub repo: [github.com](https://github.com/strukteon/BulbyBot)\n\nMy Developers are: **black_wolf**#6549 & **strukteon**#2018\nPublished on: **Not yet :wink: (Open Beta)**\nCreated by: **strukteon** on 22.8.2018");
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("about")
                .setAliases("botinfo", "info")
                .setHelp("Shows you who I am and who made me");
    }
}
