package me.strukteon.bulbybot.commands.dev;
/*
    Created by nils on 26.08.2018 at 01:11.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.commands.money.Register;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class AddMoney implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, syntax.getAsUser("user"), userSQL -> {
            userSQL.addMoney(syntax.getAsInt("amount"));
            EmbedBuilder eb = ChatTools.INFO(author);
            eb.setDescription("You added " + syntax.getAsInt("amount") + "$ to " + syntax.getAsUser("user").getAsMention() + "'s account")
                    .setFooter("This user now has " + userSQL.getMoney() + "$", syntax.getAsUser("user").getEffectiveAvatarUrl());
            channel.sendMessage(eb.build()).queue();
        });
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .limitToUsers(Settings.INSTANCE.developers);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("addmoney")
                .setHelp("Adds money to an users account")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addElement("user", SyntaxElementType.USER)
                                .addElement("amount", SyntaxElementType.INT)
                );
    }
}