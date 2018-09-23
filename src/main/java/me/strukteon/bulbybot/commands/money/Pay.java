package me.strukteon.bulbybot.commands.money;
/*
    Created by nils on 03.09.2018 at 15:55.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.core.sql.UserSQL;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Pay implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        Register.isRegistered(event, author, userSQL -> {
            UserSQL target = UserSQL.fromUser(syntax.getAsUser("user"));
            EmbedBuilder response = ChatTools.INFO(author);
            if (userSQL.getMoney() < syntax.getAsLong("amount"))
                response.setDescription("You don't have that much money!");
            else if (target == null)
                response.setDescription(Register.getRegisterMessage("", false));
            else {
                target.addMoney(syntax.getAsLong("amount"));
                userSQL.addMoney(-syntax.getAsLong("amount"));
                response.setDescription(String.format("You sent %s coins to **%s**#%s.", syntax.getAsLong("amount"), syntax.getAsUser("user").getName(), syntax.getAsUser("user").getDiscriminator()));
                syntax.getAsUser("user").openPrivateChannel().queue(pc ->
                pc.sendMessage(ChatTools.INFO(syntax.getAsUser("user"))
                .setDescription(String.format("You got %s coins from **%s**#%s", syntax.getAsLong("amount"), author.getName(), author.getDiscriminator())).build()).queue());
            }
            channel.sendMessage(response.build()).queue();
        });
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("pay")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addElement("user", SyntaxElementType.USER)
                                .addElement("amount", SyntaxElementType.LONG)
                );
    }
}
