package me.strukteon.bettercommand.command;
/*
    Created by nils on 01.08.2018 at 21:17.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.syntax.SyntaxValidateException;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public interface ErrorHandler {

    void missingBotPermissions(CommandEvent event, List<Permission> missingPermissions, BaseCommand command);

    void missingUserPermissions(CommandEvent event, List<Permission> missingPermissions, BaseCommand command);

    void notInUserlist(CommandEvent event, BaseCommand command);

    boolean validateException(CommandEvent event, BaseCommand cmd, SyntaxValidateException e);

    void onException(CommandEvent event, BaseCommand cmd, Exception e);

}
