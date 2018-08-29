package me.strukteon.bettercommand.command;
/*
    Created by nils on 29.07.2018 at 21:03.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public interface Command extends BaseCommand {

    void onExecute(CommandEvent event, String[] args, User author, MessageChannel channel) throws Exception;

}
