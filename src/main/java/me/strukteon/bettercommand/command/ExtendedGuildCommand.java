package me.strukteon.bettercommand.command;
/*
    Created by nils on 31.07.2018 at 23:56.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.syntax.Syntax;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public interface ExtendedGuildCommand extends ExtendedCommand, GuildCommand {

    @Override
    default void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) { }

    @Override
    default void onExecute(CommandEvent event, String[] args, Member author, TextChannel channel) { }

    void onExecute(CommandEvent event, Syntax syntax, Member author, TextChannel channel) throws Exception;

}
