package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 25.08.2018 at 00:26.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Static;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Balance implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        User user = author;
        if (syntax.getAsMember("user") != null)
            user = syntax.getAsMember("user").getUser();
        User finalUser = user;

        Register.isRegistered(event, user, userSQL -> {
            EmbedBuilder eb = ChatTools.INFO(author);
            eb
                    .setAuthor(finalUser.getName() + "'s balance", Static.PLACEHOLDER_URL, finalUser.getEffectiveAvatarUrl())
                    .setDescription(":moneybag: " + userSQL.getMoney() + " $");
            channel.sendMessage(eb.build()).queue();
        });
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("money")
                .setHelp("Get an update about your account's balance")
                .setAliases("balance", "coins")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addOptionalElement("user", SyntaxElementType.MEMBER)
                );
    }
}
