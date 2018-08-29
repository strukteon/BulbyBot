package me.strukteon.bettercommand.command;
/*
    Created by nils on 01.08.2018 at 23:53.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import net.dv8tion.jda.core.entities.MessageEmbed;

public interface BaseCommand {

    default PermissionManager getPermissionManager(){
        return new PermissionManager();
    }

    default MessageEmbed setOverridenHelp(CommandEvent event){
        return null;
    }

    CommandInfo getCommandInfo();

}
