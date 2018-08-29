package me.strukteon.bettercommand.command;
/*
    Created by nils on 31.07.2018 at 23:51.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.syntax.Syntax;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public interface ExtendedCommand extends BaseCommand {

    void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception;

}
