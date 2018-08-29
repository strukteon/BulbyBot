package me.strukteon.bulbybot.commands.dev;
/*
    Created by nils on 24.08.2018 at 22:22.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class ThrowException implements ExtendedCommand {
    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        throw new Exception(syntax.getAsString("text"));
    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .limitToUsers(Settings.INSTANCE.developers);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("throw")
                .setHelp("Throws a custom exception")
                .setSyntaxBuilder(
                        new SyntaxBuilder()
                                .addOptionalElement("text", SyntaxElementType.STRING)
                );
    }
}
