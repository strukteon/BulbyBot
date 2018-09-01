package me.strukteon.bulbybot.commands.moderation;
/*
    Created by nils on 30.08.2018 at 20:27.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.ExtendedGuildCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

public class ClearReactions implements ExtendedGuildCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception {
        long msgid = syntax.getAsLong("messageid");
        EmbedBuilder response = ChatTools.INFO(author.getUser());
        try {
            channel.getMessageById(msgid).queue(msg -> {
                msg.getReactions().forEach(mr -> mr.getUsers().getReaction().removeReaction().queue());
                response.setDescription("You successfully removed all reactions of the message with the id ``" + msgid + "``.");
                channel.sendMessage(response.build()).queue();
            });
        } catch (ErrorResponseException e){
            response.setDescription("Sorry, but I could not find any message with the id you provided. Please make sure it is in the same channel where you execute this command.");
            channel.sendMessage(response.build()).queue();
        }
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("clearreactions")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                            .addElement("messageid", SyntaxElementType.LONG)
                );
    }
}
