/*
 * Created by Elias on 31.08.18
 *
 * (c) Elias 2018
 */

package me.strukteon.bulbybot.commands.dev;

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.command.PermissionManager;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Pull implements ExtendedCommand {

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {

        channel.sendMessage("Pulling started!").queue();

        ProcessBuilder builder = new ProcessBuilder("/home/bulbybot.sh");
        builder.start();

        System.exit(0);

    }

    @Override
    public PermissionManager getPermissionManager() {
        return new PermissionManager()
                .limitToUsers(Settings.INSTANCE.developers);
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("pull")
                .setHelp("Pulls the newest version from github!");
    }
}
