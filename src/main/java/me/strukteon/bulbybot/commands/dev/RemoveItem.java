package me.strukteon.bulbybot.commands.dev;
/*
    Created by nils on 25.08.2018 at 21:09.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.tools.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.tools.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class RemoveItem implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .limitToUsers(Settings.INSTANCE.developers);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return null;
    }
}
